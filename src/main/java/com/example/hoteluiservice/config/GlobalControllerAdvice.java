package com.example.hoteluiservice.config;

import com.example.hoteluiservice.dto.UserInfo;
import com.example.hoteluiservice.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ModelAttribute
    public void checkAuthorization(@CurrentUser UserInfo userInfo,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {

        // Исключаем публичные страницы из проверки
        String requestURI = request.getRequestURI();
        if (isPublicEndpoint(requestURI)) {
            return;
        }

        if (userInfo == null) {
            log.warn("Unauthorized access attempt to: {}", requestURI);
            response.sendRedirect("/auth/login?error=unauthorized");
        }
    }

    @ModelAttribute("currentUser")
    public UserInfo addUserToModel(@CurrentUser UserInfo userInfo,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {

        String requestURI = request.getRequestURI();

        // Для публичных страниц просто возвращаем userInfo (может быть null)
        if (isPublicEndpoint(requestURI)) {
            return userInfo; // Возвращаем как есть, не редиректим
        }

        if (userInfo == null) {
            log.warn("Unauthorized access attempt to: {}", requestURI);
            response.sendRedirect("/auth/login?error=unauthorized");
            return null;
        }

        return userInfo;
    }



    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/auth/") ||
                uri.startsWith("/public/") ||
                uri.equals("/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/");
    }



}
