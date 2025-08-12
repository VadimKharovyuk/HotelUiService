package com.example.hoteluiservice.controller;
import com.example.hoteluiservice.dto.AuthResponse;
import com.example.hoteluiservice.dto.LoginRequest;
import com.example.hoteluiservice.dto.RegisterRequest;
import com.example.hoteluiservice.exception.AuthException;
import com.example.hoteluiservice.exception.UserServiceClientException;
import com.example.hoteluiservice.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Показать страницу входа
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    /**
     * Обработка входа
     */
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute LoginRequest loginRequest,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {

        // Проверка валидации
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            log.info("Processing login for user: {}", loginRequest.getEmail());
            AuthResponse authResponse = userService.login(loginRequest);

            // Сохраняем токены в cookies
            addTokenCookies(response, authResponse);

            log.info("Login successful for user: {}", loginRequest.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Вход выполнен успешно!");

            return "redirect:/dashboard";

        } catch (AuthException e) {
            log.error("Login failed: {}", e.getMessage());
            model.addAttribute("errorMessage", "Неверный email или пароль");
            return "auth/login";
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage());
            model.addAttribute("errorMessage", "Произошла ошибка. Попробуйте позже.");
            return "auth/login";
        }
    }

    /**
     * Показать страницу регистрации
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    /**
     * Обработка регистрации
     */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute RegisterRequest registerRequest,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Проверка валидации
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            log.info("Processing registration for user: {}", registerRequest.getEmail());
            ResponseEntity<?> response = userService.register(registerRequest);

            log.info("Registration successful for user: {}", registerRequest.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Регистрация успешна! Теперь вы можете войти в систему.");

            return "redirect:/auth/login";

        } catch (UserServiceClientException e) {
            log.error("Registration failed: {}", e.getMessage());

            // Обработка различных ошибок
            if (e.getMessage().contains("email already exists")) {
                model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
            } else if (e.getMessage().contains("username already exists")) {
                model.addAttribute("errorMessage", "Пользователь с таким именем уже существует");
            } else {
                model.addAttribute("errorMessage", "Ошибка регистрации. Проверьте введенные данные.");
            }

            return "auth/register";
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage());
            model.addAttribute("errorMessage", "Произошла ошибка. Попробуйте позже.");
            return "auth/register";
        }
    }

    /**
     * Выход из системы
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // Удаляем cookies с токенами
        removeTokenCookies(response);

        redirectAttributes.addFlashAttribute("successMessage", "Выход выполнен успешно!");
        return "redirect:/auth/login";
    }

    /**
     * Добавление токенов в cookies
     */
    private void addTokenCookies(HttpServletResponse response, AuthResponse authResponse) {
        // Access token cookie
        Cookie accessTokenCookie = new Cookie("accessToken", authResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // true для HTTPS
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60); // 30 минут
        response.addCookie(accessTokenCookie);

        // Refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // true для HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
        response.addCookie(refreshTokenCookie);
    }

    /**
     * Удаление токенов из cookies
     */
    private void removeTokenCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }
}
