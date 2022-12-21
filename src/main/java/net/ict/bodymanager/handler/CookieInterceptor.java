package net.ict.bodymanager.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
public class CookieInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
          HttpServletRequest request,
          HttpServletResponse response,
          Object handler
  ) {
    Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
    if (cookies != null) {
      for (Cookie c : cookies) {
        String cookiename = c.getName(); // 쿠키 이름 가져오기
        String value = c.getValue(); // 쿠키 값 가져오기
        if (cookiename.equals("X-ACCESS-TOKEN")) {
          response.setHeader("X-ACCESS-TOKEN", value);

          log.info("response.getHeaderNames() : " + response.getHeaderNames());
          log.info("value" + value);
          return true;
        }
      }
    }
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