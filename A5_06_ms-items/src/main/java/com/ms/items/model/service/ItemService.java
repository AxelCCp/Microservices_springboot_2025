package com.ms.items.model.service;

import java.util.List;
import java.util.Optional;

import com.ms.items.model.dto.Item;

public interface ItemService {

    List<Item> findAll();

    Optional<Item> findById(Long id);

}
