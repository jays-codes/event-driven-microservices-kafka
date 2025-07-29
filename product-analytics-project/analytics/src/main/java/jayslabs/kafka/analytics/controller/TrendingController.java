package jayslabs.kafka.analytics.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jayslabs.kafka.analytics.dto.ProductTrendingDTO;
import jayslabs.kafka.analytics.service.TrendingProductsBroadcastService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
@RequestMapping("trending")
public class TrendingController {

    private final TrendingProductsBroadcastService service;

    @GetMapping(produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ProductTrendingDTO>> trending() {
        return this.service.getTrends();

    }
}
