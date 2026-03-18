package com.group5.ems.service.guest;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.group5.ems.dto.request.ApplyJobRequestDTO;
import com.group5.ems.dto.response.ApplicationResponseDTO;
import com.group5.ems.entity.Application;
import com.group5.ems.entity.Candidate;
import com.group5.ems.entity.CandidateCv;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final CandidateService candidateService;
    private final CandidateCvService cvService;
    private final ApplicationService applicationService;
    private final JobPostService jobPostService;
    private final EmailService emailService;

    public ApplicationResponseDTO applyJobFullFlow(ApplyJobRequestDTO request) throws Exception {

        Candidate candidate = candidateService.createCandidateIfNotExist(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress(),
                request.getDateOfBirth(),
                request.getIntroduction(),
                request.getYearsExperience(),
                request.getExpectedSalary());

        CandidateCv cv = cvService.uploadCv(candidate.getId(), request.getFile());

        Application app = applicationService.applyJob(
                candidate.getId(),
                request.getJobId(),
                cv.getId());

        String jobTitle = jobPostService.getJobTitle(request.getJobId());

        emailService.sendFromTemplate(
                candidate.getEmail(),
                "APPLICATION_CONFIRM",
                Map.of(
                        "fullName", candidate.getFullName(),
                        "jobTitle", jobTitle,
                        "token", app.getTrackingToken()));

        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        dto.setApplicationId(app.getId());
        dto.setCandidateId(app.getCandidateId());
        dto.setJobId(app.getJobPostId());
        dto.setCvId(app.getCvId());
        dto.setTrackingToken(app.getTrackingToken());

        return dto;
    }
}