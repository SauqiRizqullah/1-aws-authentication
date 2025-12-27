package com.aws.authportal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InternalDocumentIdGenerator {
    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public String generateNextId() {
        String prefix = "DOC";
        String query = """
                SELECT COALESCE(MAX(CAST(SUBSTRING(id, 4) AS INTEGER)), 0)
                FROM m_internal_documents
                """;
        Object result = em.createNativeQuery(query).getSingleResult();
        int max = ((Number) result).intValue();
        return prefix + String.format("%03d", max + 1);
    }
}
