package com.spring.cloud.ms_users.model.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.spring.cloud.ms_users.model.entity.Role;

public interface RoleDao extends CrudRepository<Role, Long>{

    Optional<Role>findByName(String name);   

}
