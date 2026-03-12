package com.group5.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationResponseDTO {

    private Long applicationId;
    private Long candidateId;
    private Long jobId;
    private Long cvId;
    private String trackingToken;
}