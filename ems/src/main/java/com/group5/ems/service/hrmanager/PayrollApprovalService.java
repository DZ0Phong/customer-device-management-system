package com.group5.ems.service.hrmanager;

import com.group5.ems.dto.response.hrmanager.PayrollRunDTO;
import com.group5.ems.dto.response.hrmanager.PayrollSummaryDTO;
import com.group5.ems.entity.Payslip;
import com.group5.ems.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollApprovalService {

    private final PayslipRepository payslipRepository;

    public PayrollSummaryDTO getSummary() {
        // Count pending payslips grouped by department
        List<Payslip> pendingPayslips = payslipRepository.findByStatus("PENDING");
        
        // Group by department to count "runs"
        Map<Long, List<Payslip>> byDepartment = pendingPayslips.stream()
                .filter(p -> p.getEmployee() != null && p.getEmployee().getDepartment() != null)
                .collect(Collectors.groupingBy(p -> p.getEmployee().getDepartment().getId()));
        
        int pendingCount = byDepartment.size();
        
        // Calculate total value
        BigDecimal totalValue = pendingPayslips.stream()
                .map(Payslip::getNetSalary)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Count employees
        int employeesCovered = (int) pendingPayslips.stream()
                .map(Payslip::getEmployeeId)
                .distinct()
                .count();
        
        return PayrollSummaryDTO.builder()
                .pendingCount(pendingCount)
                .pendingChangeLabel("↑ 2 from last month")
                .pendingChangePositive(false)
                .totalValueFormatted(formatCurrency(totalValue))
                .employeesCovered(employeesCovered)
                .build();
    }

    public List<PayrollRunDTO> getPayrollRuns(int page) {
        // Get all pending payslips
        List<Payslip> payslips = payslipRepository.findByStatus("PENDING");
        
        // Group by department
        Map<Long, List<Payslip>> byDepartment = payslips.stream()
                .filter(p -> p.getEmployee() != null && p.getEmployee().getDepartment() != null)
                .collect(Collectors.groupingBy(p -> p.getEmployee().getDepartment().getId()));
        
        // Convert to DTOs
        List<PayrollRunDTO> runs = new ArrayList<>();
        for (Map.Entry<Long, List<Payslip>> entry : byDepartment.entrySet()) {
            List<Payslip> deptPayslips = entry.getValue();
            if (deptPayslips.isEmpty()) continue;
            
            String deptName = deptPayslips.get(0).getEmployee().getDepartment().getName();
            int empCount = deptPayslips.size();
            
            BigDecimal totalAmount = deptPayslips.stream()
                    .map(Payslip::getNetSalary)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            runs.add(PayrollRunDTO.builder()
                    .id(entry.getKey()) // Use department ID as run ID
                    .departmentName(deptName)
                    .periodLabel("Monthly Payroll - " + LocalDate.now().getMonth())
                    .employeeCount(empCount)
                    .totalAmountFormatted(formatCurrency(totalAmount))
                    .status("PENDING_REVIEW")
                    .dueDate(LocalDate.now().plusDays(5))
                    .build());
        }
        
        return runs;
    }

    public Map<String, Object> getPagination(int page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page);
        pagination.put("totalPages", 2);
        pagination.put("totalItems", 12);
        pagination.put("startItem", (page - 1) * 10 + 1);
        pagination.put("endItem", Math.min(page * 10, 12));
        return pagination;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "$0.00";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }
}
