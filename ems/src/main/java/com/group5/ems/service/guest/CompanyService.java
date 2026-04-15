package com.group5.ems.service.guest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.group5.ems.dto.response.RewardSpotlightDTO;
import com.group5.ems.entity.CompanyInfo;
import com.group5.ems.entity.User;
import com.group5.ems.enums.AuditAction;
import com.group5.ems.enums.AuditEntityType;
import com.group5.ems.repository.CompanyInfoRepository;
import com.group5.ems.repository.RewardDisciplineRepository;
import com.group5.ems.service.common.LogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyInfoRepository companyInfoRepository;
    private final RewardDisciplineRepository rewardDisciplineRepository;
    private final LogService logService;

    // ─── Public list (Home / About) ──────────────────────────────────
    public List<CompanyInfo> getPublicCompanyInfo() {
        return companyInfoRepository.findByIsPublicTrue();
    }

    // ─── All entries (admin) ─────────────────────────────────────────
    public List<CompanyInfo> getAllCompanyInfo() {
        return companyInfoRepository.findAll();
    }

    // ─── Lấy theo key ────────────────────────────────────────────────
    public Optional<CompanyInfo> getByKey(String key) {
        return companyInfoRepository.findByInfoKey(key);
    }

    // ─── Config map cho trang Home ────────────────────────────────────
    public Map<String, String> getHomeConfigMap() {
        List<String> specialKeys = List.of(
                "hero_title", "hero_subtitle",
                "stats_employees", "stats_offices", "stats_founded", "stats_rating",
                "cta_title", "cta_subtitle");
        return companyInfoRepository.findAll()
                .stream()
                .filter(c -> specialKeys.contains(c.getInfoKey()))
                .collect(Collectors.toMap(
                        CompanyInfo::getInfoKey,
                        c -> c.getContent() != null ? c.getContent() : ""));
    }

    // ─── CRUD (admin) ────────────────────────────────────────────────

    /**
     * Tạo mới. Nếu image != null và không rỗng, lưu bytes + contentType vào DB.
     */
    public CompanyInfo createCompanyInfo(
            String infoKey, String title, String content, boolean isPublic,
            MultipartFile image, Long actorId) throws IOException {

        if (companyInfoRepository.existsByInfoKey(infoKey)) {
            throw new IllegalArgumentException("Info key '" + infoKey + "' đã tồn tại.");
        }

        CompanyInfo info = new CompanyInfo();
        info.setInfoKey(infoKey);
        info.setTitle(title);
        info.setContent(content);
        info.setIsPublic(isPublic);
        info.setUpdatedBy(actorId);
        applyImage(info, image);

        CompanyInfo saved = companyInfoRepository.save(info);
        logService.log(AuditAction.CREATE, AuditEntityType.COMPANY, saved.getId());
        return saved;
    }

    /**
     * Cập nhật.
     * - removeImage = true  → xoá blob trong DB.
     * - image có dữ liệu    → thay blob mới.
     * - không có gì         → giữ nguyên blob cũ.
     */
    public CompanyInfo updateCompanyInfo(
            Long id, String title, String content, boolean isPublic,
            MultipartFile image, boolean removeImage, Long actorId) throws IOException {

        CompanyInfo info = companyInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục id=" + id));

        info.setTitle(title);
        info.setContent(content);
        info.setIsPublic(isPublic);
        info.setUpdatedBy(actorId);

        if (removeImage) {
            info.setImageData(null);
            info.setImageType(null);
        } else if (image != null && !image.isEmpty()) {
            applyImage(info, image);
        }
        // else: giữ nguyên blob cũ — không làm gì

        CompanyInfo saved = companyInfoRepository.save(info);
        logService.log(AuditAction.UPDATE, AuditEntityType.COMPANY, saved.getId());
        return saved;
    }

    public void deleteCompanyInfo(Long id) {
        CompanyInfo info = companyInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục id=" + id));
        logService.log(AuditAction.DELETE, AuditEntityType.COMPANY, id);
        companyInfoRepository.delete(info);
    }

    // ─── Helper ──────────────────────────────────────────────────────

    private void applyImage(CompanyInfo info, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return;
        info.setImageData(image.getBytes());
        String ct = image.getContentType();
        info.setImageType(ct != null ? ct : "image/jpeg");
    }

    // ─── Reward spotlight ─────────────────────────────────────────────
    public List<RewardSpotlightDTO> getTopRewards(int limit) {
        return rewardDisciplineRepository
                .findTopRewardsWithEmployee("REWARD")
                .stream()
                .limit(limit)
                .map(r -> {
                    String name     = "Employee";
                    String initials = "EE";
                    String dept     = "EMS Pro";

                    if (r.getEmployee() != null) {
                        User user = r.getEmployee().getUser();
                        if (user != null && user.getFullName() != null) {
                            name = user.getFullName();
                            String[] parts = name.trim().split("\\s+");
                            if (parts.length >= 2) {
                                initials = (parts[0].substring(0, 1)
                                         + parts[parts.length - 1].substring(0, 1))
                                         .toUpperCase();
                            } else {
                                initials = name.substring(0, Math.min(2, name.length()))
                                              .toUpperCase();
                            }
                        }
                        if (r.getEmployee().getDepartment() != null) {
                            dept = r.getEmployee().getDepartment().getName();
                        }
                    }
                    return new RewardSpotlightDTO(
                            name, initials, dept,
                            r.getTitle(), r.getAmount(), r.getDecisionDate());
                })
                .toList();
    }
}