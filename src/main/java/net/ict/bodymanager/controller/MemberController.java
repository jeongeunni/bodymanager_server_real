package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.dto.MemberDTO;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/initial")
@CrossOrigin("*")
public class MemberController {

    @Value("${net.ict.upload.path}")// import 시에 springframework으로 시작하는 Value
    private String uploadPath;

    private final MemberService memberService;

    // 회원가입
    @PostMapping(value = "/join", consumes = {"multipart/form-data"})
    public String join(@ModelAttribute MemberDTO memberDTO) {
        //비동기 통신으로 받아줘야하니까 @RequestBody를 사용
        return memberService.register(memberDTO);
    }

    //이메일 중복확인
    @PostMapping(value = "/emailcheck", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> emailDuple(@RequestBody Member member) {
        return memberService.emailDuple(member);
    }

    //휴대폰 번호 중복체크
    @PostMapping(value = "/phonecheck", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> phoneDuple(@RequestBody Member member) {
       return memberService.phoneDuple(member);
    }

    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {
        return memberService.deleteCookie(req);
    }

    //로그인 유지 - HttpServletRequest 에서 토큰 가져오기
    @GetMapping("/login")
    public String getCookie(HttpServletRequest req) {
        return memberService.getCookie(req);
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user, HttpServletResponse res) {
        return memberService.login(user, res);
    }

    //이메일 찾기
    @PostMapping("/findEmail")
    public String findEmail(@RequestBody Map<String, Object> map) {
        return memberService.findEmail(String.valueOf(map.get("phone")), String.valueOf(map.get("name")));
    }
}