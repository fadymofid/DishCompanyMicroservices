package com.homemade.user_service.Repositories;

import com.homemade.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);

    // Custom query to find the last added user by ID
    User findTopByOrderByIdDesc();
}
