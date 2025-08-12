package com.example.hoteluiservice.controller;
import com.example.hoteluiservice.dto.UserDto;
import com.example.hoteluiservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {


    private final UserService userService;


    @GetMapping
    public String dashboard( UserDto user, Model model) {
        // Пользователь автоматически передается через аннотацию
        model.addAttribute("user", user);

        // Можете добавить дополнительную логику
        // model.addAttribute("bookings", bookingService.getUserBookings(user.getId()));
        // model.addAttribute("favorites", favoriteService.getUserFavorites(user.getId()));

        return "dashboard";
    }
}
