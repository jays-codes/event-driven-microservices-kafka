package jayslabs.kafka.analytics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import jayslabs.kafka.analytics.event.ProductViewEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient
class AnalyticsApplicationTests extends AbstractIntegrationTest{

	@Autowired
	private WebTestClient client;

	@Test
	void trendingProductsTest() {
		//1.) emit events
		//create events -> Flux<List<ProductViewEvent>>
		var events = Flux.just(
			createProductViewEvents(1, 7), 
			createProductViewEvents(2, 4), 
			createProductViewEvents(3, 2),
			createProductViewEvents(4, 1),
			createProductViewEvents(5, 8),
			createProductViewEvents(6, 5),
			createProductViewEvents(7, 9)
		)
		.flatMap(Flux::fromIterable) //results in Flux<ProductViewEvent>
		.map(e -> this.toSenderRecord(PRODUCT_VIEW_EVENTS, e.getProductId().toString(), e));

		var resultFlux = this.<ProductViewEvent>createSender().send(events);

		//2.) verify via trending endpoint
		StepVerifier.create(resultFlux)
		.expectNextCount(36)
		.verifyComplete();
	}

	private List<ProductViewEvent> createProductViewEvents(int productId, int count){
		return IntStream.rangeClosed(1, count)
		.mapToObj(i -> new ProductViewEvent(productId))
		.collect(Collectors.toList());
	}

}
