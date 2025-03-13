package com.ms.spcloud.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authz -> {
            
            authz.requestMatchers("/authorized", "/logout").permitAll()
            
            .requestMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/items/{id}",  "/api/products/{id}",  "/api/users/{id}").hasAnyRole("ADMIN", "USER")
            
            //ESTA ES EQUIVALENTE A LAS 3 pathMatchers() SIGUIENTES:
            //.pathMatchers("/api/items/**", "/api/products/**", "/api/users/**").hasRole("ADMIN")

            .requestMatchers(HttpMethod.PUT, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")

            .anyRequest().authenticated();
            
        }).cors(csrf -> csrf.disable())

        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .oauth2Login(login -> login.loginPage("/oauth2/authorization/client-app"))

        .oauth2Client(withDefaults())
        
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new Converter<Jwt, AbstractAuthenticationToken>(){

            @Override
            public AbstractAuthenticationToken convert(Jwt source) {
                Collection <String> roles = source.getClaimAsStringList("roles");
                Collection<GrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
                return new JwtAuthenticationToken(source, authorities);
            }

            

        })))
        .build();
    }

}
