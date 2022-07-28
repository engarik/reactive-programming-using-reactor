package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public static void main(String[] args) {
        FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();

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

}
