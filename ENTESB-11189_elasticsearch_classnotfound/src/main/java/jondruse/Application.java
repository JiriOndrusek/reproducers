package jondruse;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchConstants;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.SearchHits;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
//@SpringBootApplication
//@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends RouteBuilder {

    // must have a main method spring-boot can run
    public static void main2(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("content", "test index1");

        Map<String, String> map2 = new HashMap<>();
        map2.put("content", "test index2");

        final String[] ids = new String[3];

        //index
        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> map)
                .to("elasticsearch-rest://docker-cluster?operation=Index&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("index ${body}")
                .process(exchange -> ids[0] = (String)exchange.getIn().getBody())
                .to("mock:result");
        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> map2)
                .to("elasticsearch-rest://docker-cluster?operation=Index&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("index ${body}")
                .process(exchange -> ids[1] = (String)exchange.getIn().getBody())
                .to("mock:result");


        //getById
        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> ids[0])
                .to("elasticsearch-rest://docker-cluster?operation=GetById&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("getById ${body}")
                .to("mock:result");
        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> ids[1])
                .to("elasticsearch-rest://docker-cluster?operation=GetById&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("getById ${body}")
                .to("mock:result");

        //delete
        from("timer:foo?period=10000&repeatCount=1")
                .setBody(() -> ids[0])
                .to("elasticsearch-rest://docker-cluster?operation=Delete&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("delete ${body}")
                .to("mock:result");

        //deleteIndex
        from("timer:foo?period=20000&repeatCount=1")
                .setBody(() -> "twitter")
                .to("elasticsearch-rest://docker-cluster?operation=DeleteIndex&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("delete index ${body}")
                .to("mock:result");

        //bulk index
        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> Arrays.asList(new Map[] {map, map2}))
                .to("elasticsearch-rest://docker-cluster?operation=BulkIndex&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("bulk index ${body}")
                .to("mock:result");

        //bulk
        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> Arrays.asList(new Map[] {map, map2}))
                .to("elasticsearch-rest://docker-cluster?operation=Bulk&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("bulk ${body}")
                .to("mock:result");

        // search
        Map<String, Object> actualQuery = new HashMap<>();
        actualQuery.put("content", "test");

        Map<String, Object> match = new HashMap<>();
        match.put("match", actualQuery);

        Map<String, Object> query = new HashMap<>();
        query.put("query", match);

        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> query)
                .to("elasticsearch-rest://docker-cluster?operation=Search&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("search ${body}")
                .process(exchange -> System.out.println(">>> search hits: " +((SearchHits)exchange.getIn().getBody()).getHits().length))
                .to("mock:result");

        // multisearch
        SearchRequest req = new SearchRequest();
        req.indices("twitter");
        req.types("tweet");
        SearchRequest req1 = new SearchRequest();
        req1.indices("twitter");
        req1.types("tweets");
        MultiSearchRequest request = new MultiSearchRequest().add(req1).add(req);

        from("timer:foo?period=1000&repeatCount=1")
                .setBody(() -> request)
                .to("elasticsearch-rest://docker-cluster?operation=MultiSearch&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("multiSearch ${body}")
                .process(exchange -> System.out.println(">>> multi search itams: " +((MultiSearchResponse.Item[])exchange.getIn().getBody()).length))
                .to("mock:result");

        //update

        Map<String, String> map3 = new HashMap<>();
        map3.put("content", "test index3");

        from("timer:foo?period=5000&repeatCount=1")
                .setBody(() -> map)
                .setHeader(ElasticsearchConstants.PARAM_INDEX_ID, () -> ids[1])
                .to("elasticsearch-rest://docker-cluster?operation=Update&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("update ${body}")
                .process(exchange -> ids[2] = (String)exchange.getIn().getBody())
                .to("mock:result");

        //exist
        from("timer:foo?period=5000&repeatCount=1")
                .setHeader(ElasticsearchConstants.PARAM_INDEX_NAME, () -> "twitter")
                .to("elasticsearch-rest://docker-cluster?operation=exists&indexName=twitter&hostAddresses=127.0.0.1:9200")
                .log("exists ${body}")
                .to("mock:result");

    }
}
