package jayslabs.kafka.products;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import jayslabs.kafka.products.event.ProductViewEvent;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductsApplicationTests extends AbstractIntegrationTest {

    @Autowired
	private WebTestClient webclient;

	@Test
	void testProductViewAndEvents() {
		//view products
		viewProductSuccess(1);
		viewProductSuccess(1);
		viewProductNotFound(1000);
		viewProductSuccess(5);

		//check if events are available in the topic
		var flux = this.<ProductViewEvent>createReceiver(PRODUCT_VIEW_EVENTS)
		.receive()
		.take(3);

		StepVerifier.create(flux)
		.consumeNextWith(rr -> Assertions.assertEquals(1, rr.value().getProductId()))
		.consumeNextWith(rr -> Assertions.assertEquals(1, rr.value().getProductId()))
		.consumeNextWith(rr -> Assertions.assertEquals(5, rr.value().getProductId()))
		.verifyComplete();
	}

	private void viewProductSuccess(int productId) {
		webclient
		.get()
		.uri("/product/" + productId)
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody()
		.jsonPath("$.id").isEqualTo(productId)
		.jsonPath("$.description").isEqualTo("product-" + productId);
	}

	private void viewProductNotFound(int productId) {
		webclient
		.get()
		.uri("/product/" + productId)
		.exchange()
		.expectStatus().is4xxClientError();
	}

}
