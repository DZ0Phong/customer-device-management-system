package com.group5.ems.controller.admin;

import com.group5.ems.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DepartmentController {

    private final AdminService adminService;

    @GetMapping("/departments")
    public String department(Model model) {
        return "admin/department";
    }
}
