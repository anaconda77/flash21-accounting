package com.flash21.accounting.common.config;

import com.flash21.accounting.common.util.jwt.JWTUtil;
import com.flash21.accounting.common.security.filter.JWTFilter;
import com.flash21.accounting.common.security.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth)-> auth.disable());


        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 설정

        http
                .formLogin((auth)-> auth.disable());

        http
                .httpBasic((auth)-> auth.disable());

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()) // h2 console
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*"); // 모든 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addExposedHeader("Authorization"); //클라이언트에 Authorization header 노출
        configuration.setAllowCredentials(true); // 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

