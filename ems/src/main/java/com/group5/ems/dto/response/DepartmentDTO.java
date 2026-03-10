package com.group5.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class DepartmentDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long parentId;
    private String parentName;
    private Long managerId;
    private String managerName;
    private String managerAvatarUrl;
    private Integer staffCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public String getManagerInitials() {
        if (managerName == null || managerName.isBlank()) return "?";
        String[] parts = managerName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
