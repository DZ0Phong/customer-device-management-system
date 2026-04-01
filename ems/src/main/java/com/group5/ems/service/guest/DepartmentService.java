package com.group5.ems.service.guest;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group5.ems.entity.Department;
import com.group5.ems.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
