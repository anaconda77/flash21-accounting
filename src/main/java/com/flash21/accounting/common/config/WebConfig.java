package com.flash21.accounting.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.common.util.MultipartJackson2HttpMessageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MultipartJackson2HttpMessageConverter multipartJackson2HttpMessageConverter() {
        return new MultipartJackson2HttpMessageConverter(objectMapper());
    }

}
