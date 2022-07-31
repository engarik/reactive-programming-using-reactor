package com.learnreactiveprogramming.service;

import java.util.List;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

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

    // exception handling

    @Test
    void exceptionFlux() {
        var exceptionFlux = service.exceptionFlux();

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C")
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void exceptionFluxOne() {
        var exceptionFlux = service.exceptionFlux();

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C")
            .expectError()
            .verify();
    }

    @Test
    void exceptionFluxTwo() {
        var exceptionFlux = service.exceptionFlux();

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C")
            .expectErrorMessage("Exception occurred")
            .verify();
    }

    @Test
    void exceptionFlux_onErrorReturn() {
        var exceptionFlux = service.exceptionFlux_onErrorReturn();


        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C", "D")
            .verifyComplete();
    }

    @Test
    void exceptionFlux_onErrorResume() {
        var e = new IllegalStateException("Not a valid state");
        var exceptionFlux = service.exceptionFlux_onErrorResume(e);

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exceptionFlux_onErrorResumeFail() {
        var e = new RuntimeException("Runtime exception");
        var exceptionFlux = service.exceptionFlux_onErrorResume(e);

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C")
            .verifyError(RuntimeException.class);
    }

    @Test
    void exceptionFlux_onErrorContinue() {
        var exceptionFlux = service.exceptionFlux_onErrorContinue();

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "C")
            .verifyComplete();
    }

    @Test
    void exceptionFlux_onErrorMap() {
        var exceptionFlux = service.exceptionFlux_onErrorMap();

        StepVerifier.create(exceptionFlux)
            .expectNext("A")
            .expectError(ReactorException.class)
            .verify();
    }

    @Test
    void exceptionFlux_doOnError() {
        var exceptionFlux = service.exceptionFlux_doOnError();

        StepVerifier.create(exceptionFlux)
            .expectNext("A", "B", "C")
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void exceptionMono() {
        var exceptionFlux = service.exceptionMono();

        StepVerifier.create(exceptionFlux)
            .expectNext("ABC")
            .verifyComplete();
    }

}
