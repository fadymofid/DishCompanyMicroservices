package com.example.dishescompany.Repo;

// src/main/java/com/example/identityadmin/repo/CredentialRepository.java

import com.example.dishescompany.Models.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Seller, Long> {
    /** Find a credential by its username (for login checks) */
    Seller findByUsername(String username);
}
