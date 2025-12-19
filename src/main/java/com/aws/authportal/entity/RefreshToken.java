package com.aws.authportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "m_refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private Boolean revoked = false;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    public boolean isRevoked() {
        return Boolean.TRUE.equals(revoked);
    }

    // getters setters lombok dll
}

