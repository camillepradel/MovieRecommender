package com.camillepradel.movierecommender.model.db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import java.util.List;

public interface Database {
    abstract List<Movie> getAllMovies();
    abstract List<Movie> getMoviesRatedByUser(int userId);
    abstract List<Rating> getRatingsFromUser(int userId);
    abstract void addOrUpdateRating(Rating rating);
    abstract List<Rating> processRecommendationsForUser(int userId, int processingMode);
}
