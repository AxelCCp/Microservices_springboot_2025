package com.ms.products.model.service;

import java.util.List;
import java.util.Optional;

import com.ms.products.model.entity.Product;

public interface ProductService {

    List<Product>findAll();

    Optional<Product>findById(Long id);

    Product save(Product product);

    void deleteById(Long id);

}
