package com.example.hoteluiservice.controller;

import com.example.hoteluiservice.dto.UpdateUserDto;
import com.example.hoteluiservice.dto.UserDto;
import com.example.hoteluiservice.dto.UserInfo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String userProfile(Model model, @CurrentUser UserInfo currentUser, HttpServletRequest request) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        String token = tokenService.getTokenFromCookies(request);
        UserDto profile = userService.getCurrentUserProfile(token);

        UpdateUserDto updateUserDto = userMapper.toUpdateUserDto(profile);

        model.addAttribute("user", profile);
        model.addAttribute("updateUserDto", updateUserDto);

        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @CurrentUser UserInfo currentUser,
            HttpServletRequest request,
            @ModelAttribute @Valid UpdateUserDto updateUserDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

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

            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен");
            return "redirect:/profile";

        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка обновления профиля: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}