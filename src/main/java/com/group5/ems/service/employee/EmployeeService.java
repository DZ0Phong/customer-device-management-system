package com.group5.ems.service.employee;

import com.group5.ems.entity.Employee;
import com.group5.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployeeFromDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentIdWithUser(departmentId);
    }
}
