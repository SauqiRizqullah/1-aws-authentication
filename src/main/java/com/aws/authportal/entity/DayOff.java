package com.aws.authportal.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "t_day_offs")
@Getter
@Setter
@Builder
@EntityListeners(com.aws.authportal.listener.DayOffEntityListener.class)
public class DayOff {
    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOffType dayOffType;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    private int totalDays;

    @Column(nullable = false, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    private DayOffStatus status;

    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = true)
    private User approvedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
