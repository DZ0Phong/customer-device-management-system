package com.group5.ems.controller.hrmanager;

import com.group5.ems.dto.request.PeriodCreateReq;
import com.group5.ems.entity.TimesheetPeriod;
import com.group5.ems.exception.PeriodAlreadyLockedException;
import com.group5.ems.exception.PeriodOverlapException;
import com.group5.ems.service.hr.TimesheetPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hrmanager/payroll-periods")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
@RequiredArgsConstructor
public class HRManagerTimesheetPeriodController {

    private static final int DEFAULT_PAGE_SIZE = 12;

    private final TimesheetPeriodService periodService;

    /**
     * List all timesheet periods with pagination.
     */
    @GetMapping
    public String listPeriods(@RequestParam(defaultValue = "0") int page, 
                             Model model) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Page<TimesheetPeriod> periodPage = periodService.getPeriods(pageable);

        model.addAttribute("periods", periodPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", periodPage.getTotalPages());
        model.addAttribute("totalItems", periodPage.getTotalElements());
        model.addAttribute("activePage", "payroll-periods");

        // Empty form DTO for the creation form (only if not already added by redirect)
        if (!model.containsAttribute("periodCreateReq")) {
            model.addAttribute("periodCreateReq", PeriodCreateReq.builder().build());
        }

        return "hrmanager/payroll_periods";
    }

    /**
     * Create a new timesheet period with validation.
     */
    @PostMapping("/create")
    public String createPeriod(@Valid @ModelAttribute("periodCreateReq") PeriodCreateReq req,
                               BindingResult bindingResult,
                               @RequestParam(defaultValue = "0") int page,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Return view with validation errors (field-level + @ValidDateRange)
        if (bindingResult.hasErrors()) {
            Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
            Page<TimesheetPeriod> periodPage = periodService.getPeriods(pageable);
            model.addAttribute("periods", periodPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", periodPage.getTotalPages());
            model.addAttribute("totalItems", periodPage.getTotalElements());
            model.addAttribute("showCreateForm", true);
            model.addAttribute("activePage", "payroll-periods");
            return "hrmanager/payroll_periods";
        }

        // Attempt to create — catch overlap exception and bind it to form
        try {
            periodService.createPeriod(req);
            redirectAttributes.addFlashAttribute("successMessage", "Timesheet period created successfully!");
            return "redirect:/hrmanager/payroll-periods";
        } catch (PeriodOverlapException e) {
            bindingResult.rejectValue("startDate", "error.overlap", e.getMessage());

            Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
            Page<TimesheetPeriod> periodPage = periodService.getPeriods(pageable);
            model.addAttribute("periods", periodPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", periodPage.getTotalPages());
            model.addAttribute("totalItems", periodPage.getTotalElements());
            model.addAttribute("showCreateForm", true);
            model.addAttribute("activePage", "payroll-periods");
            return "hrmanager/payroll_periods";
        }
    }

    /**
     * Lock a timesheet period (irreversible action).
     */
    @PostMapping("/{id}/lock")
    public String lockPeriod(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            periodService.lockPeriod(id);
            redirectAttributes.addFlashAttribute("successMessage", "Timesheet period locked successfully!");
        } catch (PeriodAlreadyLockedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: This period is already locked!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/hrmanager/payroll-periods";
    }
}
