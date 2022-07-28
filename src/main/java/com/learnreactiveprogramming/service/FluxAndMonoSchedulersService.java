package com.learnreactiveprogramming.service;

import java.util.List;

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
            .log()
            .subscribe(System.out::println);

        service.nameMono()
            .log()
            .subscribe(System.out::println);
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(namesList);
    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

}
