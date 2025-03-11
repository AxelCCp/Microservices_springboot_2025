package com.ms.items.model.service;

import java.util.List;
import java.util.Optional;

import com.libs.commons.ms_mylibs.model.entity.Product;
import com.ms.items.model.dto.Item;


public interface ItemService {

    List<Item> findAll();

    Optional<Item> findById(Long id);

    Product save(Product product);

    Product update(Product product, Long id);

    void delete(Long id);

}
