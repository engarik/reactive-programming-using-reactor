package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();

    @Test
    void namesFlux() {
        var namesFlux = service.namesFlux();

        StepVerifier.create(namesFlux)
            //.expectNext("alex", "ben", "chloe")
            //.expectNextCount(3)
            .expectNext("alex")
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void nameMono() {
        var nameMono = service.nameMono();
    }
}
