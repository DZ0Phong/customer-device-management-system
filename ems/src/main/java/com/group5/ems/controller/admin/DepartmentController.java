package com.group5.ems.controller.admin;

import com.group5.ems.dto.request.DepartmentFormDTO;
import com.group5.ems.dto.response.DepartmentDTO;
import com.group5.ems.dto.response.UserDTO;
import com.group5.ems.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DepartmentController {

    private final AdminService adminService;

    @GetMapping("/departments")
    public String department(@RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "name") String sort,
                             @RequestParam(defaultValue = "asc") String dir,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int pageSize,
                             Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        model.addAttribute("statTotal", adminService.getAllDepartmentsCount());
        model.addAttribute("statTotalStaff", adminService.getAllEmployeesCount());
        model.addAttribute("statWithChildren", adminService.getAllParents());

        Page<DepartmentDTO> pg = adminService.getDepartmentsFilter(keyword,sort,dir,page,pageSize);
        model.addAttribute("departments", pg);
        model.addAttribute("currentPage", pg.getNumber());
        model.addAttribute("totalPages", pg.getTotalPages());
        model.addAttribute("totalItems", pg.getTotalElements());
        model.addAttribute("pageSize", pg.getSize());

        model.addAttribute("deptForm", new DepartmentFormDTO());
        List<DepartmentDTO> allDepts = adminService.getAllDepartmentsDTO();
        model.addAttribute("allDepartments", allDepts);

        List<UserDTO> allManagers = adminService.getAllManagersForSelect();
        model.addAttribute("allManagers", allManagers);

        adminService.getUserDTO().ifPresent(u -> model.addAttribute("currentUser", u));

        return "admin/department";
    }


}
