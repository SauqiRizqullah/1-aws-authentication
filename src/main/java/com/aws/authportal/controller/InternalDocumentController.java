package com.aws.authportal.controller;

import com.aws.authportal.dtos.InternalDocumentResponse;
import com.aws.authportal.service.InternalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/documents")
public class InternalDocumentController {

    private final InternalDocumentService internalDocumentService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<InternalDocumentResponse> uploadDocument(
            @RequestPart("file")MultipartFile file,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description", required = false) String description
            )
    {
        InternalDocumentResponse response =internalDocumentService.uploadDocument(file, title, description);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
