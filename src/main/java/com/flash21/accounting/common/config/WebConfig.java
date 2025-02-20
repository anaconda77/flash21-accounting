package com.flash21.accounting.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash21.accounting.common.util.MultipartJackson2HttpMessageConverter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public List<String> skipPaths() {
        return Arrays.asList(
            "/application/register",
            "/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**"
        );
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public MultipartJackson2HttpMessageConverter multipartJackson2HttpMessageConverter() {
        return new MultipartJackson2HttpMessageConverter(objectMapper());
    }

}
