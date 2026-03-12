package com.group5.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group5.ems.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByEmailAndPhone(String email,String phone);

    List<Candidate> findByEmailContainingOrPhoneContaining(String email, String phone);
}

