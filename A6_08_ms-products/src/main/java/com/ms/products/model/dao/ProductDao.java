package com.ms.products.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.libs.commons.ms_mylibs.model.entity.Product;


public interface ProductDao extends CrudRepository<Product, Long>{

}
