package net.ict.bodymanager.handler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.filter.JwtTokenProvider;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
public class CookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        log.info("인터셉터 시작");
        Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
        if (cookies != null) {
            for (Cookie c : cookies) {
                String cookiename = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기

                log.info("cookiename :" + cookiename);

                if (cookiename.equals("X-AUTH-TOKEN")) {
                    response.setHeader("X-AUTH-TOKEN", value);
                    log.info("access value : " + value);
                } else if (cookiename.equals("X-AUTH-REFRESH")) {
                    response.setHeader("X-AUTH-REFRESH", value);
                    log.info("refresh value : " + value);
                }
            }
            log.info("true---");
            return true;
        }
        log.info("false---");
        return false;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object object,
            Exception ex
    ) throws Exception {
    }
}