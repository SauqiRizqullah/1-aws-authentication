package com.aws.authportal.listener;

import com.aws.authportal.entity.InternalDocument;
import com.aws.authportal.entity.User;
import com.aws.authportal.service.InternalDocumentIdGenerator;
import com.aws.authportal.service.UserIdGenerator;
import com.aws.authportal.utils.SpringContext;
import jakarta.persistence.PrePersist;

public class InternalDocumentEntityListener {
    @PrePersist
    public void prePersist(InternalDocument internalDocument) {
        if (internalDocument.getId() == null || internalDocument.getId().isBlank()) {
            InternalDocumentIdGenerator generator = SpringContext.getBean(InternalDocumentIdGenerator.class);
            String id = generator.generateNextId();
            internalDocument.setId(id);
        }
    }
}
