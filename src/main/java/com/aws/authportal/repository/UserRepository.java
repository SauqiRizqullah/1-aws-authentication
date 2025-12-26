package com.aws.authportal.repository;

import com.aws.authportal.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String>, JpaSpecificationExecutor {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // (opsional, tapi sering kepakai untuk search)
    List<User> findByFullNameContainingIgnoreCase(String fullName);
}
