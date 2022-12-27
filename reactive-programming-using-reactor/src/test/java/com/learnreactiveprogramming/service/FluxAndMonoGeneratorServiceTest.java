package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("Percy", "Defne", "Samurai Jack")
                // .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void namesToUpperCase() {
        //given

        //when
        var nameFlux = fluxAndMonoGeneratorService.namesToUpperCase();

        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "B", "C")
                .verifyComplete();
    }

    @Test
    void filterOutNames() {
        //given

        //when
        var nameFlux = fluxAndMonoGeneratorService.filterOutNames(5);

        //then
        StepVerifier.create(nameFlux)
                .expectNext("asdf", "rick")
                .verifyComplete();
    }
}