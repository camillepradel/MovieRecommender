package com.camillepradel.movierecommender.model.db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import java.util.List;

public abstract class AbstractDatabase {
    public abstract List<Movie> getAllMovies();
    public abstract List<Movie> getMoviesRatedByUser(int userId);
    public abstract List<Rating> getRatingsFromUser(int userId);
    public abstract void addOrUpdateRating(Rating rating);
    public abstract List<Rating> processRecommendationsForUser(int userId, int processingMode);
}
