package com.ms.items.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ms.items.model.dto.Item;
import com.ms.items.model.dto.Product;
import com.ms.items.model.service.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
public class ItemController {

     private final ItemService itemService;

    private final CircuitBreakerFactory circuitBreakerFactory;

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    public ItemController(@Qualifier("itemServiceWebClient")ItemService itemService, CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.itemService = itemService;
    }

    //se recoge el parametro que se pone desde el filtro factory del api gateway

    @GetMapping
    public List<Item>list(@RequestParam(name="name", required = false) String name, @RequestHeader(name="token-request", required = false) String token) {

        System.out.println(name);
        System.out.println(token);
        
        return this.itemService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {

        Optional<Item>opItem = this.circuitBreakerFactory.create("items").run(() -> this.itemService.findById(id), e -> {

            this.logger.error(e.getMessage());

            Product p = new Product();
            p.setCreateAt(LocalDate.now());
            p.setId(1L);
            p.setName("Camara sony");
            p.setPrice(500.00);
            Item item = new Item(p, 5);
            return Optional.of(item);
        });

        if(opItem.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(opItem.orElseThrow());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Product not found. "));
    }


    //CIRCUIT BREAKER CON ANOTACIONES

    //Esto funciona solo con las configuraciones en el application yml.

    @CircuitBreaker(name="items", fallbackMethod = "getFallBackMethodProduct") //items : es el nombre que se le dio al circuit breaker en el application yml.
    @GetMapping("/details/{id}")
    public ResponseEntity<?> detail_2(@PathVariable Long id) {

        Optional<Item>opItem =  this.itemService.findById(id);

        if(opItem.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(opItem.orElseThrow());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Product not found. "));
    }

    public ResponseEntity<?>getFallBackMethodProduct(Throwable e) {

        this.logger.error(e.getMessage());

        Product p = new Product();
        p.setCreateAt(LocalDate.now());
        p.setId(1L);
        p.setName("Camara sony");
        p.setPrice(500.00);
        Item item = new Item(p, 5);
        return ResponseEntity.ok(item);

    }


    //TIME LIMITER CON ANOTACIONES
    
    //TimeLimiter : con esta anotacion no se abre el corto circuito despues de completar un porcentaje de llamadas. para tener corto circuito hay q combinar con @CircuitBreaker

    //se envuelve la llamada en un obj completable future para poder medir el tiempo de la llamada.

    @TimeLimiter(name="items", fallbackMethod = "getFallBackMethodProduct2") //items : es el nombre que se le dio al circuit breaker en el application yml.
    @GetMapping("/details2/{id}")
    public CompletableFuture<?> detail_3(@PathVariable Long id) {

        return CompletableFuture.supplyAsync(() -> {

            Optional<Item>opItem =  this.itemService.findById(id);

            if(opItem.isPresent()){
                return ResponseEntity.status(HttpStatus.OK).body(opItem.orElseThrow());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Product not found. "));

        });

    }

    public CompletableFuture<?>getFallBackMethodProduct2(Throwable e) {

        return CompletableFuture.supplyAsync(() -> {

            this.logger.error(e.getMessage());

            Product p = new Product();
            p.setCreateAt(LocalDate.now());
            p.setId(1L);
            p.setName("Camara sony");
            p.setPrice(500.00);
            Item item = new Item(p, 5);
            return ResponseEntity.ok(item);

        });

    }



    //COMBINACION DE CIRCUITBREAKER Y TIMELIMITER

    @CircuitBreaker(name="items", fallbackMethod = "getFallBackMethodProduct3")
    @TimeLimiter(name="items") //items : es el nombre que se le dio al circuit breaker en el application yml.
    @GetMapping("/details3/{id}")
    public CompletableFuture<?> detail_4(@PathVariable Long id) {

        return CompletableFuture.supplyAsync(() -> {

            Optional<Item>opItem =  this.itemService.findById(id);

            if(opItem.isPresent()){
                return ResponseEntity.status(HttpStatus.OK).body(opItem.orElseThrow());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Product not found. "));

        });

    }

    public CompletableFuture<?>getFallBackMethodProduct3(Throwable e) {

        return CompletableFuture.supplyAsync(() -> {

            this.logger.error(e.getMessage());

            Product p = new Product();
            p.setCreateAt(LocalDate.now());
            p.setId(1L);
            p.setName("Camara sony");
            p.setPrice(500.00);
            Item item = new Item(p, 5);
            return ResponseEntity.ok(item);

        });

    }






    

}
