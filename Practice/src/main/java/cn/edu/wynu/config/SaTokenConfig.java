package cn.edu.wynu.config;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600L);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String uri = request.getRequestURI();
                if (uri.startsWith("/auth/login") || uri.startsWith("/auth/register") || uri.startsWith("/statistics/")) {
                    return true;
                }

                if (!StpUtil.isLogin()) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 401);
                    result.put("msg", "未登录，请先登录！");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                    return false;
                }
                return true;
            }
        }).addPathPatterns("/**")
          .excludePathPatterns("/auth/login", "/auth/register", "/h2-console/**");
    }
}
