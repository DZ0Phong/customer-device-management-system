package com.group5.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationResponseDTO {

    private Long id;
    private Long candidateId;
    private Long jobId;
    private Long cvId;
    private String trackingToken;
}