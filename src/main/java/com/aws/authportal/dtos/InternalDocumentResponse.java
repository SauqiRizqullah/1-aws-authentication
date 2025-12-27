package com.aws.authportal.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Builder
public class InternalDocumentResponse {
    private String id;
    private String title;
    private String description;
    private String fileType;
    private Long fileSize;
    private String status;
    private Date createdAt;
}
