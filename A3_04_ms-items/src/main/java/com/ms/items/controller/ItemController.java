package com.ms.items.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ms.items.model.dto.Item;
import com.ms.items.model.service.ItemService;

@RestController
public class ItemController {

    public ItemController(@Qualifier("itemServiceWebClient")ItemService itemService) {
        this.itemService = itemService;
    }

    //se recoge el parametro que se pone desde el filtro factory del api gateway

    @GetMapping
    public List<Item>list(@RequestParam(name="name", required = false) String name, @RequestHeader(name="token-request", required = false) String token) {

        System.out.println(name);
        System.out.println(token);
        
        return this.itemService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {

        Optional<Item>opItem = this.itemService.findById(id);

        if(opItem.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(opItem.orElseThrow());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Product not found. "));
    }


    private final ItemService itemService;

}
