package jayslabs.kafka.products.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jayslabs.kafka.products.dto.ProductDTO;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;



@RestController
@AllArgsConstructor
@RequestMapping("product")
public class ProductsController {

    @GetMapping("{productId}")
    public Mono<ProductDTO> viewProduct(@PathVariable Integer productId) {
        return Mono.empty();
    }
}
