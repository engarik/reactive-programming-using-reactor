package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();

    @Test
    void explorePublishOn() {
        var flux = service.explorePublishOn();

        StepVerifier.create(flux)
            .expectNextCount(6)
            .verifyComplete();
    }

    @Test
    void exploreSubscribeOn() {
        var flux = service.exploreSubscribeOn();

        StepVerifier.create(flux)
            .expectNextCount(6)
            .verifyComplete();
    }
}