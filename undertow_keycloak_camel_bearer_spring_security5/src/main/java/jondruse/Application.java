package jondruse;

import io.undertow.server.HttpServerExchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Bean;
import org.apache.camel.component.undertow.spi.UndertowSecurityProvider;

import javax.servlet.Filter;
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
//                from("undertow:http://localhost:8081/books?securityProvider=#securityProvider")
////                from("undertow:http://localhost:8081/books")
//                        .log("<><><><><><><><><>")
//                        .log("${body}");


                from("undertow:http://localhost:8081/books?securityProvider=#securityProvider&allowedRoles=Librarian")
//                from("undertow:http://localhost:8081/books")
                        .transform(simple("Hello ${in.header.name} - Undertow + Keycloak + Camel"))
                        .log("test: ${body}")
                        .to("undertow:http://localhost:8086/test_undertow_camel")
                        .log("${body}")
                        .to("undertow:http://localhost:8080/test")
                        .log("${body}");
            }
        };
    }

    @Bean(name = "securityProvider")
    public UndertowSecurityProvider securityProvider() throws Exception {
        UndertowSecurityProvider provider = new UndertowSecurityProvider() {
            @Override
            public void addHeader(BiConsumer<String, Object> consumer, HttpServerExchange httpExchange) throws Exception {

            }

            @Override
            public int authenticate(HttpServerExchange httpExchange, List<String> allowedRoles) throws Exception {
                return 200;
            }

            @Override
            public boolean acceptConfiguration(Object configuration, String endpointUri) throws Exception {
                return false;
            }

            @Override
            public Filter securityFilter() {
                return delegatingFilterProxyRegistrationBean.getFilter();
            }
        };
        return provider;
    }
}
