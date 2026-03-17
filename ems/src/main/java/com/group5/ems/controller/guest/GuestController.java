package com.group5.ems.controller.guest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group5.ems.dto.request.ApplyJobRequestDTO;
import com.group5.ems.dto.request.ContactRequestDTO;
import com.group5.ems.dto.response.ApplicationResponseDTO;
import com.group5.ems.entity.Department;
import com.group5.ems.entity.JobPost;
import com.group5.ems.service.guest.ApplicationService;
import com.group5.ems.service.guest.CandidateCvService;
import com.group5.ems.service.guest.CompanyService;
import com.group5.ems.service.guest.DepartmentService;
import com.group5.ems.service.guest.EmailService;
import com.group5.ems.service.guest.GuestService;
import com.group5.ems.service.guest.JobPostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;
    private final JobPostService jobPostService;
    private final CompanyService companyService;
    private final CandidateCvService candidateCvService;
    private final ApplicationService applicationService;
    private final EmailService emailService;
    private final DepartmentService departmentService;

    // =============================
    // COMPANY INFO
    // =============================

    @GetMapping("/info")
    public String viewCompanyInfo(Model model) {

        model.addAttribute("info",
                companyService.getPublicCompanyInfo());

        return "guest/company-info";
    }

    // =============================
    // APPLICATIONS
    // =============================

    @GetMapping("/applications/{candidateId}")
    public String viewApplications(
            @PathVariable Long candidateId,
            Model model) {

        model.addAttribute("applications",
                applicationService.getApplicationsByCandidate(candidateId));

        return "guest/applications";
    }

    // =============================
    // APPLY JOB
    // =============================

    @PostMapping(value = "/apply-full", consumes = "multipart/form-data")
    @ResponseBody
    public ApplicationResponseDTO applyFull(
            @ModelAttribute ApplyJobRequestDTO request) throws Exception {

        return guestService.applyJobFullFlow(request);
    }

    @PostMapping("/apply")
    @ResponseBody
    public String applyJob(
            @RequestParam Long candidateId,
            @RequestParam Long jobId,
            @RequestParam Long cvId) {

        applicationService.applyJob(candidateId, jobId, cvId);

        return "ok";
    }

    // =============================
    // CANDIDATE CVS
    // =============================

    @GetMapping("/candidate-cv/{candidateId}")
    public String viewCandidateCv(
            @PathVariable Long candidateId,
            Model model) {

        model.addAttribute("cvs",
                candidateCvService.getCandidateCvs(candidateId));

        return "guest/candidate-cv";
    }

    // =============================
    // HOME
    // =============================

    @GetMapping({ "", "/" })
    public String home(Model model) {

        List<JobPost> jobs = jobPostService.getOpenJobs();

        model.addAttribute("companyNews",
                companyService.getPublicCompanyInfo());

        model.addAttribute("featuredJobs",
                jobs.stream().limit(6).toList());

        model.addAttribute("openCount",
                jobs.size());

        model.addAttribute("deptCount",
                jobPostService.countJobsByDepartment(null));

        return "guest/index";
    }

    // =============================
    // JOB LIST
    // =============================

    @GetMapping("/jobs")
    public String jobs(Model model) {

        List<JobPost> jobs = jobPostService.getOpenJobs();
        List<Department> departments = departmentService.getAllDepartments();

        Map<Long, Long> deptCounts = new HashMap<>();

        for (Department d : departments) {
            deptCounts.put(d.getId(),
                    jobPostService.countJobsByDepartment(d.getId()));
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("departments", departments);
        model.addAttribute("deptCounts", deptCounts);
        model.addAttribute("openCount", jobs.size());

        return "guest/jobs";
    }

    @GetMapping("/jobs/department/{id}")
    public String jobsByDepartment(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("jobs",
                jobPostService.getJobsByDepartment(id));

        model.addAttribute("departments",
                departmentService.getAllDepartments());

        model.addAttribute("openCount",
                jobPostService.getOpenJobs().size());

        return "guest/jobs";
    }

    // =============================
    // JOB DETAIL
    // =============================

    @GetMapping("/jobs/{id}")
    public String jobDetail(
            @PathVariable Long id,
            Model model) {

        JobPost job = jobPostService.getJobDetail(id);

        if (job == null) {
            return "redirect:/guest/jobs";
        }

        model.addAttribute("jobs",
                jobPostService.getOpenJobs());

        model.addAttribute("departments",
                departmentService.getAllDepartments());

        model.addAttribute("openCount",
                jobPostService.getOpenJobs().size());

        model.addAttribute("openJobId", id);

        return "guest/jobs";
    }

    // =============================
    // ABOUT
    // =============================

    @GetMapping("/about")
    public String about(Model model) {

        model.addAttribute("companyInfoList",
                companyService.getPublicCompanyInfo());

        return "guest/about";
    }

    // =============================
    // CONTACT
    // =============================

    @GetMapping("/contact")
    public String contact() {
        return "guest/contact";
    }

    @PostMapping("/contact/send")
    @ResponseBody
    public String sendContact(@RequestBody ContactRequestDTO request) {

        emailService.sendContactEmail(
                request.getSenderName(),
                request.getSenderEmail(),
                request.getSenderPhone(),
                request.getTopic(),
                request.getMessage());

        return "success";
    }

    // =============================
    // TRACK APPLICATION
    // =============================

    @GetMapping("/track/api/{token}")
    @ResponseBody
    public ApplicationResponseDTO trackApplicationApi(
            @PathVariable String token) {

        return applicationService.trackApplicationDTO(token);
    }

    @PostMapping("/application/delete/{token}")
    @ResponseBody
    public String deleteApplication(@PathVariable String token) {

        applicationService.deleteApplicationByToken(token);

        return "deleted";
    }

    // =============================
    // DOWNLOAD CV
    // =============================

//     @GetMapping("/cv/{id}")
//     @ResponseBody
//     public ResponseEntity<byte[]> downloadCv(@PathVariable Long id) {

//         CandidateCv cv = candidateCvService.getCandidateCvById(id);

//         return ResponseEntity.ok()
//                 .header("Content-Disposition",
//                         "attachment; filename=\"" + cv.getFileName() + "\"")
//                 .header("Content-Type", cv.getFileType())
//                 .body(cv.getFileData());
//     }
}