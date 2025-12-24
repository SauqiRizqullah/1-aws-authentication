package com.aws.authportal.listener;

import com.aws.authportal.entity.DayOff;
import com.aws.authportal.service.DayOffIdGenerator;
import com.aws.authportal.utils.SpringContext;
import jakarta.persistence.PrePersist;

public class DayOffEntityListener {

    @PrePersist
    public void prePersist(DayOff dayOff) {
        if (dayOff.getId() == null || dayOff.getId().isBlank()) {
            DayOffIdGenerator generator = SpringContext.getBean(DayOffIdGenerator.class);
            String id = generator.generateNextId();
            dayOff.setId(id);
        }
    }
}
