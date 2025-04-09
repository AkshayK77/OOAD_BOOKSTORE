package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.entity.User;

public interface UserService {
    User registerUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    User getCurrentUser();
} 