package com.example.hoteluiservice.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String role;
}
