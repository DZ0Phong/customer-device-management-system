package com.group5.ems.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record HrPerformanceDTO(
    Long id,
    String employeeName,
    String employeeCode,
    String department,
    String reviewerName,
    String status,
    String score,
    String reviewPeriod,
    BigDecimal performanceScore,
    BigDecimal potentialScore,
    String talentMatrix,
    String strengths,
    String areasToImprove,
    LocalDateTime createdAt
) {
}
