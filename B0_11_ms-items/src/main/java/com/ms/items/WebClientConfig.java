package com.ms.items;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


//28 - configuracion de comunicacion reactiva con WebClient
@Configuration
public class WebClientConfig {

    /*
    @Value("${config.base-url.endpoint.ms-products}")
    private String url;

    
    @Bean
    @LoadBalanced
    WebClient.Builder webClient() {
        return WebClient.builder().baseUrl(url);
    }
    */


    //131-modificacion para propagar el contexto de las trazas de zipkin entre los microservicios:
    @Bean                                                                                                                 //lbFunction : provee el load balancer.
    WebClient webClient(WebClient.Builder webClientBuilder, @Value("${config.base-url.endpoint.ms-products}") String url, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return webClientBuilder.baseUrl(url).filter(lbFunction).build();
    }


}
