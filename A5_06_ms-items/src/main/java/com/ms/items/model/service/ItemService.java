package com.ms.items.model.service;

import java.util.List;
import java.util.Optional;

import com.ms.items.model.dto.Item;
import com.ms.items.model.dto.Product;

public interface ItemService {

    List<Item> findAll();

    Optional<Item> findById(Long id);

    Product save(Product product);

    Product update(Product product, Long id);

    void delete(Long id);

}
