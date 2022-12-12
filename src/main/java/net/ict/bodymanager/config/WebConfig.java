package net.ict.bodymanager.config;

import net.ict.bodymanager.handler.CookieInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new CookieInterceptor())
            .addPathPatterns("/**") // 해당 경로에 접근하기 전에 인터셉터가 가로챈다.
            .excludePathPatterns("/initial/**"); // 해당 경로는 인터셉터가 가로채지 않는다.
    System.out.println("WebConfig+++++++++++++++++++++++++++++++");
  }
}