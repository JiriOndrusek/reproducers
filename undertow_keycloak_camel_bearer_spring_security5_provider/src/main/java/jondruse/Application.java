package jondruse;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.spring.security.SpringSecurityConfiguration;
import org.apache.camel.component.spring.security.SpringSecurityProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import javax.servlet.Filter;

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
                        .transform(simple("Hello ${in.header." + SpringSecurityProvider.PRINCIPAL_NAME_HEADER + "} - Undertow + Keycloak + Camel"))
                        .log("test: ${body}");
//                        .to("undertow:http://localhost:8086/test_undertow_camel")
//                        .log("${body}")
//                        .to("undertow:http://localhost:8080/test")
//                        .log("${body}");
            }
        };
    }

    @Bean(name = "springSecurity5Configuration")
    public SpringSecurityConfiguration securityConfiguration(ClientRegistrationRepository repository) {
        return new SpringSecurityConfiguration() {
            @Override
            public Filter getSecurityFilter() {
                return delegatingFilterProxyRegistrationBean.getFilter();
            }

        };
    }


}
