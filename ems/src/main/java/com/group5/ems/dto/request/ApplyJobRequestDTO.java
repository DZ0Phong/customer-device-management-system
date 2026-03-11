package com.group5.ems.dto.request;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ApplyJobRequestDTO {

    private String fullName;
    private String email;
    private String phone;
    private String address;

    private Integer yearsExperience;
    private BigDecimal expectedSalary;

    private MultipartFile file;
    private Long jobId;
}