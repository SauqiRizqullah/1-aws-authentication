package com.aws.authportal.listener;

import com.aws.authportal.entity.User;
import com.aws.authportal.service.UserIdGenerator;
import com.aws.authportal.utils.SpringContext;
import jakarta.persistence.PrePersist;

public class UserEntityListener {
    @PrePersist
    public void prePersist(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            UserIdGenerator generator = SpringContext.getBean(UserIdGenerator.class);
            String id = generator.generateNextId();
            user.setId(id);
        }
    }
}
