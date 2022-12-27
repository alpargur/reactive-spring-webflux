package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest // spins up MongoDB for integration test
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test") // uses given profile configs for instantiation
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(
                new MovieInfo(
                        "null",
                        "Batman Begins",
                        2055,
                        List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")
                ),
                new MovieInfo(
                        "asdf",
                        "Batman The Dark Knight",
                        2008,
                        List.of("Christian Bale", "Heathledger"),
                        LocalDate.parse("2008-07-18")
                ),
                new MovieInfo(
                        "qwerty",
                        "Batman The Dark Knight Rises",
                        2012,
                        List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")
                )
        );

        movieInfoRepository.saveAll(movieInfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll()
                .block();
    }

    @Test
    void findAll() {

        var moviesInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        var moviesInfoMono = movieInfoRepository.findById("null").log();

        StepVerifier.create(moviesInfoMono)
                .assertNext( movieInfo -> assertEquals("Batman Begins", movieInfo.getName()))
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        var movieInfo = new MovieInfo(
                null,
                "Spider-Man 1",
                2002,
                List.of("Tobey Maguire", "Willem Dafoe"),
                LocalDate.parse("2002-05-02")
        );

        var moviesInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfoSaved -> {
                    assertEquals("Spider-Man 1", movieInfoSaved.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        String newMovieName = "New Name";

        var movieInfo = movieInfoRepository
                .findById("qwerty")
                .block();

        movieInfo.setName(newMovieName);

        var movieInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfoUpdated -> {
                    assertEquals(newMovieName, movieInfoUpdated.getName());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {

        movieInfoRepository.deleteById("qwerty")
                .block();

        var movieInfoFlux = movieInfoRepository.findAll();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }
}