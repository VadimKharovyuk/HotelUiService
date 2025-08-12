package com.example.hoteluiservice.config;

import com.example.hoteluiservice.dto.UserInfo;
import com.example.hoteluiservice.util.CurrentUser;
import com.example.hoteluiservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        try {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request == null) {
                log.error("HttpServletRequest is null");
                return null;
            }

            // Извлекаем токен из заголовка Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("No valid Authorization header found");
                return null;
            }

            String token = authHeader.substring(7);

            // Извлекаем данные пользователя из токена
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // Валидируем токен
            if (!jwtUtil.validateToken(token, username)) {
                log.warn("Invalid JWT token for user: {}", username);
                return null;
            }

            // Создаем объект с информацией о пользователе
            return UserInfo.builder()
                    .id(userId)
                    .username(username)
                    .email(email)
                    .role(role)
                    .build();

        } catch (Exception e) {
            log.error("Error resolving current user: {}", e.getMessage());
            return null;
        }
    }
}