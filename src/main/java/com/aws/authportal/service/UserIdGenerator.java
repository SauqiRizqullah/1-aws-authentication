package com.aws.authportal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserIdGenerator {
    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public String generateNextId() {
        String prefix = "U";
        String query = "SELECT COALESCE(MAX(CAST(SUBSTRING(id, 2) AS INTEGER)), 0) FROM m_users";
        Object result = em.createNativeQuery(query).getSingleResult();
        int max = 0;
        if (result instanceof Number) {
            max = ((Number) result).intValue();
        } else {
            try {
                max = Integer.parseInt(String.valueOf(result));
            } catch (Exception ignored) {
            }
        }
        int next = max + 1;
        return prefix + String.format("%03d", next);
    }
}
