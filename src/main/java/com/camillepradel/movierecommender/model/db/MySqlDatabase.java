package com.camillepradel.movierecommender.model.db;

import com.camillepradel.movierecommender.model.Genre;
import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySqlDatabase extends AbstractDatabase {

    Connection connection = null;

    // db connection info
    String url = "jdbc:mysql://192.168.56.101:3306/movie_recommender"
            + "?zeroDateTimeBehavior=convertToNull&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    String login = "root";
    String password = "root";

    public MySqlDatabase() {
        // load JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    HashMap<Integer, Genre> getAllGenres() throws SQLException {
        HashMap<Integer, Genre> allGenres = new HashMap<>();

        String selectGenresSQL = "SELECT genre.id AS genre_id, genre.name AS genre_name\n"
                + "FROM genres genre";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(selectGenresSQL);
        while (rs.next()) {
            int genreId = rs.getInt("genre_id");
            String genreName = rs.getString("genre_name");
            allGenres.put(genreId, new Genre(genreId, genreName));
        }
        return allGenres;
    }

    @Override
    public List<Movie> getAllMovies() {
        List<Movie> movies = new LinkedList<Movie>();

        if (connection != null) {
            try {
                HashMap<Integer, Genre> allGenres = getAllGenres();

                String selectMoviesSQL = "SELECT movie.id AS movie_id, movie.title AS movie_title, GROUP_CONCAT(mg.genre_id) AS genre_ids\n"
                        + "from movies movie, movie_genre mg\n"
                        + "WHERE mg.movie_id = movie.id\n"
                        + "GROUP BY movie.id, movie.title";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(selectMoviesSQL);
                while (rs.next()) {
                    int movieId = rs.getInt("movie_id");
                    String movieTitle = rs.getString("movie_title");
                    String genreIds = rs.getString("genre_ids");
                    List<Genre> movieGenres = new LinkedList<>();
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
        List<Movie> movies = new LinkedList<Movie>();

        if (connection != null) {
            try {
                HashMap<Integer, Genre> allGenres = getAllGenres();

                String selectMoviesSQL = "SELECT movie.id AS movie_id, movie.title AS movie_title, GROUP_CONCAT(mg.genre_id) AS genre_ids\n"
                        + "FROM movies movie, movie_genre mg, ratings rating\n"
                        + "WHERE rating.user_id = ? AND rating.movie_id = movie.id AND mg.movie_id = movie.id\n"
                        + "GROUP BY movie.id, movie.title";
                PreparedStatement statement = connection.prepareStatement(selectMoviesSQL);
                statement.setInt(1, userId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    int movieId = rs.getInt("movie_id");
                    String movieTitle = rs.getString("movie_title");
                    String genreIds = rs.getString("genre_ids");
                    List<Genre> movieGenres = new LinkedList<>();
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
        List<Rating> ratings = new LinkedList<Rating>();

        if (connection != null) {
            try {
                HashMap<Integer, Genre> allGenres = getAllGenres();

                String selectMoviesSQL = "SELECT movie.id AS movie_id, movie.title AS movie_title, GROUP_CONCAT(mg.genre_id) AS genre_ids, rating.rating AS rating\n" +
                                        "FROM movies movie, movie_genre mg, ratings rating\n" +
                                        "WHERE rating.user_id = ? AND rating.movie_id = movie.id AND mg.movie_id = movie.id\n" +
                                        "GROUP BY movie.id, movie.title, rating.rating";
                PreparedStatement statement = connection.prepareStatement(selectMoviesSQL);
                statement.setInt(1, userId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    int movieId = rs.getInt("movie_id");
                    String movieTitle = rs.getString("movie_title");
                    int rating = rs.getInt("rating");
                    String genreIds = rs.getString("genre_ids");
                    List<Genre> movieGenres = new LinkedList<>();
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
            try {
                String replaceRatingsSQL = "REPLACE INTO ratings (user_id, movie_id, rating, date) VALUES (?, ?, ?, CURDATE());";
                PreparedStatement statement = connection.prepareStatement(replaceRatingsSQL);
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
        List<Rating> recommendations = new LinkedList<Rating>();
        String titlePrefix;
        if (processingMode == 0) {
            titlePrefix = "0_";
        } else if (processingMode == 1) {
            titlePrefix = "1_";
        } else if (processingMode == 2) {
            titlePrefix = "2_";
        } else {
            titlePrefix = "default_";
        }
        recommendations.add(new Rating(new Movie(0, titlePrefix + "Titre 0", Arrays.asList(new Genre[]{genre0, genre1})), userId, 5));
        recommendations.add(new Rating(new Movie(1, titlePrefix + "Titre 1", Arrays.asList(new Genre[]{genre0, genre2})), userId, 5));
        recommendations.add(new Rating(new Movie(2, titlePrefix + "Titre 2", Arrays.asList(new Genre[]{genre1})), userId, 4));
        recommendations.add(new Rating(new Movie(3, titlePrefix + "Titre 3", Arrays.asList(new Genre[]{genre0, genre1, genre2})), userId, 3));
        return recommendations;
    }

}
