package com.group5.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // Template dùng dept.createdAt / dept.updatedAt (formatted string)
    public String getCreatedAt() {
        if (createTime == null) return "";
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getUpdatedAt() {
        if (updateTime == null) return "";
        return updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getManagerInitials() {
        if (managerName == null || managerName.isBlank()) return "?";
        String[] parts = managerName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
