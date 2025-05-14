package com.example.dishescompany.Repo;

// src/main/java/com/example/identityadmin/repo/CredentialRepository.java

import com.example.dishescompany.Models.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    /** Find a credential by its username (for login checks) */
    Credential findByUsername(String username);
}
