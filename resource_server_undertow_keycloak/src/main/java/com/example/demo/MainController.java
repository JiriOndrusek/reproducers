package com.example.demo;


//import org.springframework.security.oauth2.client.annotation.OAuth2Client;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class MainController {


    @GetMapping("/hello")
    public Map<String, String> hello(final @AuthenticationPrincipal Jwt jwt) {
        System.out.println("claims:\n" + jwt.getClaims());
        System.out.println("\nheaders:\n" + jwt.getHeaders());
        return Collections.singletonMap("message", "Hello " + jwt.getSubject());
    }
}