package com.camillepradel.movierecommender.model.db;

import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.camillepradel.movierecommender.model.Genre;
import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import com.camillepradel.movierecommender.utils.MySQL;

public class MySqlDatabase implements Database {

    Connection connection = null;

    /**
     * Creates a proxy to interact with MySQL database.
     * 
     * @throws IllegalArgumentException if the connection cannot be initialized.
     */
    public MySqlDatabase() {
        // load JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // TODO [Refactor] The URL, login & password to MySQL should be given as parameters
            connection = DriverManager.getConnection(MySQL.URL, MySQL.LOGIN, MySQL.PASSWORD);
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new IllegalStateException("The connection with MySQL cannot be initialized", e);
        }
    }

    HashMap<Integer, Genre> getAllGenres() throws SQLException {
        HashMap<Integer, Genre> allGenres = new HashMap<>();

        String selectGenresSQL = "SELECT genre.id AS genre_id, genre.name AS genre_name\n"
                			   + "FROM genres genre";
        
        try (
        	Statement statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(selectGenresSQL);
		) {
	        while (rs.next()) {
	            int genreId = rs.getInt("genre_id");
	            String genreName = rs.getString("genre_name");
	            allGenres.put(genreId, new Genre(genreId, genreName));
	        }
	        
	        return allGenres;
        }
    }

    @Override
    public List<Movie> getAllMovies() {
        List<Movie> movies = new LinkedList<>();

        if (connection != null) {
            String selectMoviesSQL = 
            		  "SELECT movie.id AS movie_id, movie.title AS movie_title, GROUP_CONCAT(mg.genre_id) AS genre_ids\n"
                    + "from movies movie, movie_genre mg\n"
                    + "WHERE mg.movie_id = movie.id\n"
                    + "GROUP BY movie.id, movie.title";
                
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery(selectMoviesSQL);
                while (rs.next()) {
                    int movieId = rs.getInt("movie_id");
                    String movieTitle = rs.getString("movie_title");
                    String genreIds = rs.getString("genre_ids");
                    
                    List<Genre> movieGenres = new LinkedList<>();
                    HashMap<Integer, Genre> allGenres = getAllGenres();
                    
                    for (String genreId : genreIds.split(",")) {
                        movieGenres.add(allGenres.get(Integer.parseInt(genreId)));
                    }
                    movies.add(new Movie(movieId, movieTitle, movieGenres));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return movies;
    }

    @Override
    public List<Movie> getMoviesRatedByUser(int userId) {
        List<Movie> movies = new LinkedList<>();

        if (connection != null) {
            String selectMoviesSQL = 
            		  "SELECT movie.id AS movie_id, movie.title AS movie_title, GROUP_CONCAT(mg.genre_id) AS genre_ids\n"
                    + "FROM movies movie, movie_genre mg, ratings rating\n"
                    + "WHERE rating.user_id = ? AND rating.movie_id = movie.id AND mg.movie_id = movie.id\n"
                    + "GROUP BY movie.id, movie.title";
            
           try (PreparedStatement statement = connection.prepareStatement(selectMoviesSQL)) {
                statement.setInt(1, userId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    int movieId = rs.getInt("movie_id");
                    String movieTitle = rs.getString("movie_title");
                    String genreIds = rs.getString("genre_ids");
                    
                    List<Genre> movieGenres = new LinkedList<>();
                    HashMap<Integer, Genre> allGenres = getAllGenres();
                    
                    for (String genreId : genreIds.split(",")) {
                        movieGenres.add(allGenres.get(Integer.parseInt(genreId)));
                    }
                    movies.add(new Movie(movieId, movieTitle, movieGenres));
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return movies;
    }

    @Override
    public List<Rating> getRatingsFromUser(int userId) {
        List<Rating> ratings = new LinkedList<>();

        if (connection != null) {
        	String selectMoviesSQL = 
        			"SELECT movie.id AS movie_id, movie.title AS movie_title, GROUP_CONCAT(mg.genre_id) AS genre_ids, rating.rating AS rating\n" +
        			"FROM movies movie, movie_genre mg, ratings rating\n" +
        			"WHERE rating.user_id = ? AND rating.movie_id = movie.id AND mg.movie_id = movie.id\n" +
        			"GROUP BY movie.id, movie.title, rating.rating";
        	
            try (PreparedStatement statement = connection.prepareStatement(selectMoviesSQL)) {
                statement.setInt(1, userId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    int movieId = rs.getInt("movie_id");
                    String movieTitle = rs.getString("movie_title");
                    int rating = rs.getInt("rating");
                    String genreIds = rs.getString("genre_ids");
                    
                    List<Genre> movieGenres = new LinkedList<>();
                    HashMap<Integer, Genre> allGenres = getAllGenres();
                    
                    for (String genreId : genreIds.split(",")) {
                        movieGenres.add(allGenres.get(Integer.parseInt(genreId)));
                    }
                    ratings.add(new Rating(new Movie(movieId, movieTitle, movieGenres), userId, rating));
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ratings;
    }

    @Override
    public void addOrUpdateRating(Rating rating) {
        if (connection != null) {
        	String replaceRatingsSQL = "REPLACE INTO ratings (user_id, movie_id, rating, date) VALUES (?, ?, ?, CURDATE());";
            try (PreparedStatement statement = connection.prepareStatement(replaceRatingsSQL)) {
                statement.setInt(1, rating.getUserId());
                statement.setInt(2, rating.getMovieId());
                statement.setInt(3, rating.getScore());
                statement.executeUpdate();
                
            } catch (SQLException ex) {
                Logger.getLogger(MySqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public List<Rating> processRecommendationsForUser(int userId, int processingMode) {
        // process recommendations for specified user exploiting other users ratings
        //       use different methods depending on processingMode parameter
        Genre genre0 = new Genre(0, "genre0");
        Genre genre1 = new Genre(1, "genre1");
        Genre genre2 = new Genre(2, "genre2");
        
        List<Rating> recommendations = new LinkedList<>();
        
        String titlePrefix = "default_";
        
        if (processingMode == 0) {
            titlePrefix = "0_";
        } else if (processingMode == 1) {
            titlePrefix = "1_";
        } else if (processingMode == 2) {
            titlePrefix = "2_";
        }
        
        recommendations.add(new Rating(new Movie(0, titlePrefix + "Titre 0", asList(genre0, genre1)), userId, 5));
        recommendations.add(new Rating(new Movie(1, titlePrefix + "Titre 1", asList(genre0, genre2)), userId, 5));
        recommendations.add(new Rating(new Movie(2, titlePrefix + "Titre 2", asList(genre1)), userId, 4));
        recommendations.add(new Rating(new Movie(3, titlePrefix + "Titre 3", asList(genre0, genre1, genre2)), userId, 3));
        
        return recommendations;
    }

}
