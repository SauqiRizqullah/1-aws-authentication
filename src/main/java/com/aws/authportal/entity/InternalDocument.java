package com.aws.authportal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "m_internal_documents")
@EntityListeners(com.aws.authportal.listener.InternalDocumentEntityListener.class)
public class InternalDocument {

    @Id
    private String id;

    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @Column(name = "file_key", nullable = false)
    private String fileKey; // s3 file key
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "file_size")
    private Long fileSize;


    @Enumerated(EnumType.STRING)
    @Column(name = "document_status", nullable = false)
    private DocumentStatus status; // ACTIVE, ARCHIVED

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}
