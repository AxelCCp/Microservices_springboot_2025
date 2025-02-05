package com.ms.items.model.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ms.items.client.ProductFeign;
import com.ms.items.model.dto.Item;
import com.ms.items.model.dto.Product;

import feign.FeignException;

@Service
public class ItemServiceFeign implements ItemService{

    @Override
    public List<Item> findAll() {
      return this.productFeign.findAll().stream().map(product -> {
        Random random = new Random();
        return new Item(product, random.nextInt(10) + 1);
      }).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {

      try{
        Product product = this.productFeign.details(id);
        return Optional.ofNullable(new Item(product, new Random().nextInt(10) + 1));
      } catch(FeignException e) {
        return Optional.empty();
      }
    
    }


    @Autowired
    private ProductFeign productFeign;

}
