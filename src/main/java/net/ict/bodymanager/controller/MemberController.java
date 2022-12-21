package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.controller.dto.MemberDTO;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.MemberRepository;
import net.ict.bodymanager.service.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/initial")
@CrossOrigin("*")
public class MemberController {

  @Value("${net.ict.upload.path}")// import 시에 springframework으로 시작하는 Value
  private String uploadPath;

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final TokenHandler tokenHandler;


  // 회원가입
  @PostMapping(value = "/join", consumes = {"multipart/form-data"})
  public String join(@ModelAttribute MemberDTO memberDTO) {
    //비동기 통신으로 받아줘야하니까 @RequestBody를 사용
    return memberService.register(memberDTO);
  }

  //이메일 중복확인
  @PostMapping(value = "/emailcheck", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> emailDuple(@RequestBody Member member) {
    memberRepository.existsByEmail(member.getEmail());
    if (memberRepository.existsByEmail(member.getEmail())) {
      Map<String, String> result = Map.of("message", "duplicate");
      return ResponseEntity.ok(result);
    } else {
      Map<String, String> result = Map.of("message", "ok");
      return ResponseEntity.ok(result);
    }
  }

  //휴대폰 번호 중복체크
  @PostMapping(value = "/phonecheck", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> phoneDuple(@RequestBody Member member) {
    memberRepository.existsByPhone(member.getPhone());
    if (memberRepository.existsByPhone(member.getPhone())) {
      Map<String, String> result = Map.of("message", "duplicate");
      return ResponseEntity.ok(result);
    } else {
      Map<String, String> result = Map.of("message", "ok");
      return ResponseEntity.ok(result);
    }
  }



  //로그인 유지
//  //로그인 유지  - 쿠키에서 가져오기
//  @GetMapping("/login")
//  public String isLogin(@CookieValue("X-AUTH-TOKEN") String token) {
//    JSONObject fir_object = new JSONObject();
//    JSONObject sec_object = new JSONObject();
//    //토큰이 없을 때
//    if (token.equals("")) {
//      fir_object.put("message", "not auth");
//      return fir_object.toString();
//    }
//    String email = jwtTokenProvider.getUserPk(token);
//    //일치하는 이메일이 없을 때
//    if (memberRepository.findByEmail(email).isEmpty()) {
//      fir_object.put("message", "not auth");
//      return fir_object.toString();
//    }
//    Member member = memberRepository.findByEmail(email)
//            .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
//    String name = member.getName();
//    String profile = member.getProfile();
//    String type = member.getType();
//    fir_object.put("message", "ok");
//    fir_object.put("data", sec_object);
//    sec_object.put("name", name);
//    sec_object.put("profile", profile);
//    sec_object.put("type", type);
//
//    return fir_object.toString();
//  }

  //로그인 유지 - HttpServletRequest 에서 토큰 가져오기
  @GetMapping("/login")
  public String getCookie(HttpServletRequest req) {
    JSONObject fir_object = new JSONObject();
    JSONObject sec_object = new JSONObject();
    Cookie[] cookies = req.getCookies(); // 모든 쿠키 가져오기
    log.info("++++++++++++++++++Cookies : " +cookies);
    if (cookies != null) {
      for (Cookie c : cookies) {
        String cookiename = c.getName(); // 쿠키 이름 가져오기
        String value = c.getValue(); // 쿠키 값 가져오기
        if (cookiename.equals("X-AUTH-TOKEN")) {
          String email = jwtTokenProvider.getUserPk(value);
          Member member = memberRepository.findByEmail(email)
                  .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
          String name = member.getName();
          String profile = member.getProfile();
          String type = member.getType();
          fir_object.put("message", "ok");
          fir_object.put("data", sec_object);
          sec_object.put("name", name);
          sec_object.put("profile", profile);
          sec_object.put("type", type);
          return fir_object.toString();
        }
      }
    }
    fir_object.put("message", "not auth");
    return fir_object.toString();
  }


  // 로그인
  @PostMapping("/login")
  public String login(@RequestBody Map<String, String> user, HttpServletResponse response) {
    JSONObject fir_object = new JSONObject();
    JSONObject sec_object = new JSONObject();

    System.out.println("=============================login 시작");

    //일치하는 이메일이 없을 때
    if (memberRepository.findByEmail(user.get("email")).isPresent()) {
      Member member = memberRepository.findByEmail(user.get("email"))
              .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
      //일치하는 비밀번호가 없을 때
      if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
        fir_object.put("message", "not auth");
        return fir_object.toString();
      }
      String token = jwtTokenProvider.createToken(member.getUsername(), member.getRoles()); //토큰 생성
      response.setHeader("X-AUTH-TOKEN", token);   //헤더 "X-AUTH-TOKEN" 에 토큰 값 저장하려 했지만 헤더에서 값을 못빼옴 일단 헤더에 넣긴했음
      ResponseCookie cookie = ResponseCookie.from("X-AUTH-TOKEN", token)  //쿠키에 저장
              .maxAge(7 * 24 * 60 * 60)
              .path("/")
              .secure(true)
              .sameSite("None")
              .httpOnly(true)
              .build();
      response.setHeader("Set-Cookie", cookie.toString());

      System.out.println("cookie.getName() : " + cookie.getName());
      System.out.println("cookie.getValue() : " + cookie.getValue());

      String token_email = jwtTokenProvider.getUserPk(token);   //토큰에서 이에일 가져옴

      String name = member.getName();
      String profile = member.getProfile();
      String type = member.getType();

      fir_object.put("message", "ok");
      fir_object.put("data", sec_object);
      sec_object.put("name", name);
      sec_object.put("profile", profile);
      sec_object.put("type", type);

      System.out.println("get-user : " + token_email);
      System.out.println("=============================login 끝");
      return fir_object.toString();
    }
    fir_object.put("message", "not auth");
    return fir_object.toString();
  }

  //이메일 찾기
  @PostMapping("/findEmail")
  public String findEmail(@RequestBody Map<String, Object> map) {
    return memberService.findEmail(String.valueOf(map.get("phone")), String.valueOf(map.get("name")));
  }


  //로그아웃
  @GetMapping("/logout")
  public String logout(@CookieValue("X-AUTH-TOKEN") String token) {
    JSONObject object = new JSONObject();
    if (token.equals("")) {
      object.put("message", "not found");
      return object.toString();
    }
    token = null;
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("로그아웃");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    object.put("message", "ok");
    return object.toString();
  }
}