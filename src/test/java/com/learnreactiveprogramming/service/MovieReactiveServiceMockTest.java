package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    void getAllMovies_error() {
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

    @Test
    void getAllMovies_retry() {
        var errorMessage = "Exception occurred";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        var movieFlux = reactiveMovieService.getAllMovies_retry();

        StepVerifier.create(movieFlux)
            //.expectError(MovieException.class)
            //.expectErrorMessage(errorMessage)
            .expectErrorSatisfies((exception) -> {
                assertEquals(errorMessage, exception.getMessage());
            })
            .verify();

        verify(reviewService, times(4))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_retryWhen() {
        var errorMessage = "Exception occurred";
        when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenThrow(new NetworkException(errorMessage));

        var movieFlux = reactiveMovieService.getAllMovies_retryWhen();

        StepVerifier.create(movieFlux)
            .expectErrorSatisfies((exception) -> {
                assertEquals(errorMessage, exception.getMessage());
            })
            .verify();

        verify(reviewService, times(4))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_retryWhenNotHappened() {
        var errorMessage = "Exception occurred";
        when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenThrow(new ServiceException(errorMessage));

        var movieFlux = reactiveMovieService.getAllMovies_retryWhen();

        StepVerifier.create(movieFlux)
            .expectErrorSatisfies((exception) -> {
                assertEquals(errorMessage, exception.getMessage());
            })
            .verify();

        verify(reviewService, times(1))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_repeat() {
        when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenCallRealMethod();

        var movieFlux = reactiveMovieService.getAllMovies_repeat();

        StepVerifier.create(movieFlux)
            .expectNextCount(6)
            .thenCancel()
            .verify();

        verify(reviewService, times(6))
            .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_repeatN() {
        when(movieInfoService.retrieveMoviesFlux())
            .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
            .thenCallRealMethod();

        var numOfTimes = 2L;

        var movieFlux = reactiveMovieService.getAllMovies_repeatN(numOfTimes);

        StepVerifier.create(movieFlux)
            .expectNextCount(9)
            .verifyComplete();

        verify(reviewService, times(9))
            .retrieveReviewsFlux(isA(Long.class));
    }

}
