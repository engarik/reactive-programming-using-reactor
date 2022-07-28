package com.learnreactiveprogramming.imperative;

import java.util.ArrayList;
import java.util.List;

public class ImperativeExample {
    public static void main(String[] args) {
        var names = List.of("alex", "ben", "chloe", "adam", "adam");
        var newNames = namesGreaterThanSize(names, 3);
        System.out.println(newNames);
    }

    private static List<String> namesGreaterThanSize(List<String> names, int size) {
        var result = new ArrayList<String>();

        for (String name : names) {
            if (name.length() > size && !result.contains(name.toUpperCase())) {
                result.add(name.toUpperCase());
            }
        }

        return result;
    }
}
