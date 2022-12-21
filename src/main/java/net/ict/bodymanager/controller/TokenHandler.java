package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.MemberRepository;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Log4j2
@RequiredArgsConstructor
@RestController
public class TokenHandler {
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;

    public Long getIdFromToken () {
      log.info("토큰아이디 시작@@@@@@@@@@@@@@@@");
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
              .getRequestAttributes()).getRequest();
      Cookie[] cookies = request.getCookies();
      log.info("쿠키값 받기 시작@@@@@@@@@@@@@@@@@@@@@@@@@@");
      for (Cookie c : cookies) {
        String cookiename = c.getName(); // 쿠키 이름 가져오기
        String value = c.getValue(); // 쿠키 값 가져오기
        if (cookiename.equals("X-AUTH-TOKEN")) {
          if (!jwtTokenProvider.validateToken(value)) {
            log.info("토큰 없음");
            return null;
          }
          String email = jwtTokenProvider.getUserPk(value);
          Member member_find = memberRepository.findByEmail(email)
                  .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
          Long member_id = member_find.getMember_id();
          log.info("멤버아이디 리턴@@@@@@@@@@@@@@@@");
          return member_id;
        } else {
          log.info("토큰 없음");
        }
      }
      return null;
    }
  }

