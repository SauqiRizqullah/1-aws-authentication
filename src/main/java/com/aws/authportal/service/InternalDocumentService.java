package com.aws.authportal.service;

import com.aws.authportal.dtos.InternalDocumentResponse;
import com.aws.authportal.entity.DocumentStatus;
import com.aws.authportal.entity.InternalDocument;
import com.aws.authportal.entity.User;
import com.aws.authportal.repository.InternalDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InternalDocumentService {

    private final FileStorageService fileStorageService;
    private final InternalDocumentRepository internalDocumentRepository;

    public InternalDocumentResponse uploadDocument(
            MultipartFile file,
            String title,
            String description
    ){
        // 1. Cek Akun yang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // 2. Validasi akun yang login
        if (currentUser == null) {
            throw new RuntimeException("Unauthorized: User not logged in");
        }
        if (!currentUser.getRole().name().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Unauthorized: Only Admin Can Upload Documents");
        }

        // 3. Upload file ke S3
        String fileKey = fileStorageService.upload(file, "internal-documents");

        // 4. Simpan metadata dokumen ke database
        InternalDocument document = new InternalDocument();
        document.setTitle(title);
        document.setDescription(description);
        document.setFileKey(fileKey);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus(DocumentStatus.ACTIVE);

        InternalDocument saved = internalDocumentRepository.save(document);

        return InternalDocumentResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .fileType(saved.getFileType())
                .fileSize(saved.getFileSize())
                .status(saved.getStatus().name())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
