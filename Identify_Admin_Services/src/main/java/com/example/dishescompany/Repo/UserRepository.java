package com.example.dishescompany.Repo;


import com.example.dishescompany.Models.Role;
import com.example.dishescompany.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByRole(Role role);

}

