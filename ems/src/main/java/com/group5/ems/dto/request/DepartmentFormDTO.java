package com.group5.ems.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class DepartmentFormDTO {
    private Long id;
    private Long parentId;
    private Long managerId;
    private String code;
    private String name;
    private String description;
}
