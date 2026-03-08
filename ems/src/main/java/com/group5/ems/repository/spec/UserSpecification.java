package com.group5.ems.repository.spec;

import com.group5.ems.entity.Role;
import com.group5.ems.entity.User;
import com.group5.ems.entity.UserRole;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    //filter cho status
    //root là đại diện cho table được truyền vào từ Specification<?>
    public static Specification<User> hasKeyword(String keyword){
        if(keyword==null||keyword.isBlank()){
            return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        //criteriaBuilder là build condition
        //ở đây return là tìm trong table User(root) với điều kiện full name hoặc email
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("fullName")), pattern),
                cb.like(cb.lower(root.get("email")), pattern)
        );
    }

    public static Specification<User> hasStatus(String statusFilter) {
        return (root, query, cb) -> {
            if (statusFilter == null || statusFilter.isBlank()) {
                return cb.conjunction();
            }
            String dbStatus;
            switch (statusFilter.trim().toLowerCase()) {
                case "active" -> dbStatus = "ACTIVE";
                case "inactive" -> dbStatus = "INACTIVE";
                case "suspended" -> dbStatus = "LOCKED";
                default -> {
                    return cb.conjunction();
                }
            }
            return cb.equal(root.get("status"), dbStatus);
        };
    }

    public static Specification<User> hasRoleCode(String roleCode){
        if(roleCode==null||roleCode.isBlank()){
            return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, cb) -> {
            query.distinct(true); // tránh duplicate khi user có nhiều role
            Join<User, UserRole> userRoleJoin = root.join("userRoles", JoinType.INNER);
            Join<UserRole, Role> roleJoin = userRoleJoin.join("role", JoinType.INNER);
            return cb.equal(roleJoin.get("code"), roleCode.trim());
        };
    }

    public static Specification<User> withFilters(String keyword, String statusFilter, String roleCode){
        return Specification.where(hasKeyword(keyword)).and(hasStatus(statusFilter)).and(hasRoleCode(roleCode));
    }

}
