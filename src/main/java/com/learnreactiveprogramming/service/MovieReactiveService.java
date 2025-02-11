package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.List;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Revenue;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        return moviesInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();

                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap((exception) -> {
                log.error("Exception is: ", exception);

                return new MovieException(exception.getMessage());
            });
    }

    public Flux<Movie> getAllMovies_retry() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        return moviesInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();

                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap((exception) -> {
                log.error("Exception is: ", exception);

                return new MovieException(exception.getMessage());
            })
            .retry(3)
            .log();
    }

    public Flux<Movie> getAllMovies_retryWhen() {
        var backoff = Retry.backoff(3, Duration.ofMillis(500))
            .filter((exception) -> exception instanceof MovieException)
            .onRetryExhaustedThrow((retrySpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));

        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        return moviesInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();

                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap((exception) -> {
                log.error("Exception is: ", exception);

                if (exception instanceof NetworkException) {
                    return new MovieException(exception.getMessage());
                } else {
                    throw new ServiceException(exception.getMessage());
                }

            })
            .retryWhen(backoff)
            .log();
    }

    public Flux<Movie> getAllMovies_repeat() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        return moviesInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();

                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap((exception) -> {
                log.error("Exception is: ", exception);

                if (exception instanceof NetworkException) {
                    return new MovieException(exception.getMessage());
                } else {
                    throw new ServiceException(exception.getMessage());
                }

            })
            .repeat()
            .log();
    }

    public Flux<Movie> getAllMovies_repeatN(Long n) {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        return moviesInfoFlux
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();

                return reviewsMono
                    .map(reviews -> new Movie(movieInfo, reviews));
            })
            .onErrorMap((exception) -> {
                log.error("Exception is: ", exception);

                if (exception instanceof NetworkException) {
                    return new MovieException(exception.getMessage());
                } else {
                    throw new ServiceException(exception.getMessage());
                }

            })
            .repeat(n)
            .log();
    }

    public Mono<Movie> getMovieById(Long movieId) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        Mono<List<Review>> reviewFlux = reviewService.retrieveReviewsFlux(movieId).collectList();

        return movieInfoMono.zipWith(reviewFlux, Movie::new);
    }

    public Mono<Movie> getMovieById_withRevenue(Long movieId) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        Mono<List<Review>> reviewFlux = reviewService.retrieveReviewsFlux(movieId).collectList();
        Mono<Revenue> revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
            .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono
            .zipWith(reviewFlux, Movie::new)
            .zipWith(revenueMono, (movie, revenue) -> {
                movie.setRevenue(revenue);
                return movie;
            });
    }
}
