package com.aws.authportal.specification;

import com.aws.authportal.dtos.SearchDayOffRequest;
import com.aws.authportal.entity.DayOff;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DayOffSpecification {
    public static Specification<DayOff> getSpecification(SearchDayOffRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUser() != null && request.getUser().getId() != null) {
                // filter by exact user id (safer and efficient)
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), request.getUser().getId()));
            }
            if (request.getUser() != null && request.getUser().getFullName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("fullName")),
                        "%" + request.getUser().getFullName().toLowerCase() + "%"));
            }

            // /all?fullName=
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                predicates.add(
                        root.get("user").get("id").in(request.getUserIds())
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }


}
