package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/* This repository enables to interact with MongoDB in reactive fashion */
/* ReactiveMongoDBRepository interface contains methods to interact with MongoDB */
/* MovieInfoRepository extends it and accepts MovieInfo Document */
/* Which has as "Id" type "String" */

public interface MovieInfoRepository extends ReactiveMongoRepository <MovieInfo,String> {

    Flux<MovieInfo> findByYear(Integer year);
    Flux<MovieInfo> findByName(String name);
}
