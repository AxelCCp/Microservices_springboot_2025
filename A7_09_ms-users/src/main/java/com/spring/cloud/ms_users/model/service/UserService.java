package com.spring.cloud.ms_users.model.service;

import java.util.Optional;

import com.spring.cloud.ms_users.model.entity.User;

public interface UserService {


    Iterable<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> findByUsername(String username);

    User saveUser(User user);

    Optional<User> updateUser(User user, Long id);

    void deleteUser(Long id);


}
