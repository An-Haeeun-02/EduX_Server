package com.Capstone.EduX;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // API 경로 전체에 대해
                .allowedOrigins("http://localhost:5173",
                        "http://edux-web.s3-website.ap-northeast-2.amazonaws.com",
                        "http://edux-web.com",
                        "https://edux-web.com",
                        "http://www.edux-web.com",
                        "https://www.edux-web.com") //허용할 프론트 주소들
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 요청 헤더 허용
                .allowCredentials(true); // 인증 정보(쿠키 등) 허용
    }
}
