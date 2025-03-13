package com.ms.spcloud.gateway.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/authorized")
    public Map<String, String> getMethodName(@RequestParam String code) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("code", code);
        return map;
    }


    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Collections.singletonMap("logout", "ok");
    }

}
