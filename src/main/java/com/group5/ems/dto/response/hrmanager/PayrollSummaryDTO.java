package com.group5.ems.dto.response.hrmanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSummaryDTO {
    private Integer pendingCount;
    private String pendingChangeLabel;
    private Boolean pendingChangePositive;
    private String totalValueFormatted;
    private Integer employeesCovered;
}
