package net.ict.bodymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.dto.MemberDTO;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.MemberRepository;
import net.ict.bodymanager.util.LocalUploader;
import net.ict.bodymanager.util.S3Uploader;
import org.json.JSONObject;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalUploader localUploader;
    private final S3Uploader s3Uploader;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @Override
    public String join(MemberDTO memberDTO) {
        Member member = dtoToEntity(memberDTO, localUploader, s3Uploader);
        member.changePassword(passwordEncoder.encode(memberDTO.getPassword()));
        memberRepository.save(member);

        JSONObject join = new JSONObject();
        join.put("message", "ok");
        return join.toString();
    }

    //수정 << 미완성
    @Override
    public void modify(MemberDTO memberDTO) {

        Optional<Member> result = memberRepository.findById(memberDTO.getMember_id());

        Member member = result.orElseThrow();

        member.change(memberDTO.getEmail(), memberDTO.getGender());
        memberRepository.save(member);
    }

    //이메일 찾기
    @Override
    public String findEmail(String phone, String name) {
        String email = memberRepository.findByPhoneAndName(phone, name);
        JSONObject object = new JSONObject();
        JSONObject data = new JSONObject();

        object.put("email", email);

        if (email == null || email.equals("")) {
            data.put("message", "not found");

        } else {
            data.put("message", "ok");
            data.put("data", object);
        }
        return data.toString();
    }

    //로그아웃
    @Override
    public String logout(HttpServletRequest req) {
        JSONObject object = new JSONObject();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();

        Cookie[] cookies = req.getCookies(); // 모든 쿠키 가져오기
        for (Cookie c : cookies) {
            String cookiename = c.getName(); // 쿠키 이름 가져오기
            String value = c.getValue(); // 쿠키 값 가져오기
            if (cookiename.equals("X-AUTH-TOKEN")) {
                log.info("여긴 실행됨?");
                String email = jwtTokenProvider.getUserPk(value);
                log.info("UserPk 실행완료");
                memberRepository.deleteToken(email);  //DB에 있는 리프레시 토큰 값 삭제
                c.setMaxAge(0); //X-AUTH-TOKEN 없애기
                object.put("message", "ok");
                response.setHeader("X-AUTH-TOKEN", null);
                log.info("로그이웃 성공");
                return object.toString();
            }
        }
        log.info("로그이웃 실패");
        object.put("message", "not found");
        return object.toString();
    }

    //로그인유지
    @Override
    public String loginning(HttpServletRequest req) {
        JSONObject fir_object = new JSONObject();
        JSONObject sec_object = new JSONObject();
        Cookie[] cookies = req.getCookies(); // 모든 쿠키 가져오기
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

    //로그인
    @Override
    public String login(Map<String, String> user, HttpServletResponse response) {
        JSONObject fir_object = new JSONObject();
        JSONObject sec_object = new JSONObject();

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

            ResponseCookie cookie = jwtTokenProvider.makeCookie(token);
            response.setHeader("Set-Cookie", cookie.toString());
//------
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getUsername());
            response.setHeader("X-AUTH-REFRESH", refreshToken);   //헤더 "X-AUTH-TOKEN" 에 토큰 값 저장하려 했지만 헤더에서 값을 못빼옴 일단 헤더에 넣긴했음

            ResponseCookie refreshCookie = jwtTokenProvider.makeR_Cookie(token);
            response.setHeader("Set-Cookie", refreshCookie.toString());

            memberRepository.updateToken(refreshToken, member.getEmail());  //DB에 저장

            System.out.println("cookie.getName() : " + cookie.getName());
            System.out.println("cookie.getValue() : " + cookie.getValue());
            System.out.println("refreshCookie.getName() : " + refreshCookie.getName());
            System.out.println("refreshCookie.getValue() : " + refreshCookie.getValue());

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
        fir_object.put("message", "not auth");
        return fir_object.toString();
    }

    //이메일 중복확인
    @Override
    public ResponseEntity<Map<String, String>> emailDuple(Member member) {
        memberRepository.existsByEmail(member.getEmail());
        if (memberRepository.existsByEmail(member.getEmail())) {
            Map<String, String> result = Map.of("message", "duplicate");
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> result = Map.of("message", "ok");
            return ResponseEntity.ok(result);
        }
    }

    //휴대폰 중복확인
    @Override
    public ResponseEntity<Map<String, String>> phoneDuple(Member member) {
        memberRepository.existsByPhone(member.getPhone());
        if (memberRepository.existsByPhone(member.getPhone())) {
            Map<String, String> result = Map.of("message", "duplicate");
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> result = Map.of("message", "ok");
            return ResponseEntity.ok(result);
        }
    }

    //회원 삭제
    @Override
    public void remove(Long member_id) {
        memberRepository.deleteById(member_id);
    }
}
