package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

        service.namesFlux()
            .subscribe(System.out::println);

        service.nameMono()
            .subscribe(System.out::println);
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(namesList)
            .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("alex")
            .log();
    }

    public Flux<String> namesFluxMap(Integer length) {
        return Flux.fromIterable(namesList)
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .map(name -> name.length() + "-" + name)
            .log();
    }

    public Flux<String> namesFluxImmutability() {
        var namesFlux = Flux.fromIterable(namesList);

        namesFlux.map(String::toUpperCase);

        return namesFlux;
    }

    public Flux<String> namesFluxFlatMap(Integer length) {
        return Flux.fromIterable(namesList)
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .flatMap(this::split)
            .log();
    }

    public Flux<String> namesFluxFlatMapAsync(Integer length) {
        return Flux.fromIterable(namesList)
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .flatMap(this::splitWithDelay)
            .log();
    }

    public Flux<String> namesFluxConcatMap(Integer length) {
        return Flux.fromIterable(namesList)
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .concatMap(this::splitWithDelay)
            .log();
    }

    public Flux<String> namesFluxTransform(Integer length) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > length);

        return Flux.fromIterable(namesList)
            .transform(filterMap)
            .flatMap(this::split)
            .defaultIfEmpty("default")
            .log();
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(Integer length) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > length)
            .flatMap(this::split);

        Flux<String> switchFlux = Flux.just("default")
            .transform(filterMap);

        return Flux.fromIterable(namesList)
            .transform(filterMap)
            .switchIfEmpty(switchFlux)
            .log();
    }

    public Mono<String> namesMonoMapFilter(Integer length) {
        return Mono.just("alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > length);
    }

    public Mono<List<String>> namesMonoFlatMap(Integer length) {
        return Mono.just("alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .flatMap(this::splitStringMono)
            .log();
    }

    public Flux<String> namesMonoFlatMapMany(Integer length) {
        return Mono.just("alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .flatMapMany(this::split)
            .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        String[] charArray = s.split("");
        List<String> charList = List.of(charArray);

        return Mono.just(charList);
    }

    public Flux<String> split(String name) {
        String[] split = name.split("");
        return Flux.fromArray(split);
    }

    public Flux<String> splitWithDelay(String name) {
        String[] split = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(split)
            .delayElements(Duration.ofMillis(delay));
    }

    // CONCAT

    public Flux<String> exploreConcat() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux)
            .log();
    }

    public Flux<String> exploreConcatWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux)
            .log();
    }

    public Flux<String> exploreConcatWithMono() {
        Mono<String> aMono = Mono.just("A");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return aMono.concatWith(defFlux)
            .log();
    }

    // MERGE

    public Flux<String> exploreMerge() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux)
            .log();
    }

    public Flux<String> exploreMergeWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux)
            .log();
    }

    public Flux<String> exploreMergeWithMono() {
        Mono<String> aMono = Mono.just("A");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return aMono.mergeWith(defFlux)
            .log();
    }


    public Flux<String> exploreMergeSequential() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux)
            .log();
    }

    public Flux<String> exploreZip() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> String.join("", first, second))
            .log();
    }

    public Flux<String> exploreZipLarge() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        Flux<String> _123Flux = Flux.just("1", "2", "3");
        Flux<String> _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
            .map(tuple -> String.join("", tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()))
            .log();
    }

    public Flux<String> exploreZipWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> String.join("", first, second))
            .log();
    }

    public Mono<String> exploreZipWithMono() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");

        return aMono.zipWith(bMono)
            .map(tuple -> String.join("", tuple.getT1(), tuple.getT2()));
    }

    // doOn callbacks

    public Flux<String> namesDoOnNext(Integer length) {
        return Flux.fromIterable(namesList)
            .map(String::toUpperCase)
            .filter(name -> name.length() > length)
            .map(name -> name.length() + "-" + name)
            .doOnNext(System.out::println)
            .doOnSubscribe(subscription -> System.out.println("On subscription: " + subscription))
            .doOnComplete(() -> System.out.println("On complete"))
            .doFinally(signalType -> System.out.println("Do finally: " + signalType))
            .log();
    }

    // exception handling
    public Flux<String> exceptionFlux() {
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception occurred")))
            .concatWith(Flux.just("D"))
            .log();
    }

    public Flux<String> exceptionFlux_onErrorReturn() {
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception occurred")))
            .onErrorReturn("D")
            .log();
    }

    public Flux<String> exceptionFlux_onErrorResume(Exception e) {
        var recoveryFlux = Flux.just("D", "E", "F");

        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(e))
            .onErrorResume(exception -> {
                log.error("Exception is ", exception);

                if (exception instanceof IllegalStateException) {
                    return recoveryFlux;
                } else {
                    return Flux.error(exception);
                }
            })
            .log();
    }

    public Flux<String> exceptionFlux_onErrorContinue() {
        return Flux.just("A", "B", "C")
            .map((value) -> {
                if (value.equals("B")) {
                    throw new IllegalStateException("Illegal state");
                }

                return value;
            })
            .onErrorContinue((exception, value) -> {
                log.error("Exception: ", exception);
                log.info("Value: {}", value);
            })
            .log();
    }

    public Flux<String> exceptionFlux_onErrorMap() {
        return Flux.just("A", "B", "C")
            .map((value) -> {
                if (value.equals("B")) {
                    throw new IllegalStateException("Illegal state");
                }

                return value;
            })
            .onErrorMap(exception -> {
                log.error("Exception: ", exception);

                return new ReactorException(exception, exception.getMessage());
            })
            .log();
    }

    public Flux<String> exceptionFlux_doOnError() {
        return Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception occurred")))
            .doOnError(exception -> log.error("Exception", exception))
            .log();
    }

    public Mono<Object> exceptionMono() {
        return Mono.just("A")
            .map(value -> {
                throw new RuntimeException("Exception");
            })
            .onErrorReturn("ABC")
            .log();
    }

}
