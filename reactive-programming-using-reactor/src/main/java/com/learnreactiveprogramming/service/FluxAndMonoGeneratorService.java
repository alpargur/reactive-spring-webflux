package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Collection<String> getNames() {
        return List.of("Percy", "Mango", "Fenerbahce", "Rick");
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(getNames());
    }

    public Mono<String> namesMono() {
        return Mono.just("Peercy");
    }

    public Flux<String> namesFluxWithLog() {
        return Flux.fromIterable(List.of("a", "b", "c")).log();
    }

    public Flux<String> namesToUpperCase() {
        return Flux.fromIterable(List.of("a", "b", "c"))
                .map(String::toUpperCase);
                // .map(str -> str.toUpperCase);
    }

    public Flux<String> filterOutNames(int length) {
        return Flux.fromIterable((getNames()))
                .filter(str -> str.length() < length);
    }

    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }


    public Flux<String> getCharsOfNames() {
        return Flux.fromIterable(getNames())
                .filter(str -> str.length() < 5)
                .flatMap(str -> splitString(str));
    }

    public Flux<String> getCharsOfNamesV2() {
        return Mono.just("Michael")
                .flatMapMany(this::splitString)
                .log();
    }

    public Flux<String> splitStringDelayed(String name) {
        var charArray = name.split("");
        Random random = new Random();
        int delay = random.nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay))
                .log();
    }

    public Flux<String> getCharsOfNamesInOrder() {
        return Flux.fromIterable(getNames())
                .filter(str -> str.length() < 3)
                .concatMap(str -> splitStringDelayed(str))
                .log();
    }

    public Flux<String> useTransform(int length) {
        // this Function Functional Interface can be used from other methods that require same step
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toLowerCase)
                .filter(str -> str.length() > length);

        var ifEmptyFlux = Flux.just("default flux")
                .transform(filterMap);

        return Flux.fromIterable(getNames())
                .transform(filterMap)
                //.defaultIfEmpty("default value")
                .switchIfEmpty(ifEmptyFlux)
                .log();
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");

        return Flux.concat(abcFlux, defFlux);
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");

        return abcFlux.concatWith(defFlux);
    }

    public Flux<String> exploreMerge() {
        var xyzFlux = Flux.just("x", "y", "z")
                .delayElements(Duration.ofMillis(100));
        var asdFlux = Flux.just("a", "s", "d")
                .delayElements(Duration.ofMillis(120));

        return Flux.merge(xyzFlux, asdFlux)
                .log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("a", "b", "c");
        var xyzFlux = Flux.just("x", "y", "z");
        var flux123 = Flux.just(1, 2, 3);

        return Flux.zip(abcFlux, flux123, xyzFlux)
                .map(t3 -> t3.getT1() + t3.getT2() + t3.getT3())
                .log();
    }

    public Mono<String> exploreZipWith() {
        var aMono = Mono.just("a");
        var bMono = Mono.just("b");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

/*        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Hi, my name is " + name); // consume values one by one
                });

        fluxAndMonoGeneratorService.namesMono()
                .subscribe(name -> {
                    System.out.println("Hello bitches, it's your boi " + name);
                });

        fluxAndMonoGeneratorService.namesFluxWithLog()
                .subscribe(name -> {
                    System.out.println(name);
                });

        fluxAndMonoGeneratorService.getCharsOfNames()
                .subscribe(System.out::println);

        fluxAndMonoGeneratorService.useTransform(4)
                .subscribe(System.out::println);*/

        fluxAndMonoGeneratorService.exploreZipWith()
                .subscribe(System.out::println);
    }
}
