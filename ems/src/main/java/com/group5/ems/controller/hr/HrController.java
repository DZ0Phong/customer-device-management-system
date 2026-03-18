package com.group5.ems.controller.hr;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group5.ems.dto.response.HrDashboardMetricsDTO;
import com.group5.ems.entity.JobPost;
import com.group5.ems.repository.DepartmentRepository;
import com.group5.ems.repository.JobPostRepository;
import com.group5.ems.repository.PositionRepository;
import com.group5.ems.service.hr.HrDashboardService;
import com.group5.ems.service.hr.HrRecruitmentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/hr")
@RequiredArgsConstructor
public class HrController {

    private final HrDashboardService dashboardService;
    private final com.group5.ems.service.hr.HrEmployeeService employeeService;
    private final com.group5.ems.service.hr.HrLeaveService leaveService;
    private final com.group5.ems.service.hr.HrPayrollService payrollService;
    private final com.group5.ems.service.hr.HrAttendanceService attendanceService;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final JobPostRepository jobPostRepository;
    private final HrRecruitmentService recruitmentService;
    private final com.group5.ems.service.hr.HrPerformanceService performanceService;
    private final com.group5.ems.service.hr.HrRequestService requestService;


    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        HrDashboardMetricsDTO metrics = dashboardService.getDashboardMetrics();
        model.addAttribute("activeEmployees", metrics.activeEmployees());
        model.addAttribute("pendingLeave", metrics.pendingLeaveRequests());
        model.addAttribute("openJobs", metrics.openJobPosts());
        model.addAttribute("pendingRequests", metrics.pendingWorkflowRequests());
        return "hr/dashboard";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "hr/employees";
    }

    @GetMapping("/attendance")
    public String attendance(Model model) {
        model.addAttribute("attendances", attendanceService.getAllAttendances());
        return "hr/attendance";
    }

    @GetMapping("/leave")
    public String leave(Model model) {
        HrDashboardMetricsDTO metrics = dashboardService.getDashboardMetrics();
        model.addAttribute("pendingLeave", metrics.pendingLeaveRequests());
        model.addAttribute("pendingRequests", metrics.pendingWorkflowRequests());
        
        model.addAttribute("pendingLeaves", leaveService.getPendingLeaves());
        model.addAttribute("leaveHistory", leaveService.getLeaveHistory());
        
        return "hr/leave";
    }

    @GetMapping("/payroll")
    public String payroll(Model model) {
        model.addAttribute("payslips", payrollService.getAllPayslips());
        return "hr/payroll";
    }


    @GetMapping("/performance")
    public String performance(Model model) {
        return "hr/performance";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        HrDashboardMetricsDTO metrics = dashboardService.getDashboardMetrics();
        model.addAttribute("pendingLeave", metrics.pendingLeaveRequests());
        model.addAttribute("pendingRequests", metrics.pendingWorkflowRequests());
        return "hr/requests";
    }

    // ── Recruitment ────────────────────────────────────────────────────────

    @GetMapping("/recruitment")
    public String recruitment(Model model) {
        model.addAttribute("activeJobs",        recruitmentService.getActiveJobPosts());
        model.addAttribute("totalOpenJobs",     recruitmentService.countOpenJobs());
        model.addAttribute("recentApplicants",  recruitmentService.getRecentApplications());
        model.addAttribute("totalApplications", recruitmentService.countTotalApplications());
        model.addAttribute("departments",       departmentRepository.findAll());
        model.addAttribute("positions",         positionRepository.findAll());
        return "hr/recruitment";
    }

    /**
     * Create a new job post.
     * action  → status OPEN
     * action  → status DRAFT
     */
    @PostMapping("/recruitment/jobs/create")
    public String createJobPost(
            @RequestParam String title,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String requirements,
            @RequestParam(required = false) String benefits,
            @RequestParam(required = false) BigDecimal salaryMin,
            @RequestParam(required = false) BigDecimal salaryMax,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate openDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate closeDate,
            @RequestParam(defaultValue = "draft") String action,
            RedirectAttributes redirectAttributes) {

        JobPost job = new JobPost();
        job.setTitle(title);
        job.setDepartmentId(departmentId);
        job.setPositionId(positionId);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setBenefits(benefits);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);
        job.setOpenDate(openDate != null ? openDate : LocalDate.now());
        job.setCloseDate(closeDate);
        job.setStatus("publish".equals(action) ? "OPEN" : "DRAFT");

        jobPostRepository.save(job);

        String msg = "publish".equals(action)
                ? "Job post \"" + title + "\" published successfully!"
                : "Job post \"" + title + "\" saved as draft.";
        redirectAttributes.addFlashAttribute("successMessage", msg);

        return "redirect:/hr/recruitment";
    }

    @PostMapping("/recruitment/applications/stage")
    public String updateApplicationStage(
            @RequestParam Long applicationId,
            @RequestParam String stage,
            @RequestParam(required = false, defaultValue = "") String note,
            RedirectAttributes redirectAttributes) {

        recruitmentService.updateApplicationStage(applicationId, stage, note);
        redirectAttributes.addFlashAttribute("successMessage",
                "Application moved to stage: " + stage);

        return "redirect:/hr/recruitment";
    }
}
