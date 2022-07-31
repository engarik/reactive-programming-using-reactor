package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieReactiveServiceMockTest {

    @Mock
    MovieInfoService movieInfoService;

    @Mock
    ReviewService reviewService;

    @InjectMocks
    MovieReactiveService reactiveMovieService;

    @Test
    void getAllMovieInfo() {
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        var movieFlux  =  reactiveMovieService.getAllMovies();

        StepVerifier.create(movieFlux)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void getAllMovieInfo_error() {
        var errorMessage = "Exception occurred";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        var movieFlux = reactiveMovieService.getAllMovies();

        StepVerifier.create(movieFlux)
            //.expectError(MovieException.class)
            //.expectErrorMessage(errorMessage)
            .expectErrorSatisfies((exception) -> {
                assertEquals(errorMessage, exception.getMessage());
            })
            .verify();

    }

}
