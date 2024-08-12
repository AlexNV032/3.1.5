package com.alexnv.service;

import com.alexnv.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User findByEmail(String email);
    List<User> findAllUsers();
    User findById(Long id);
    User updateUser(Long id, User user, Set<Long> roleIds);
    void deleteUserById(Long id);

}
