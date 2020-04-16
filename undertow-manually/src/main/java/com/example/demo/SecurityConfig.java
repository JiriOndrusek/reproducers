package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    @EnableWebSecurity
    public static class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void init(WebSecurity web) throws Exception {
            ExceptionHandlingConfigurer exceptionHandlingConfigurer = web.getConfigurer(ExceptionHandlingConfigurer.class);
            exceptionHandlingConfigurer.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/keycloak"));
            super.init(web);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2Login();
//            http.authorizeRequests().anyRequest().authenticated()
//                    .and()
//                    .oauth2Login()
//                    .clientRegistrationRepository(this.clientRegistrationRepository())
//                    .authorizedClientService(authorizedClientService());
        }
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(Collections.singletonList(getKeycloakRegistration()));
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {

        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    private ClientRegistration getKeycloakRegistration() {
        return ClientRegistration.withRegistrationId("keycloak")
                .clientId("demoapp")
                .clientSecret("demoapp")
                .clientName("Keycloak")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid","profile", "email")
                .authorizationUri("http://localhost:8001/auth/realms/demo/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8001/auth/realms/demo/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8001/auth/realms/demo/protocol/openid-connect/userinfo")
                .jwkSetUri("http://localhost:8001/auth/realms/demo/protocol/openid-connect/certs")
                .userNameAttributeName("preferred_username")
                .build();
    }

}