package com.group5.ems.controller.admin;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group5.ems.entity.CompanyInfo;
import com.group5.ems.repository.CompanyInfoRepository;
import com.group5.ems.repository.UserRepository;
import com.group5.ems.service.admin.AdminService;
import com.group5.ems.service.guest.CompanyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/company-info")
@RequiredArgsConstructor
public class CompanyInfoController {

    private final CompanyService companyService;
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final CompanyInfoRepository companyInfoRepository;

    // ─── List ────────────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("companyInfoList", companyService.getAllCompanyInfo());
        adminService.getUserDTO().ifPresent(u -> model.addAttribute("currentUser", u));
        model.addAttribute("departmentCount", adminService.getAllDepartmentsCount());
        return "admin/company-info";
    }

    // ─── Serve image blob from DB ────────────────────────────────────
    /**
     * Endpoint dùng chung cho cả admin và trang Home:
     *   <img th:src="@{/admin/company-info/image/{id}(id=${info.id})}"/>
     *
     * Trả về bytes LONGBLOB với Content-Type đúng.
     * Nếu entry không có ảnh → 404.
     *
     * NOTE: Nếu bạn muốn cả guest truy cập được (trang Home),
     * hãy map thêm /home/company-info/image/{id} hoặc mở permitAll
     * cho pattern /admin/company-info/image/** trong SecurityConfig.
     */
    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> serveImage(@PathVariable Long id) {
        return companyInfoRepository.findById(id)
                .filter(CompanyInfo::hasImage)
                .map(info -> {
                    HttpHeaders headers = new HttpHeaders();
                    String mime = info.getImageType() != null
                            ? info.getImageType() : "image/jpeg";
                    headers.setContentType(MediaType.parseMediaType(mime));
                    // Cache 1 giờ để tránh tải lại mỗi lần
                    headers.setCacheControl("no-cache, no-store, must-revalidate");
                    return new ResponseEntity<>(info.getImageData(), headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── Create ──────────────────────────────────────────────────────
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public String create(
            @RequestParam String infoKey,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "false") boolean isPublic,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra) {

        try {
            Long actorId = resolveUserId(principal);
            companyService.createCompanyInfo(
                    infoKey.trim(), title.trim(), content.trim(), isPublic, image, actorId);
            ra.addFlashAttribute("successMsg", "Successfully created \"" + title + "\".");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/admin/company-info";
    }

    // ─── Update ──────────────────────────────────────────────────────
    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public String update(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "false") boolean isPublic,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra) {

        try {
            Long actorId = resolveUserId(principal);
            companyService.updateCompanyInfo(
                    id, title.trim(), content.trim(), isPublic, image, removeImage, actorId);
            ra.addFlashAttribute("successMsg", "Updated \"" + title + "\".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/admin/company-info";
    }

    // ─── Delete ──────────────────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            companyService.deleteCompanyInfo(id);
            ra.addFlashAttribute("successMsg", "Successfully deleted");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Khong the xoa: " + e.getMessage());
        }
        return "redirect:/admin/company-info";
    }

    private Long resolveUserId(UserDetails principal) {
        if (principal == null) return null;
        return userRepository.findByUsername(principal.getUsername())
                .map(u -> u.getId())
                .orElse(null);
    }
}