package com.security.ms_oauth2.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.security.ms_oauth2.model.entity.User;

import io.micrometer.tracing.Tracer;



@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private WebClient client;

    private final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private Tracer tracer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.info("INGRESANDO AL PROCESO DE LOGIN - USERSERVICE - MS-OAUTH2 - USERNAME:: {}", username);

        Map<String, String> params = new HashMap<>();
        params.put("username", username);

        try {
            
            User user = client.get().uri("/username/{username}", params)            //llama al rest que tiene esta ruta en el ms-users.
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(User.class)
            .block();

            List<GrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());


            logger.info("se ha realizado el login con exito, user:: {}", username);
            tracer.currentSpan().tag("success.login.message", "se ha realizado el login con exito, user:: " +  username + ".");
            
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, roles);

        } catch (WebClientResponseException e) {

            String error = "Error en el login ... no existe el user en el sistema";

            logger.error(error);

            tracer.currentSpan().tag("error.login.message", error + " : " + e.getMessage());

            throw new UsernameNotFoundException("Error en el login, no existe el user con el nombre " + username + " en el sistema.");

        }


    }

}
