package com.example.hoteluiservice.controller;

import com.example.hoteluiservice.dto.UpdateUserDto;
import com.example.hoteluiservice.dto.UserDto;
import com.example.hoteluiservice.dto.UserInfo;
import com.example.hoteluiservice.exception.InvalidUserDataException;
import com.example.hoteluiservice.mapper.UserMapper;
import com.example.hoteluiservice.service.TokenService;
import com.example.hoteluiservice.service.UserService;
import com.example.hoteluiservice.util.CurrentUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/profile")
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    @GetMapping
    public String userProfile(Model model,
                              @CurrentUser UserInfo currentUser,
                              HttpServletRequest request,
                              @RequestParam(required = false) String success,
                              @RequestParam(required = false) String error) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        String token = tokenService.getTokenFromCookies(request);
        UserDto profile = userService.getCurrentUserProfile(token);

        UpdateUserDto updateUserDto = userMapper.toUpdateUserDto(profile);

        model.addAttribute("user", profile);
        model.addAttribute("updateUserDto", updateUserDto);

        // Обрабатываем сообщения из URL параметров
        if ("updated".equals(success)) {
            model.addAttribute("successMessage", "Профиль успешно обновлен");
        }
        if ("validation_error".equals(error)) {
            model.addAttribute("errorMessage", "Ошибка валидации данных");
        }
        if ("email_exists".equals(error)) {
            model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
        }

        if ("service_error".equals(error)) {
            model.addAttribute("errorMessage", "Ошибка сервиса. Попробуйте позже");
        }

        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @CurrentUser UserInfo currentUser,
            HttpServletRequest request,
            @ModelAttribute @Valid UpdateUserDto updateUserDto,
            BindingResult bindingResult,
            Model model) {

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Если есть ошибки валидации, возвращаем форму с ошибками
        if (bindingResult.hasErrors()) {
            String token = tokenService.getTokenFromCookies(request);
            UserDto profile = userService.getCurrentUserProfile(token);
            model.addAttribute("user", profile);
            model.addAttribute("errorMessage", "Пожалуйста, исправьте ошибки в форме");
            return "user/profile";
        }

        try {
            String token = tokenService.getTokenFromCookies(request);
            UserDto updatedUser = userService.updateUserProfile(token, updateUserDto);

            // Redirect с параметром успеха
            return "redirect:/profile?success=updated";

        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return "redirect:/profile?error=service_error";
        }
    }
}