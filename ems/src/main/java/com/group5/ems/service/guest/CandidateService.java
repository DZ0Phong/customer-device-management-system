package com.group5.ems.service.guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.group5.ems.entity.Candidate;
import com.group5.ems.repository.CandidateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Candidate> searchCandidate(String email, String phone) {
        return candidateRepository
                .findByEmailContainingOrPhoneContaining(email, phone);
    }

    public Candidate createCandidateIfNotExist(
            String fullName,
            String email,
            String phone,
            String address,
            LocalDate dateOfBirth,
            String introduction,
            Integer yearsExperience,
            BigDecimal expectedSalary) {

        Optional<Candidate> existed =
                candidateRepository.findByEmailAndPhone(email, phone);

        if (existed.isPresent()) {
            return existed.get();
        }

        Candidate candidate = new Candidate();

        candidate.setFullName(fullName);
        candidate.setEmail(email);
        candidate.setPhone(phone);
        candidate.setAddress(address);
        candidate.setDateOfBirth(dateOfBirth);
        candidate.setIntroduction(introduction);
        candidate.setYearsExperience(yearsExperience);
        candidate.setExpectedSalary(expectedSalary);

        return candidateRepository.save(candidate);
    }
}