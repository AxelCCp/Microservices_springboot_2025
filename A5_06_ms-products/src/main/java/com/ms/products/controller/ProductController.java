package com.ms.products.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.products.model.entity.Product;
import com.ms.products.model.service.ProductService;

@RestController     //@RequestMapping("/api/products")
public class ProductController {

    final private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public List<Product> list() {
        return this.productService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product>details(@PathVariable Long id) throws InterruptedException {

        if(id.equals(10L)){
            throw new IllegalStateException("producto no encontrado");
        }

        if(id.equals(7L)) {
            TimeUnit.SECONDS.sleep(3L);
        }

        Optional<Product> opProduct = this.productService.findById(id);
        if(opProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(opProduct.orElseThrow());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @PostMapping
    public ResponseEntity<?>create(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.save(product));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?>update(@PathVariable Long id, @RequestBody Product product) {
        
        Optional<Product> opProduct = this.productService.findById(id);
    
        if(opProduct.isPresent()) {

            Product product_DB = opProduct.get();

            product_DB.setName(product.getName());
            product_DB.setPrice(product.getPrice());
            product_DB.setCreateAt(product_DB.getCreateAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.save(product_DB));
        }
    
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?>delete(@PathVariable Long id) {

        Optional<Product> opProduct = this.productService.findById(id);
        if(opProduct.isPresent()) {
            this.productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    
    }

}
