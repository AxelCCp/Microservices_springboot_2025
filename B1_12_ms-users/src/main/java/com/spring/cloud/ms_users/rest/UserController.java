package com.spring.cloud.ms_users.rest;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.cloud.ms_users.model.entity.User;
import com.spring.cloud.ms_users.model.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);



    @GetMapping
    public ResponseEntity<?>userList() {

        logger.info("user - controller - list");

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?>getUserById(@PathVariable Long id) {

        logger.info("user - controller - getUserById");
        
        //return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUserById(id));

        Optional<User> opUser = this.userService.getUserById(id);

        return opUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<?>getUserByUsername(@PathVariable String username) {

        logger.info("user - controller - getUserByUsername");
        
        Optional<User> opUser = this.userService.findByUsername(username);

        return opUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<?>create(@RequestBody User user) {

        logger.info("user - controller - create");

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.saveUser(user));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?>update(@RequestBody User user, @PathVariable Long id) {

        logger.info("user - controller - update");

        Optional<User> op_user_updated= this.userService.updateUser(user, id);

        return op_user_updated.map(user_updated ->  ResponseEntity.status(HttpStatus.CREATED).body(user_updated)).orElseGet(()-> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteById(@PathVariable Long id) {

        logger.info("user - controller - delete");

        this.userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
