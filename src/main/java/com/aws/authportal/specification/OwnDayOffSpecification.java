package com.aws.authportal.specification;

import com.aws.authportal.dtos.SearchDayOffRequest;
import com.aws.authportal.entity.DayOff;
import com.aws.authportal.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OwnDayOffSpecification {
    public static Specification<DayOff> getSpecification(SearchDayOffRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUser() != null && request.getUser().getId() != null) {
                // filter by exact user id (safer and efficient)
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), request.getUser().getId()));
            } else if (request.getUser() != null && request.getUser().getFullName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("fullName")),
                        "%" + request.getUser().getFullName().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }
}
