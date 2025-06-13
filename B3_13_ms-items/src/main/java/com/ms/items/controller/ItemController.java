package com.ms.items.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.libs.commons.ms_mylibs.model.entity.Product;
import com.ms.items.model.dto.Item;
import com.ms.items.model.service.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RefreshScope //permite refrescar componenetes en tiempo real para no tener que reiniciar la aplicacion entera. Los componenetes se refrescan a través de una ruta url con ACTUATOR. por lo tanto hay q agregar esta dependecia. 
@RestController
public class ItemController {

    private final ItemService itemService;

    private final CircuitBreakerFactory circuitBreakerFactory;

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Value("${configuracion.texto}")
    private String text;


    @Autowired
    private Environment env;


    @GetMapping("/fetch-configs")
    public ResponseEntity<?>fetchConfigs(@Value("${server.port}") String port) {
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);
        logger.info("text: " + text);
        logger.info("port: " + port);

        if(env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")){
            json.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
            json.put("autor.email", env.getProperty("configuracion.autor.email"));
        }
        return ResponseEntity.ok(json);
    }

    //------ELIGE INYECCION-----------------
    //@Qualifier("itemServiceWebClient")
    //@Qualifier("itemServiceFeign")
    //--------------------------------------
    public ItemController(@Qualifier("itemServiceWebClient")ItemService itemService, CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.itemService = itemService;
    }

    //se recoge el parametro que se pone desde el filtro factory del api gateway
    @GetMapping
    public List<Item>list(@RequestParam(name="name", required = false) String name, @RequestHeader(name="token-request", required = false) String token) {

        logger.info("Llamada a metodo de controller item: public List<Item>list()");
        logger.info("msg: request parameter: {}", name);
        logger.info("Token: {}", token);

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



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product  product) {
        logger.info("item controller - create - product: {}", product);
        return this.itemService.save(product);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product update(@RequestBody Product product, @PathVariable Long id) {
        logger.info("item controller - update - product: {}", product);
        return this.itemService.update(product, id);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.info("item controller - delete - product with id: {}", id);
        this.itemService.delete(id);
    }

}
