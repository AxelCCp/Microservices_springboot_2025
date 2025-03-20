package com.ms.spcloud.gateway.filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@Component
public class SampleGlobalFilter implements Filter,  Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public int getOrder() {
       return 100;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        logger.info("llamada filtro SampleGlobalFilter:: doFilter()");

        chain.doFilter(request, response);  //para q continue con la ejecucion,  si no se pone esto,  no devuelve el codigo para obtener el token.
    }


}

/* 

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

//44 - 45

@Component
public class SampleGlobalFilter implements GlobalFilter,  Ordered{

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        logger.info("ejecutando el filtro antes del request PRE");

        ServerHttpRequest mutateRequest = exchange.getRequest().mutate().headers(h -> h.add("token", "abcdefg")).build();           //46-la request es inmutable,  por lo tanto con mutate se crea una request nuevo.

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutateRequest).build();

        return chain.filter(mutatedExchange)

        .then(Mono.fromRunnable(() -> {

            logger.info("ejecutando filtro POST responses");

            //se obtiene token con programacion normal
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");

            if(token != null) {
                logger.info("token: " + token);
            }


            //se obtiene token con programacion funcional
            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token")).ifPresent(value -> {
                logger.info("token - programacion funcional: " + value);
                mutatedExchange.getResponse().getHeaders().add("token", value);
            });


            mutatedExchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());

            //mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);          // establece respuesta como texto plano
        
        }));

    }

    //46 - 
    @Override
    public int getOrder() {
       return 100;
    }

}


*/