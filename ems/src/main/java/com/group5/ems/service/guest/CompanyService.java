package com.group5.ems.service.guest;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group5.ems.entity.CompanyInfo;
import com.group5.ems.repository.CompanyInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyInfoRepository companyInfoRepository;

    public List<CompanyInfo> getPublicCompanyInfo() {
        return companyInfoRepository.findByIsPublicTrue();
    }
}