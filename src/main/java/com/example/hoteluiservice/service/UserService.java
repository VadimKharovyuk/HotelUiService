package com.example.hoteluiservice.service;

import com.example.hoteluiservice.client.UserClient;
import com.example.hoteluiservice.dto.*;

import com.example.hoteluiservice.exception.AuthException;
import com.example.hoteluiservice.exception.UserServiceClientException;
import com.example.hoteluiservice.util.PageResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserClient userClient;


    public void logout(String refreshToken) {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        userClient.logout(request);
    }


    /**
     * Авторизация пользователя
     */
    public AuthResponse login(LoginRequest request) {
        try {
            log.info("Attempting login for user: {}", request.getEmail());
            AuthResponse response = userClient.login(request);
            log.info("Login successful for user: {}", request.getEmail());
            return response;
        } catch (FeignException e) {
            log.error("Login failed for user {}: {}", request.getEmail(), e.getMessage());
            throw new AuthException("Login failed: " + e.getMessage());
        }
    }

    /**
     * Регистрация нового пользователя
     */
    public ResponseEntity<?> register(RegisterRequest request) {
        try {
            log.info("Attempting registration for user: {}", request.getEmail());
            ResponseEntity<?> response = userClient.register(request);
            log.info("Registration successful for user: {}", request.getEmail());
            return response;
        } catch (FeignException e) {
            log.error("Registration failed for user {}: {}", request.getEmail(), e.getMessage());
            throw new UserServiceClientException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Получение профиля текущего пользователя
     */
    public UserDto getCurrentUserProfile(String token) {
        try {
            String bearerToken = addBearerPrefix(token);
            log.info("Getting user profile");
            UserDto profile = userClient.getProfile(bearerToken);
            log.info("Profile retrieved for user: {}", profile.getUsername());
            return profile;
        } catch (FeignException e) {
            log.error("Failed to get user profile: {}", e.getMessage());
            throw new UserServiceClientException("Failed to get profile: " + e.getMessage());
        }
    }


    /**
     * Получение списка всех пользователей с простыми параметрами
     */
    public PageResponse<UserDto> getAllUsers(int page, int size, String token) {
        try {
            String bearerToken = addBearerPrefix(token);
            Map<String, Object> params = buildPageableParams(page, size);

            log.info("Getting all users - page: {}, size: {}", page, size);
            PageResponse<UserDto> response = userClient.getAllUsers(params, bearerToken);
            log.info("Retrieved {} users, total: {}",
                    response.getContent().size(), response.getTotalElements());
            return response;
        } catch (FeignException e) {
            log.error("Failed to get users list: {}", e.getMessage());
            throw new UserServiceClientException("Failed to get users list: " + e.getMessage());
        }
    }

    /**
     * Проверка валидности токена (для JWT фильтров)
     */
    public boolean isTokenValid(String token) {
        try {
            getCurrentUserProfile(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Добавление Bearer префикса к токену
     */
    private String addBearerPrefix(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    /**
     * Преобразование параметров пагинации в Map для Feign
     */
    private Map<String, Object> buildPageableParams(int page, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        return params;
    }
}