package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

    private MovieInfoService movieInfoService = new MovieInfoService();
    private ReviewService reviewService = new ReviewService();
    private MovieReactiveService service = new MovieReactiveService(movieInfoService, reviewService);

    @Test
    void getAllMovies() {
        Flux<Movie> allMovies = service.getAllMovies().log();

        StepVerifier.create(allMovies)
            .assertNext(movie -> {
                assertEquals("Batman Begins", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .assertNext(movie -> {
                assertEquals("The Dark Knight", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .assertNext(movie -> {
                assertEquals("Dark Knight Rises", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .verifyComplete();
    }

    @Test
    void getMovieById() {
        Mono<Movie> movie1 = service.getMovieById(100L).log();
        Mono<Movie> movie2 = service.getMovieById(101L).log();

        StepVerifier.create(movie1)
            .assertNext(movie -> {
                assertEquals(100L, movie.getMovie().getMovieInfoId());
                assertEquals("Batman Begins", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .verifyComplete();

        StepVerifier.create(movie2)
            .assertNext(movie -> {
                assertEquals(101L, movie.getMovie().getMovieInfoId());
                assertEquals("Batman Begins", movie.getMovie().getName());
                assertEquals(2, movie.getReviewList().size());
            })
            .verifyComplete();
    }
}