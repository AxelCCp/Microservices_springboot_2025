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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.ms.items.model.dto.Item;
import com.ms.items.model.dto.Product;

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

        try {

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

        } catch(WebClientResponseException e) {

            return Optional.empty();

        }

       
    }



}
