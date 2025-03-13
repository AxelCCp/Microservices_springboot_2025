package com.ms.products.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libs.commons.ms_mylibs.model.entity.Product;
import com.ms.products.model.service.ProductService;

@RestController     //@RequestMapping("/api/products")
public class ProductController {

    final private ProductService productService;

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public List<Product> list(@RequestHeader(name="message-request", required=false) String messageRequest) {
        
        logger.info("product - controller - list");

        logger.info("messageRequest :: {}", messageRequest );

        return this.productService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product>details(@PathVariable Long id) throws InterruptedException {

        logger.info("product - controller - detail - id: {}", id);

        if(id.equals(10L)){
            throw new IllegalStateException("producto no encontrado");
        }

        if(id.equals(7L)) {
            TimeUnit.SECONDS.sleep(3L);
        }

        Optional<Product> opProduct = this.productService.findById(id);
        if(opProduct.isPresent()) {
            logger.info("product - controller - detai - id: {} - OK OK OK", id);
            return ResponseEntity.status(HttpStatus.OK).body(opProduct.orElseThrow());
        }
        
        logger.info("product - controller - detail - id: {} - NOT FOUND", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @PostMapping
    public ResponseEntity<?>create(@RequestBody Product product) {
        logger.info("product - controller - create - product: {}", product);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.save(product));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?>update(@PathVariable Long id, @RequestBody Product product) {
        
        logger.info("product - controller - update - product: {}", product);

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

        logger.info("product - controller - delete - id: {}", id);

        Optional<Product> opProduct = this.productService.findById(id);

        if(opProduct.isPresent()) {
            this.productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    
    }

}
