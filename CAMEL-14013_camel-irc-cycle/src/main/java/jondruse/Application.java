package jondruse;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends RouteBuilder {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure() throws Exception {
        from("irc://priv2@127.0.0.1:6667?channels=#test1&commandTimeout=10000")
//                .removeHeaders("irc.target")
                .log("${headers}")
                .to("irc://chan2@127.0.0.1:6667?channels=#test2&commandTimeout=10000");
    }
}
