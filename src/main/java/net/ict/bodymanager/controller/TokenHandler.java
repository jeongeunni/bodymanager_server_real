package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.MemberRepository;
import org.json.JSONObject;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@RestController
public class TokenHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public Long getIdFromToken() {
        String access_token = "";
        String refresh_token = "";
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
//                .getRequestAttributes()).getRequest();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            String cookiename = c.getName(); // 쿠키 이름 가져오기
            String value = c.getValue(); // 쿠키 값 가져오기

            if (cookiename.equals("X-AUTH-TOKEN")) {
                access_token = value;
            } else if (cookiename.equals("X-AUTH-REFRESH")) {
                refresh_token = value;
            }
        }
        //(CASE 1) access token이 유효하고, refresh token이 유효하다면
        if (jwtTokenProvider.validateToken(access_token) && jwtTokenProvider.validateToken(refresh_token)) {
            log.info("access와 refresh가 유효함");
            String email = jwtTokenProvider.getUserPk(access_token);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
            Long member_id = member.getMember_id();
            String DB_refreshToken = member.getRefreshToken();
            if (DB_refreshToken.equals(refresh_token)) {  //DB에 있는 리프레시 토큰값과 동일하다면
                log.info("멤버아이디 리턴@@@@@@@@@@@@@@@@");
                return member_id;
            } else { //DB에 있는 리프레시 토큰값과 동일하지 않다면
                log.info("access와 refresh가 유효하지만 DB에 있는 refresh와 동일하지 않음");
                response.setHeader("Set-cookie", jwtTokenProvider.deleteCookie());
                response.addHeader("Set-Cookie", jwtTokenProvider.deleteR_Cookie());
            }

            //(CASE 2) access token이 유효하고, refresh token이 유효하지 않다면
        } else if (jwtTokenProvider.validateToken(access_token) && !jwtTokenProvider.validateToken(refresh_token)) {
            log.info("access가 유효하지만 refresh가 유효하지 않음");
            response.setHeader("Set-cookie", jwtTokenProvider.deleteCookie());
            response.addHeader("Set-Cookie", jwtTokenProvider.deleteR_Cookie());

            //(CASE 3) access token이 유효하지 않고, refresh token이 유효하다면
        } else if (!jwtTokenProvider.validateToken(access_token) && jwtTokenProvider.validateToken(refresh_token)) {
            log.info("access 토큰 유효하지 않지만 , refresh 토큰 유효성 검사 성공");
            log.info("새로운 access token 발급");
            String email = jwtTokenProvider.getUserPk(refresh_token);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
            Long member_id = member.getMember_id();
            String DB_refreshToken = member.getRefreshToken();
            if (DB_refreshToken.equals(refresh_token)) {  //DB에 있는 리프레시 토큰값과 동일하다면
                log.info("멤버아이디 리턴@@@@@@@@@@@@@@@@");
                String token = jwtTokenProvider.createToken(member.getUsername(), member.getRoles()); //토큰 생성
                response.setHeader("X-AUTH-TOKEN", token);   //헤더 "X-AUTH-TOKEN" 에 토큰 값 저장하려 했지만 헤더에서 값을 못빼옴 일단 헤더에 넣긴했음
                ResponseCookie cookie = jwtTokenProvider.makeCookie(token);
                response.addHeader("Set-Cookie", cookie.toString());
                return member_id;
            } else { //DB에 있는 리프레시 토큰값과 동일하지 않다면
                log.info("access와 refresh가 유효하지만 DB에 있는 refresh와 동일하지 않음");
                response.setHeader("Set-cookie", jwtTokenProvider.deleteCookie());
                response.addHeader("Set-Cookie", jwtTokenProvider.deleteR_Cookie());
            }

            //(CASE 4)access token이 유효하지 않고, refresh token이 유효하지 않다면
        } else if (!jwtTokenProvider.validateToken(access_token) && !jwtTokenProvider.validateToken(refresh_token)) {
            log.info("access와 refresh가 유효하지 않음");
            response.setHeader("Set-cookie", jwtTokenProvider.deleteCookie());
            response.addHeader("Set-Cookie", jwtTokenProvider.deleteR_Cookie());
        }
        return null;
    }
}

