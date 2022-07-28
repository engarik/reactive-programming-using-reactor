package com.learnreactiveprogramming.functional;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionalExample {
    public static void main(String[] args) {
        var names = List.of("alex", "ben", "chloe", "adam", "adam");
        var newNames = namesGreaterThanSize(names, 3);
        System.out.println(newNames);
    }

    private static List<String> namesGreaterThanSize(List<String> names, int size) {
        return names.parallelStream()
            .filter(name -> name.length() > size)
            .distinct()
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
    }
}
