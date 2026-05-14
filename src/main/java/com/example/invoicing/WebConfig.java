package com.example.invoicing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            	registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://reactweb-mni6.onrender.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
            }
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/api/**")
//                        .allowedOrigins(
//                                "http://localhost:5173",
//                                "https://reactweb-mni6.onrender.com"
//                        )
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                        .allowedHeaders("*")
//                        .exposedHeaders("Location") // if you rely on Location or custom headers
//                        .allowCredentials(true)      // set to true only if you use cookies/auth
//                        .maxAge(3600);               // cache preflight for 1 hour
//            }
        };
    }
}
