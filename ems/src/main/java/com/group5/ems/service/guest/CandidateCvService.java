package com.group5.ems.service.guest;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.group5.ems.entity.CandidateCv;
import com.group5.ems.repository.CandidateCvRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateCvService {

    private final CandidateCvRepository candidateCvRepository;

    public CandidateCv uploadCv(Long candidateId, MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        CandidateCv cv = new CandidateCv();

        cv.setCandidateId(candidateId);
        cv.setFileName(file.getOriginalFilename());
        cv.setFileType(file.getContentType());
        cv.setFileData(file.getBytes());

        return candidateCvRepository.save(cv);
    }

    public List<CandidateCv> getCandidateCvs(Long candidateId) {
        return candidateCvRepository.findByCandidateId(candidateId);
    }
}