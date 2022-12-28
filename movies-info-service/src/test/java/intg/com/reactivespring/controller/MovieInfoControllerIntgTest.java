package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test")
@AutoConfigureWebTestClient // allows integration with the client
class MovieInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String URI = "/v1/movieInfo";

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(
                new MovieInfo(
                        "null",
                        "Batman Begins",
                        2005,
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

        movieInfoRepository
                .saveAll(movieInfos)
                .blockLast();
    }
    @AfterEach
    void tearDown() {
        movieInfoRepository
                .deleteAll()
                .block();
    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo(
                null,
                "Samurai Jack",
                2009,
                List.of("Aku", "Jack"),
                LocalDate.parse("2009-11-17")
        );

        webTestClient
                .post()
                .uri(URI)
                .bodyValue(movieInfo)
                .exchange() // make call to the endpoint
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoSaved = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoSaved != null;
                    assert movieInfoSaved.getMovieInfoId() != null;
                });
    }

    @Test
    void getAllMovieInfos() {

        webTestClient
                .get()
                .uri(URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        String id = "asdf";
        String name = "Batman The Dark Knight";

        webTestClient
                .get()
                .uri(URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name") // get name attribute from the json response body
                .isEqualTo(name);
    }

    @Test
    void updateMovieInfo() {
        String id = "asdf";
        var updatedMovieInfo = new MovieInfo(
                null,
                "Batman The Dark Knight",
                2022,
                List.of("Christian Bale", "Alpar"),
                LocalDate.parse("2022-12-12")
        );

        webTestClient
                .put()
                .uri(URI + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.year")
                .isEqualTo(2022);
    }

    @Test
    void deleteMovieInfo() {
        String id = "asdf";

        webTestClient
                .delete()
                .uri(URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void addMovieInfoValidation() {
        var movieInfo = new MovieInfo(
                null,
                "",
                -2,
                List.of("Harry Kane", "Neymar Jr"),
                LocalDate.parse("2002-12-23")
        );

        webTestClient
                .post()
                .uri(URI)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("Bad Request");
    }

    @Test
    void updateMovieInfoNotFound() {
        String id = "xxx";
        var movieInfoUpdated = new MovieInfo(
                "xxx",
                "Jimmy Neutron",
                2007,
                null,
                null
        );

        webTestClient
                .put()
                .uri(URI + "/{id}", id)
                .bodyValue(movieInfoUpdated)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMovieInfoByIdNotFound() {
        String id = "xxx";

        webTestClient
                .get()
                .uri(URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMovieInfoByYear() {
        Integer year = 2012;

        var uri = UriComponentsBuilder.fromUriString(URI)
                .queryParam("year", year)
                .buildAndExpand()
                .toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoByName() {

        String name = "Batman The Dark Knight";

        var uri = UriComponentsBuilder.fromUriString(URI)
                .queryParam("name", name)
                .buildAndExpand()
                .toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }
}