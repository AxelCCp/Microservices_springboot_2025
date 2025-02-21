package com.ms.products.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.ms.products.model.entity.Product;

public interface ProductDao extends CrudRepository<Product, Long>{

}
