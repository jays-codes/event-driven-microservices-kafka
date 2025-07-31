package jayslabs.kafka.analytics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import jayslabs.kafka.analytics.dto.ProductTrendingDTO;
import jayslabs.kafka.analytics.event.ProductViewEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient(timeout = "10000")
class AnalyticsApplicationTests extends AbstractIntegrationTest{

	@Autowired
	private WebTestClient client;

	@Test
	void trendingProductsTest() throws InterruptedException {
		//1.) emit events
		//create events -> Flux<List<ProductViewEvent>>
		var events = Flux.just(
				createEvent(2, 2),
				createEvent(1, 1),
				createEvent(6, 3),
				createEvent(4, 2),
				createEvent(5, 5),
				createEvent(4, 2),
				createEvent(6, 3),
				createEvent(3, 3)
		)
		.flatMap(Flux::fromIterable) //results in Flux<ProductViewEvent>
		.map(e -> this.toSenderRecord(PRODUCT_VIEW_EVENTS, e.getProductId().toString(), e));

		var resultFlux = this.<ProductViewEvent>createSender().send(events);

		StepVerifier.create(resultFlux)
		.expectNextCount(21)
		.verifyComplete();

		// Wait for Kafka processing (1s buffer) + trending service polling (5s)
		Thread.sleep(6000);

		//2.) verify via trending endpoint
		var mono = this.client.get()
		.uri("/trending")
		.accept(MediaType.TEXT_EVENT_STREAM)
		.exchange()
		.returnResult(new ParameterizedTypeReference<List<ProductTrendingDTO>>() {})
		.getResponseBody()
		.next(); // Mono<List<ProductTrendingDTO>>

		StepVerifier.create(mono)
		.consumeNextWith(this::validateResult)
		.verifyComplete();
	}

    private void validateResult(List<ProductTrendingDTO> list){
		Assertions.assertEquals(5, list.size());
		Assertions.assertEquals(6, list.get(0).getProductId());
		Assertions.assertEquals(6, list.get(0).getViewCount());
		Assertions.assertEquals(2, list.get(4).getProductId());
		Assertions.assertEquals(2, list.get(4).getViewCount());
		Assertions.assertTrue(list.stream().noneMatch(p->p.getProductId() == 1));
	}

	private List<ProductViewEvent> createEvent(int productId, int count){
		return IntStream.rangeClosed(1, count)
		.mapToObj(i -> new ProductViewEvent(productId))
		.collect(Collectors.toList());
	}

}
