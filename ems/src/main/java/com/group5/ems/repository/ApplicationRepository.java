package com.group5.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.group5.ems.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

        List<Application> findByJobPostId(Long jobPostId);

        List<Application> findByCandidateId(Long candidateId);

        Optional<Application> findByJobPostIdAndCandidateId(Long jobPostId, Long candidateId);

        List<Application> findByJobPostIdAndStatus(Long jobPostId, String status);

        int countByStatus(String status);

        Optional<Application> findByTrackingToken(String token);

        void deleteByTrackingToken(String token);

        @EntityGraph(attributePaths = {
                        "candidate",
                        "jobPost",
                        "jobPost.department",
                        "stages"
        })
        @Query("SELECT a FROM Application a")
        Page<Application> findAllWithDetails(Pageable pageable);

        @EntityGraph(attributePaths = {
                        "candidate",
                        "jobPost",
                        "jobPost.department",
                        "stages"
        })
        Optional<Application> findById(Long id);

        long countByJobPostId(Long jobPostId);

        List<Application> findByJobPostIdOrderByAppliedAtDesc(Long jobPostId);

        List<Application> findByCandidateIdOrderByAppliedAtDesc(Long candidateId);
}
