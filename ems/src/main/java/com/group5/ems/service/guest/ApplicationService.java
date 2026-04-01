package com.group5.ems.service.guest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.group5.ems.dto.response.ApplicationResponseDTO;
import com.group5.ems.entity.Application;
import com.group5.ems.entity.JobPost;
import com.group5.ems.repository.ApplicationRepository;
import com.group5.ems.repository.JobPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostRepository jobPostRepository;

    public Application applyJob(Long candidateId, Long jobId, Long cvId) {

        Optional<Application> existed =
                applicationRepository.findByJobPostIdAndCandidateId(jobId, candidateId);

        if (existed.isPresent()) {
            return existed.get();
        }

        Application app = new Application();

        app.setCandidateId(candidateId);
        app.setJobPostId(jobId);
        app.setCvId(cvId);

        String token = UUID.randomUUID().toString().replace("-", "");
        app.setTrackingToken(token);

        return applicationRepository.save(app);
    }

    public List<Application> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    public Application findByToken(String token) {
        return applicationRepository
                .findByTrackingToken(token)
                .orElse(null);
    }

    public void deleteApplicationByToken(String token) {

        Application app = applicationRepository
                .findByTrackingToken(token)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        applicationRepository.delete(app);
    }

    public ApplicationResponseDTO trackApplicationDTO(String token) {

        Application app = applicationRepository
                .findByTrackingToken(token)
                .orElse(null);

        if (app == null)
            return null;

        // Lấy job title
        String jobTitle = null;
        if (app.getJobPostId() != null) {
            jobTitle = jobPostRepository
                    .findById(app.getJobPostId())
                    .map(JobPost::getTitle)
                    .orElse(null);
        }

        // Format appliedAt
        String appliedAt = app.getAppliedAt() != null
                ? app.getAppliedAt().toString()
                : null;

        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        dto.setApplicationId(app.getId());
        dto.setCandidateId(app.getCandidateId());
        dto.setJobId(app.getJobPostId());
        dto.setCvId(app.getCvId());
        dto.setTrackingToken(app.getTrackingToken());
        dto.setStatus(app.getStatus() != null ? app.getStatus().toString() : null);
        dto.setAppliedAt(appliedAt);
        dto.setJobPost(new ApplicationResponseDTO.JobPostInfo(jobTitle));

        return dto;
    }
}