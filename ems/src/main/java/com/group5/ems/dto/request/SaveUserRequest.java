package com.group5.ems.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SaveUserRequest {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phone;
    private String status;
}
