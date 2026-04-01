package com.group5.ems.dto.response;

public record HrAttendanceStatsDTO(
    long presentCount,
    long lateCount,
    long leaveCount,
    long absentCount
) {}
