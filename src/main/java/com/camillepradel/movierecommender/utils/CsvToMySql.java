package com.camillepradel.movierecommender.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class CsvToMySql {

    static final String pathToCsvFiles = "D:\\MovieRecommender\\src\\main\\java\\com\\camillepradel\\movierecommender\\utils\\";
    static final String usersCsvFile = pathToCsvFiles + "users.csv";
    static final String moviesCsvFile = pathToCsvFiles + "movies.csv";
    static final String genresCsvFile = pathToCsvFiles + "genres.csv";
    static final String movGenresCsvFile = pathToCsvFiles + "mov_genre.csv";
    static final String ratingsCsvFile = pathToCsvFiles + "ratings.csv";
    static final String friendsCsvFile = pathToCsvFiles + "friends.csv";
    static final String cvsSplitBy = ",";

    private static void commitUsers(Connection connection) throws SQLException {
        System.out.println(usersCsvFile);

        // create table
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (\n"
                + "  id int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  age int(11) NOT NULL,\n"
                + "  sex varchar(1) NOT NULL,\n"
                + "  occupation varchar(60) NOT NULL,\n"
                + "  zip varchar(6) NOT NULL,\n"
                + "  PRIMARY KEY (`id`)\n"
                + ");");

        // populate table
        try (BufferedReader br = new BufferedReader(new FileReader(usersCsvFile))) {

            String insertQuery = "INSERT INTO users (id, age, sex, occupation, zip) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertUsers = null;

            try {
                connection.setAutoCommit(false);
                insertUsers = connection.prepareStatement(insertQuery);

                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(cvsSplitBy);
                    insertUsers.setInt(1, Integer.parseInt(values[0]));
                    insertUsers.setInt(2, Integer.parseInt(values[1]));
                    insertUsers.setString(3, values[2]);
                    insertUsers.setString(4, values[3]);
                    insertUsers.setString(5, values[4]);
                    insertUsers.executeUpdate();
                }
                connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                if (connection != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        connection.rollback();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            } finally {
                if (insertUsers != null) {
                    insertUsers.close();
                }
                connection.setAutoCommit(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void commitMovies(Connection connection) throws SQLException {
        // movies.csv
        System.out.println(moviesCsvFile);

        // create table
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS movies (\n"
                + "  id int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  title varchar(200) NOT NULL,\n"
                + "  date date NOT NULL,\n"
                + "  PRIMARY KEY (`id`)\n"
                + ");");

        // populate table
        try (BufferedReader br = new BufferedReader(new FileReader(moviesCsvFile))) {

            String insertQuery = "INSERT INTO movies (id, title, date) VALUES (?, ?, ?)";
            PreparedStatement insertMovies = null;

            try {
                connection.setAutoCommit(false);
                insertMovies = connection.prepareStatement(insertQuery);

                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(cvsSplitBy);
                    int movieId = Integer.parseInt(values[0]);
                    String title = String.join(",", Arrays.copyOfRange(values, 1, values.length - 1));
                    Date date = new Date(Long.parseLong(values[values.length - 1]));
                    insertMovies.setInt(1, movieId);
                    insertMovies.setString(2, title);
                    insertMovies.setDate(3, date);
                    insertMovies.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                if (connection != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        connection.rollback();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            } finally {
                if (insertMovies != null) {
                    insertMovies.close();
                }
                connection.setAutoCommit(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void commitGenres(Connection connection) throws SQLException {
        // genres.csv
        System.out.println(genresCsvFile);

        // create table
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS genres (\n"
                + "  name varchar(60) NOT NULL,\n"
                + "  id int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  PRIMARY KEY (`id`)\n"
                + ");");

        // necessary to make mySQL accept 0 as a valid id value for genre unknown
        statement.execute("SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';");

        // populate table
        try (BufferedReader br = new BufferedReader(new FileReader(genresCsvFile))) {

            String insertQuery = "INSERT INTO genres (name, id) VALUES (?, ?)";
            PreparedStatement insertGenres = null;

            try {
                connection.setAutoCommit(false);
                insertGenres = connection.prepareStatement(insertQuery);

                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(cvsSplitBy);
                    String name = values[0];
                    int genreId = Integer.parseInt(values[1]);
                    insertGenres.setString(1, name);
                    insertGenres.setInt(2, genreId);
                    insertGenres.executeUpdate();
                }
                connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                if (connection != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        connection.rollback();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            } finally {
                if (insertGenres != null) {
                    insertGenres.close();
                }
                connection.setAutoCommit(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void commitMovGenres(Connection connection) throws SQLException {
        // mov_genre.csv
        System.out.println(movGenresCsvFile);

        // create table
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS movie_genre (\n"
                + "  movie_id int(11) NOT NULL,\n"
                + "  genre_id int(11) NOT NULL,\n"
                + "  KEY movie_id (movie_id),\n"
                + "  KEY genre_id (genre_id)\n"
                + ");");
        statement.executeUpdate("ALTER TABLE movie_genre\n"
                + "  ADD CONSTRAINT movie_genre_to_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE;\n");
        statement.executeUpdate("ALTER TABLE movie_genre\n"
                + "  ADD CONSTRAINT movie_genre_to_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE ON UPDATE CASCADE;\n");

        // populate table
        try (BufferedReader br = new BufferedReader(new FileReader(movGenresCsvFile))) {

            String insertQuery = "INSERT INTO movie_genre (movie_id, genre_id) VALUES (?, ?)";
            PreparedStatement insertGenres = null;

            try {
                connection.setAutoCommit(false);
                insertGenres = connection.prepareStatement(insertQuery);

                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(cvsSplitBy);
                    int movieId = Integer.parseInt(values[0]);
                    int genreId = Integer.parseInt(values[1]);
                    insertGenres.setInt(1, movieId);
                    insertGenres.setInt(2, genreId);
                    insertGenres.executeUpdate();
                }
                connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                if (connection != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        connection.rollback();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            } finally {
                if (insertGenres != null) {
                    insertGenres.close();
                }
                connection.setAutoCommit(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void commitRatings(Connection connection) throws SQLException {
        // ratings.csv
        System.out.println(ratingsCsvFile);
    }

    private static void commitFriends(Connection connection) throws SQLException {
        // friends.csv
        System.out.println(friendsCsvFile);
    }

    public static void main(String[] args) {
        // load JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // db connection info
        String url = "jdbc:mysql://localhost:3306/movie_recommender"
                + "?zeroDateTimeBehavior=convertToNull&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String login = "root";
        String password = "";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, login, password);
            Statement statement = connection.createStatement();

            // drop all tables
            statement.executeUpdate("DROP TABLE IF EXISTS movie_genre;");
            statement.executeUpdate("DROP TABLE IF EXISTS ratings;");
            statement.executeUpdate("DROP TABLE IF EXISTS friends;");
            statement.executeUpdate("DROP TABLE IF EXISTS users;");
            statement.executeUpdate("DROP TABLE IF EXISTS movies;");
            statement.executeUpdate("DROP TABLE IF EXISTS genres;");

            commitUsers(connection);
            commitMovies(connection);
            commitGenres(connection);
            commitMovGenres(connection);
            commitRatings(connection);
            commitFriends(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignore) {
                    // exception occured while closing connexion -> nothing else can be done
                }
            }
        }

        System.out.println("done");
    }

}
