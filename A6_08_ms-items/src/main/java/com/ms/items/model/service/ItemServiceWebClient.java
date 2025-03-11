package com.ms.items.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

//import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.libs.commons.ms_mylibs.model.entity.Product;
import com.ms.items.model.dto.Item;


//@Primary
@Service
public class ItemServiceWebClient implements ItemService {

    private final WebClient.Builder client;


    public ItemServiceWebClient(Builder client) {
        this.client = client;
    }


    @Override
    public List<Item> findAll() {
       return this.client.build()
            .get()
            .accept(MediaType.APPLICATION_JSON)                 // 29 - acepta un json.
            .retrieve().bodyToFlux(Product.class)                  // 29 - lo que devuelve se pasa a flujo reactivo. 
            
            .map(product -> {
                Random random = new Random();
                return new Item(product, random.nextInt(10) + 1);
            })

            .collectList()                                      // 29 - se pasa a lista reactiva. 
            .block();                                           // 29 - se pasa a un objeto no reactivo.
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);

        //56 - se comenta el try,  para poder usar el circuit braker.
        //try {

            return Optional.of(client.build()
                .get()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                
                .map(product -> {
                    Random random = new Random();
                    return new Item(product, random.nextInt(10) + 1);
                })
                
                .block());

        //} catch(WebClientResponseException e) {
          //  return Optional.empty();
        //}

       
    }


    @Override
    public Product save(Product product) {

      return client.build().post()                                          //se define el tipo de peticion.
                    .contentType(MediaType.APPLICATION_JSON)                //se va a enviar un json.
                    .bodyValue(product)                                     //se pasa el obj en el body.
                    .retrieve()                                             //se envia la peticion.
                    .bodyToMono(Product.class)                              //el ms-products responde con un producto en el body,  por lo tanto se recibe y se pasa el body a un mono de tipo product.
                    .block();                                               //luego del flujo mono se obtiene el producto
    
                }


    @Override
    public Product update(Product product, Long id) {
        
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);

        return client.build()
                .put()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .block();

    }


    @Override
    public void delete(Long id) {
        
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);

       client.build().delete().uri("/{id}", params).retrieve().bodyToMono(Void.class).block();
    }



}
