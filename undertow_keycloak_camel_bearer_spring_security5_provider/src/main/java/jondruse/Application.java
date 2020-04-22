package jondruse;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.springSecurity5.SpringSecurity5Configuration;
import org.apache.camel.component.springSecurity5.SpringSecurity5Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Bean;
import org.apache.camel.component.undertow.spi.UndertowSecurityProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {
    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    DelegatingFilterProxyRegistrationBean delegatingFilterProxyRegistrationBean;

    @Bean
    public RouteBuilder route() {
        return new RouteBuilder() {
            public void configure() throws Exception {
//                from("undertow:http://localhost:8081/books?allowedRoles=Librarian")
                from("undertow:http://localhost:8081/books?securityConfiguration=#springSecurity5Configuration&allowedRoles=Librarian")
                        .transform(simple("Hello ${in.header." + SpringSecurity5Provider.PRINCIPAL_NAME_HEADER + "} - Undertow + Keycloak + Camel"))
                        .log("test: ${body}");
//                        .to("undertow:http://localhost:8086/test_undertow_camel")
//                        .log("${body}")
//                        .to("undertow:http://localhost:8080/test")
//                        .log("${body}");
            }
        };
    }

    @Bean(name = "springSecurity5Configuration")
    public SpringSecurity5Configuration securityConfiguration(ClientRegistrationRepository repository) {
        return new SpringSecurity5Configuration() {
            @Override
            public Filter getSecurityFilter() {
                return delegatingFilterProxyRegistrationBean.getFilter();
            }

            @Override
            public ClientRegistration getKeycloakRegistration() {
                return repository.findByRegistrationId("keycloak");
            }


        };
    }


}
