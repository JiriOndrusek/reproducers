package jondruse.reproducers.tika.quickstart.imageparser;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TikaParserResourceTest {

    @Test
    public void testImage() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("black.png");
        given()
          .when()
                .contentType(ContentType.BINARY)
                .body(is)
                .post("/tika/parse")
          .then()
             .statusCode(200)
             .body(is("image/gif"));
    }

    @Test
    public void tesPdf() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("quarkus.pdf");
        given()
          .when()
                .contentType(ContentType.BINARY)
                .body(is)
                .post("/tika/parse")
          .then()
             .statusCode(200)
             .body(is("application/pdf"));
    }

}