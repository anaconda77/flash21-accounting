package com.flash21.accounting.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.common.exception.ErrorResponse;
import com.flash21.accounting.common.exception.errorcode.AuthErrorCode;
import com.flash21.accounting.common.exception.errorcode.ErrorCode;
import com.flash21.accounting.common.util.jwt.JWTUtil;
import com.flash21.accounting.common.security.details.CustomUserDetails;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final List<String> skipPaths;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return skipPaths.stream()
            .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    // jwt를 검증하는 filter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        //Authorization 헤더 검증
        if(authorization == null) {
            handleInvalidTokenException(response, AuthErrorCode.NOT_EXISTING_TOKEN);
            return;
        }

        String token = authorization.split(" ")[1];

        // 토큰 유효성 및 소멸 여부 검증
        if(!authorization.startsWith("Bearer ") || !jwtUtil.isValidToken(token)) {
            handleInvalidTokenException(response, AuthErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);


        User user = User.builder()
                .username(username)
                .role(Role.ROLE_ADMIN)
                .password("temp")
                .build();


        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void handleInvalidTokenException(HttpServletResponse response, ErrorCode error) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
            .write(objectMapper.writeValueAsString(
                ErrorResponse.of(error.status(), error.code(), error.message())));
    }
}

