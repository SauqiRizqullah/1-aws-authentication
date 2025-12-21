package com.aws.authportal.specification;

import com.aws.authportal.dtos.SalaryResponse;
import com.aws.authportal.dtos.SearchSalaryRequest;
import com.aws.authportal.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SalarySpecification {
    public static Specification<User> getSpecification (SearchSalaryRequest request){
        return (root, query, criteriaBuilder) ->{

            List<Predicate> predicates = new ArrayList<>();
            if (request.getFullName() != null){
                Predicate fullNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), "%" + request.getFullName().toLowerCase() + "%");
                predicates.add(fullNamePredicate);
            }
            if (request.getRole() != null){
                Predicate rolePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("role")), "%" + request.getRole().toLowerCase() + "%");
                predicates.add(rolePredicate);
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
