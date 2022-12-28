package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    static String URI = "/v1/movieInfo";

    @Test
    void getMovieInfoById() {

        String id = "asdf";
        String name = "Jimmy Neutron";
        var movieInfo = new MovieInfo(
                "asdf",
                "Jimmy Neutron",
                2007,
                null,
                null
        );

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo(name);
    }

    @Test
    void getMovieInfoByYear() {

        Integer year = 2022;
        var movieInfo = new MovieInfo(
                "asdf",
                "Jimmy Neutron",
                2022,
                List.of("Jimmy", "Albert"),
                LocalDate.parse("2022-09-11")
        );

        when(movieInfoServiceMock.getMovieInfoByYear(isA(Integer.class))).thenReturn(Flux.just(movieInfo));

        webTestClient
                .get()
                .uri(URI + "/{year}", year)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.year")
                .isEqualTo(year);
    }

    @Test
    void getAllMovieInfos() {

        var movieInfos = List.of(
                new MovieInfo(
                        null,
                        "Jimmy Neutron",
                        2007,
                        null,
                        null
                ),
                new MovieInfo(
                        null,
                        "Adventure Time",
                        2013,
                        null,
                        null
                )
        );

        when(movieInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient
                .get()
                .uri(URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo(
                "asdf",
                "Jimmy Neutron",
                2007,
                null,
                null
        );

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .post()
                .uri(URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void updateMovieInfo() {
        String id = "asdf";
        var movieInfo = new MovieInfo(
                "asdf",
                "Jimmy Neutron",
                2007,
                null,
                null
        );

        when(movieInfoServiceMock.updateMovieInfo(isA(String.class), isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .put()
                .uri(URI + "/{id}", id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    void deleteMovieInfo() {
        String id = "asdf";

        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
