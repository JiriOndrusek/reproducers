package jondruse.reproducers.tika.quickstart.imageparser;

import io.quarkus.tika.TikaContent;
import io.quarkus.tika.TikaParser;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;


@Path("/tika")
public class TikaParserResource {
    private static final Logger log = Logger.getLogger(TikaParserResource.class);
    @Inject
    TikaParser parser;

    @POST
    @Path("/parse")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public String extractText(InputStream message) {
        Instant start = Instant.now();

        TikaContent tc = parser.parse(message);

        Instant finish = Instant.now();

        log.info(Duration.between(start, finish).toMillis() + " mls have passed");

        return tc.getMetadata().getSingleValue("Content-Type");
    }
}