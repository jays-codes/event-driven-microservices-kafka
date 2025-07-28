package jayslabs.kafka.analytics.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jayslabs.kafka.analytics.dto.ProductTrendingDTO;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
@RequestMapping("trending")
public class TrendingController {

    @GetMapping(produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductTrendingDTO> trending() {
        return Flux.empty();
    }
}
