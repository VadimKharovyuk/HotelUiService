package com.example.hoteluiservice.controller;
import com.example.hoteluiservice.dto.UserDto;
import com.example.hoteluiservice.dto.UserInfo;
import com.example.hoteluiservice.service.UserService;
import com.example.hoteluiservice.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {


    private final UserService userService;

    @GetMapping
    public String dashboard(@CurrentUser UserInfo userInfo, Model model) {

        // Проверяем авторизацию
        if (userInfo == null) {
            log.warn("Unauthorized access attempt to dashboard");
            return "redirect:/auth/login?error=unauthorized";
        }


        model.addAttribute("user", userInfo);
        return "dashboard";
    }
}
