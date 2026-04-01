package com.group5.ems.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HrApplicantDTO {

    private Long id;
    private Long applicationId;
    private String applicantName;
    private String initials;
    private String email;
    private String phone;
    private String appliedJob;
    private String department;
    private LocalDateTime appliedDate;
    private String appliedDateFormatted;
    private String stage;
    private Integer yearsExperience;
    private BigDecimal expectedSalary;
}