package com.spring.cloud.ms_users.model.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.spring.cloud.ms_users.model.entity.User;

public interface UserDao extends CrudRepository<User, Long> {


    Optional<User> findByUsername(String username);

}
