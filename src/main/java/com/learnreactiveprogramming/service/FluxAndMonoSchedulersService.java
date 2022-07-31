package com.learnreactiveprogramming.service;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public Flux<String> explorePublishOn() {
        var namesFlux = Flux.fromIterable(namesList)
            .publishOn(Schedulers.parallel())
            .map(this::upperCase)
            .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
            .publishOn(Schedulers.parallel())
            .map(this::upperCase)
            .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> exploreSubscribeOn() {
        var namesFlux = fluxFromList(namesList)
            .subscribeOn(Schedulers.boundedElastic());

        var namesFlux1 = fluxFromList(namesList1)
            .subscribeOn(Schedulers.boundedElastic());

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> fluxFromList(List<String> list) {
        return Flux.fromIterable(list)
            .map(this::upperCase)
            .log();
    }
}
