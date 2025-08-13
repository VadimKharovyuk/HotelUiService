package com.example.hoteluiservice.client;

import com.example.hoteluiservice.dto.*;
import com.example.hoteluiservice.util.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    @PutMapping("/api/user/profile")
    ResponseEntity<UserDto> updateProfile(
            @RequestHeader("Authorization") String bearerToken,  // передаем токен
            @RequestBody UpdateUserDto updateDto
    );

    @GetMapping("/api/user/profile")
    UserDto getProfile(@RequestHeader("Authorization") String token);

    @PostMapping("/api/auth/login")
    AuthResponse login(@RequestBody LoginRequest request);

    @PostMapping("/api/auth/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest request);



    @GetMapping("/api/admin/users")
    PageResponse<UserDto> getAllUsers(@RequestParam Map<String, Object> params,
                                      @RequestHeader("Authorization") String token);

    @PostMapping("/api/auth/logout")
    ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request);


}
