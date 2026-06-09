package com.group5.ems.dto.response.hrmanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunDTO {
    private Long id;
    private String departmentName;
    private String periodLabel;
    private Integer employeeCount;
    private String totalAmountFormatted;
    private String status;
    private LocalDate dueDate;
}
