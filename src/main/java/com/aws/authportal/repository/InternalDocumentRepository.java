package com.aws.authportal.repository;

import com.aws.authportal.entity.InternalDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalDocumentRepository extends CrudRepository<InternalDocument, String> {
}
