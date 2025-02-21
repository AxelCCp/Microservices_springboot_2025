package com.ms.products.model.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.products.model.dao.ProductDao;
import com.ms.products.model.entity.Product;

@Service
public class ProductServiceImpl implements ProductService{

    public ProductServiceImpl(ProductDao productDao, Environment environment){
        this.productDao = productDao;
        this.environment = environment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return ((List<Product>) this.productDao.findAll()).stream().map(product -> {
            product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
            return product;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return this.productDao.findById(id).map(product -> {
            product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
            return product;
        });
    }


    //@Autowired
    private final ProductDao productDao;

    private final Environment environment; 

}
