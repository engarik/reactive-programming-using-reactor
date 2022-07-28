package com.learnreactiveprogramming.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();

    @Test
    void namesFlux() {
        var namesFlux = service.namesFlux();

        StepVerifier.create(namesFlux)
            .expectNext("alex", "ben", "chloe")
            //.expectNextCount(3)
            //.expectNext("alex")
            //.expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        var namesFluxMap = service.namesFluxMap(3);

        StepVerifier.create(namesFluxMap)
            .expectNext("4-ALEX", "5-CHLOE")
            .verifyComplete();
    }

    @Test
    void namesFluxMapImmutability() {
        var namesFluxMap = service.namesFluxImmutability();

        StepVerifier.create(namesFluxMap)
            .expectNext("alex", "ben", "chloe")
            .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        var namesFluxMap = service.namesFluxFlatMap(3);

        StepVerifier.create(namesFluxMap)
            .expectNext("A", "L", "E", "X")
            .expectNext("C", "H", "L", "O", "E")
            .verifyComplete();
    }

    @Test
    void namesFluxAsync() {
        var namesFluxMap = service.namesFluxFlatMapAsync(3);

        StepVerifier.create(namesFluxMap)
            //.expectNext("A", "L", "E", "X")
            //.expectNext("C", "H", "L", "O", "E")
            .expectNextCount(9)
            .verifyComplete();
    }

    @Test
    void namesConcatMap() {
        var namesFluxMap = service.namesFluxConcatMap(3);

        StepVerifier.create(namesFluxMap)
            .expectNext("A", "L", "E", "X")
            .expectNext("C", "H", "L", "O", "E")
            .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        var namesMonoFlatMap = service.namesMonoFlatMap(3);

        StepVerifier.create(namesMonoFlatMap)
            .expectNext(List.of("A", "L", "E", "X"))
            .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        var namesMonoFlatMapMany = service.namesMonoFlatMapMany(3);

        StepVerifier.create(namesMonoFlatMapMany)
            .expectNext("A", "L", "E", "X")
            .verifyComplete();
    }

    @Test
    void namesMonoFlatTransform() {
        var namesFluxMap = service.namesFluxTransform(3);

        StepVerifier.create(namesFluxMap)
            .expectNext("A", "L", "E", "X")
            .expectNext("C", "H", "L", "O", "E")
            .verifyComplete();
    }

    @Test
    void namesMonoFlatTransformOne() {
        var namesFluxMap = service.namesFluxTransform(6);

        StepVerifier.create(namesFluxMap)
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        var namesFluxMap = service.namesFluxTransformSwitchIfEmpty(6);

        StepVerifier.create(namesFluxMap)
            .expectNext("D", "E", "F", "A", "U", "L", "T")
            .verifyComplete();
    }

    @Test
    void exploreConcat() {
        var exploreConcatFlux = service.exploreConcat();

        StepVerifier.create(exploreConcatFlux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreMerge() {
        var flux = service.exploreMerge();

        StepVerifier.create(flux)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        var flux = service.exploreMergeSequential();

        StepVerifier.create(flux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    // ZIP

    @Test
    void exploreZip() {
        var flux = service.exploreZip();

        StepVerifier.create(flux)
            .expectNext("AD", "BE", "CF")
            .verifyComplete();
    }

    @Test
    void exploreZipLarge() {
        var flux = service.exploreZipLarge();

        StepVerifier.create(flux)
            .expectNext("AD14", "BE25", "CF36")
            .verifyComplete();
    }

    @Test
    void namesFluxDoOnNext() {
        var namesFluxMap = service.namesDoOnNext(3);

        StepVerifier.create(namesFluxMap)
            .expectNext("4-ALEX", "5-CHLOE")
            .verifyComplete();
    }


}
