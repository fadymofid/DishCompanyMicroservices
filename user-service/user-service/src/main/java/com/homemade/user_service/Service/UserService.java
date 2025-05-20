package com.homemade.user_service.Service;

import com.homemade.user_service.Repositories.UserRepository;
import com.homemade.user_service.entity.User;
import com.homemade.user_service.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register a new user
    public boolean registerUser(String username, String password, UserRole role, Double balance) {
        if (userRepository.existsByUsername(username)) {
            return false;
        }
        // If role is SELLER and password is missing, generate new password
        if (role == UserRole.SELLER && (password == null || password.isEmpty())) {
            password = generateUniqueSellerPassword();
        }
        User user = new User(username, password, role, balance);
        userRepository.save(user);
        return true;
    }

    // Generate a unique password for SELLER role users based on the id of the last inserted user
    public String generateUniqueSellerPassword() {
        User lastUser = userRepository.findTopByOrderByIdDesc(); 
        long lastUserId = (lastUser != null) ? lastUser.getId() : 0; 
        return "sellerPassword" + (lastUserId + 1); 
    }

    // Get user by id
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Authenticate a user
    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    // Get user by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get all users with the role CUSTOMER
    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .toList();
    }

    // Get all users with the role SELLER
    public List<User> getAllSellers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.SELLER)
                .toList();
    }

    // Delete user by username
    @Transactional
    public boolean deleteUser(String username) {
        if (!userRepository.existsByUsername(username)) return false;
        userRepository.deleteByUsername(username);
        return true;
    }

    // Add a method to save user after balance update
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
