package com.spring.cloud.ms_users.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.cloud.ms_users.model.dao.RoleDao;
import com.spring.cloud.ms_users.model.dao.UserDao;
import com.spring.cloud.ms_users.model.entity.Role;
import com.spring.cloud.ms_users.model.entity.User;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Iterable<User> getAllUsers() {
        return this.userDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
       return this.userDao.findById(id);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        
        List<Role>roles = new ArrayList<>();
        Optional<Role>opRole = this.roleDao.findByName("ROLE_USER");
        opRole.ifPresent(role -> roles.add(role));

        if(user.isAdmin()){
            Optional<Role>op_role_admin = this.roleDao.findByName("ROLE_ADMIN");
            op_role_admin.ifPresent(role -> roles.add(role));
        }

        user.setRoles(roles);

        user.setEnabled(true);

        return this.userDao.save(user);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(User user, Long id) {
        
         Optional<User> opUser_db = this.getUserById(id);

        return opUser_db.map(userDb -> {
            userDb.setEmail(user.getEmail());
            userDb.setUsername(user.getUsername());
            
            if(user.getEnabled() == null) {
                userDb.setEnabled(true);
            } else {
                userDb.setEnabled(user.getEnabled());
            }

            List<Role>roles = new ArrayList<>();
            Optional<Role>opRole = this.roleDao.findByName("ROLE_USER");
            opRole.ifPresent(role -> roles.add(role));

            if(user.isAdmin()){
                Optional<Role>op_role_admin = this.roleDao.findByName("ROLE_ADMIN");
                op_role_admin.ifPresent(role -> roles.add(role));
            }

            userDb.setRoles(roles);
            return Optional.of(this.userDao.save(userDb));
            
        })
        
        .orElseGet(() -> Optional.empty()); 
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        this.userDao.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) { 
        return this.userDao.findByUsername(username);
    }

    
}
