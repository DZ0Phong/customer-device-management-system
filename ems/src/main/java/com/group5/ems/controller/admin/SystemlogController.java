package com.group5.ems.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class SystemlogController {

    @GetMapping("system-log")
    public String systemlog(){
        return "admin/system-log";
    }
}
