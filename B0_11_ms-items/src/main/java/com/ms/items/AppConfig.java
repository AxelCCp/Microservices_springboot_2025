package com.ms.items;

import java.time.Duration;  

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class AppConfig {

    // EL TIME LIMITER SUCEDE ANTES QUE EL SLOW CALL DURATION,  POR LO TANTO AL TIME OUT DURATION SE LE DA UN NUMERO MAS ALTO PARA Q NO SUCEDA.
    // SE DEBE USAR UNA O LA OTRA.


    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> customizerCircuitBreaker() {
        return (factory) -> factory.configureDefault(id -> {
            return new Resilience4JConfigBuilder(id).circuitBreakerConfig(CircuitBreakerConfig.custom()
            .slidingWindowSize(10)                                  //ventana de 10 llamadas.
            .failureRateThreshold(50)                               //porcentage de falla al 50%.
            .waitDurationInOpenState(Duration.ofSeconds(10L))       //duracion de circuit braker en estado abierto.
            .permittedNumberOfCallsInHalfOpenState(5)               //numero de llamadas en el estado semi abierto del circuit breaker.
            
            //CONFIGURACION DE LLAMADAS LENTAS
            .slowCallDurationThreshold(Duration.ofSeconds(2))       //una llamada es lenta a partir de los 2 seg.
            .slowCallRateThreshold(50)                              //con el 50% de llamadas lentas se abre el circuit breaker.

            .build())
            
            //CONFIGURACION DE TIME LIMITER CONFIG - CONFIGURA EL TIEMPO MAXIMO Q DEMORA UNA RESPUESTA CORRECTA
            .timeLimiterConfig(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(4L))
            .build())
            .build();                      
        });
    }

}
