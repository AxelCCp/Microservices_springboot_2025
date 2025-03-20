package com.ms.spcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class MsGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGatewayServerApplication.class, args);
	}


	@Bean
	RouterFunction<ServerResponse> routerConfig() {

		return GatewayRouterFunctions
					.route("ms-products")
					.route(GatewayRequestPredicates.path("/api/products/**"), HandlerFunctions.http())

					//filtro personalizado - Se clona la request y se modifica antes de ser enviado al microservicio.
					.filter((request, next) -> {

						//filtro - pre handle - modif la request
						ServerRequest requestModified = ServerRequest.from(request).header("message-request", "algun mensaje al request").build();
						//luego se obtiene la resp
						ServerResponse response =  next.handle(requestModified);
						// se modif 
						response.headers().add("message response", "algun mensaje en la respuesta");

						return response;
					})

					.filter(LoadBalancerFilterFunctions.lb("ms-products"))
					.filter(CircuitBreakerFilterFunctions.circuitBreaker(config -> config.setId("products").setStatusCodes("500").setFallbackPath("forward:/api/items/5")))
					.before(BeforeFilterFunctions.stripPrefix(2)).build();
	}

}
