package com.security.ms_oauth2.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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



@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private WebClient.Builder client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Map<String, String> params = new HashMap<>();
        params.put("username", username);

        try {
            
            User user = client.build().get().uri("/username/{username}", params)            //llama al rest que tiene esta ruta en el ms-users.
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(User.class)
            .block();

            List<GrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
            
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, roles);

        } catch (WebClientResponseException e) {

            throw new UsernameNotFoundException("Error en el login, no existe el user con el nombre " + username + " en el sistema.");

        }


    }

}
