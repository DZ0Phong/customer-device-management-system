package com.group5.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.group5.ems.entity.JobPost;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {

    List<JobPost> findByStatus(String status);

    List<JobPost> findByDepartmentId(Long departmentId);

    List<JobPost> findByCreatedBy(Long createdBy);

    int countByStatus(String status);
}

    @Query("SELECT COUNT(DISTINCT j.departmentId) FROM JobPost j")
    long countDistinctDepartment();

    @Query("""
            SELECT COUNT(j)
            FROM JobPost j
            WHERE j.departmentId = :deptId
            """)
    long countByDepartment(Long deptId);
}
