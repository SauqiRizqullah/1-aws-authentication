package com.aws.authportal.controller;

import com.aws.authportal.dtos.InternalDocumentResponse;
import com.aws.authportal.entity.InternalDocument;
import com.aws.authportal.service.FileStorageService;
import com.aws.authportal.service.InternalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/documents")
public class InternalDocumentController {

    private final InternalDocumentService internalDocumentService;

    private final FileStorageService fileStorageService;

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

    @GetMapping("/{id}")
    public ResponseEntity<InternalDocument> getMetaData(
            @PathVariable(name = "id") String id
    ){
        return ResponseEntity.ok(internalDocumentService.getById(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable(name = "id") String id
    ){
        InternalDocument internalDocument = internalDocumentService.getById(id);

        ResponseInputStream<GetObjectResponse> s3Object =
                fileStorageService.download(internalDocument.getFileKey());

        try {
            byte[] bytes = s3Object.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + internalDocument.getTitle() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, internalDocument.getFileType())
                    .body(bytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "id") String id
    ){
        internalDocumentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
