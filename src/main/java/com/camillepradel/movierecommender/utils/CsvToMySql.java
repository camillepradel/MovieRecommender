package com.camillepradel.movierecommender.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class CsvToMySql {

    static final Path RESOURCES = Paths.get(new File("./src/main/resources/").getAbsolutePath());
    
    static final File USERS_CSV_FILE = RESOURCES.resolve("users.csv").toFile();
    static final File MOVIES_CSV_FILE = RESOURCES.resolve("movies.csv").toFile();
    static final File GENRES_CSV_FILE = RESOURCES.resolve("genres.csv").toFile();
    static final File MOVIE_GENRES_FILE = RESOURCES.resolve("mov_genre.csv").toFile();
    static final File RATINGS_CSV_FILE = RESOURCES.resolve("ratings.csv").toFile();
    static final File FRIENDS_CSV_FILE = RESOURCES.resolve("friends.csv").toFile();
    static final String CSV_SEPARATOR = ",";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // load JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (
    		Connection connection = DriverManager.getConnection(MySQL.URL, MySQL.LOGIN, MySQL.PASSWORD);
        	Statement statement = connection.createStatement();
		) {
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
            commitMovieGenre(connection);
            commitRatings(connection);
            commitFriends(connection);
        }
    }

    private static void commitUsers(Connection connection) throws SQLException {
        // populate table
        try (Statement statement = connection.createStatement()) {
        	
        	// create table
        	statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (\n"
        			+ "  id int(11) NOT NULL AUTO_INCREMENT,\n"
        			+ "  age int(11) NOT NULL,\n"
        			+ "  sex varchar(1) NOT NULL,\n"
        			+ "  occupation varchar(60) NOT NULL,\n"
        			+ "  zip varchar(6) NOT NULL,\n"
        			+ "  PRIMARY KEY (`id`)\n"
        			+ ");");
        	
            String insertQuery = "INSERT INTO users (id, age, sex, occupation, zip) VALUES (?, ?, ?, ?, ?)";

            try (
        		BufferedReader br = new BufferedReader(new FileReader(USERS_CSV_FILE));
        		PreparedStatement insertUsers = connection.prepareStatement(insertQuery);
			) {
            	connection.setAutoCommit(false);

                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(CSV_SEPARATOR);
                    insertUsers.setInt(1, Integer.parseInt(values[0]));
                    insertUsers.setInt(2, Integer.parseInt(values[1]));
                    insertUsers.setString(3, values[2]);
                    insertUsers.setString(4, values[3]);
                    insertUsers.setString(5, values[4]);
                    insertUsers.executeUpdate();
                }
                connection.commit();
                
            } catch (IOException | SQLException e) {
            	System.err.println("Cannot insert users ; transaction is being roll-backed");
            	e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void commitMovies(Connection connection) throws SQLException {
        // create table
        try (Statement statement = connection.createStatement()) {
        	
	        statement.executeUpdate("CREATE TABLE IF NOT EXISTS movies (\n"
				                + "  id int(11) NOT NULL AUTO_INCREMENT,\n"
				                + "  title varchar(200) NOT NULL,\n"
				                + "  date date NOT NULL,\n"
				                + "  PRIMARY KEY (`id`)\n"
				                + ");");
	        
	        String insertQuery = "INSERT INTO movies (id, title, date) VALUES (?, ?, ?)";
	
	        // populate table
	        try (
        		BufferedReader br = new BufferedReader(new FileReader(MOVIES_CSV_FILE));
        		PreparedStatement insertMovies = connection.prepareStatement(insertQuery);
    		) {
                connection.setAutoCommit(false);

                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(CSV_SEPARATOR);
                    int movieId = Integer.parseInt(values[0]);
                    String title = String.join(",", Arrays.copyOfRange(values, 1, values.length - 1));
                    Date date = new Date(Long.parseLong(values[values.length - 1]) * 1000);
                    insertMovies.setInt(1, movieId);
                    insertMovies.setString(2, title);
                    insertMovies.setDate(3, date);
                    insertMovies.executeUpdate();
                }
                connection.commit();
            } 
	        catch (IOException | SQLException e) {
	        	System.err.println("Cannot insert movies ; transaction is being roll-backed");
            	e.printStackTrace();
                connection.rollback();
            } 
	        finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void commitGenres(Connection connection) throws SQLException {
        // create table
    	try (Statement statement = connection.createStatement()) {
	        statement.executeUpdate("CREATE TABLE IF NOT EXISTS genres (\n"
	                + "  name varchar(60) NOT NULL,\n"
	                + "  id int(11) NOT NULL AUTO_INCREMENT,\n"
	                + "  PRIMARY KEY (`id`)\n"
	                + ");");
	
	        // necessary to make mySQL accept 0 as a valid id value for genre unknown
	        statement.execute("SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';");
	        
	        String insertQuery = "INSERT INTO genres (name, id) VALUES (?, ?)";
	
	        // populate table
	        try (
        		BufferedReader br = new BufferedReader(new FileReader(GENRES_CSV_FILE));
        		PreparedStatement insertGenres = connection.prepareStatement(insertQuery);
    		) {
                connection.setAutoCommit(false);
	
                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(CSV_SEPARATOR);
                    String name = values[0];
                    int genreId = Integer.parseInt(values[1]);
                    insertGenres.setString(1, name);
                    insertGenres.setInt(2, genreId);
                    insertGenres.executeUpdate();
                }
                connection.commit();
	
            } catch (IOException | SQLException e) {
            	System.err.println("Cannot insert genres ; transaction is being roll-backed");
            	e.printStackTrace();
                connection.rollback();
            } finally {
            	connection.setAutoCommit(true);
            }
    	}
    }

    private static void commitMovieGenre(Connection connection) throws SQLException {
        // create table
        try (Statement statement = connection.createStatement()) {
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
	        
	        String insertQuery = "INSERT INTO movie_genre (movie_id, genre_id) VALUES (?, ?)";
	
	        // populate table
	        try (
        		BufferedReader br = new BufferedReader(new FileReader(MOVIE_GENRES_FILE));
	        	PreparedStatement insertMovieGenre = connection.prepareStatement(insertQuery);
    		) {
                connection.setAutoCommit(false);
	
                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(CSV_SEPARATOR);
                    int movieId = Integer.parseInt(values[0]);
                    int genreId = Integer.parseInt(values[1]);
                    insertMovieGenre.setInt(1, movieId);
                    insertMovieGenre.setInt(2, genreId);
                    insertMovieGenre.executeUpdate();
                }
                connection.commit();
	
            } catch (IOException | SQLException e) {
            	System.err.println("Cannot insert movie genres ; transaction is being roll-backed");
            	e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void commitRatings(Connection connection) throws SQLException {
        // create table
        try (Statement statement = connection.createStatement()) {
	        statement.executeUpdate("CREATE TABLE IF NOT EXISTS ratings (\n"
				                + "  user_id int(11) NOT NULL,\n"
				                + "  movie_id int(11) NOT NULL,\n"
				                + "  rating int(11) NOT NULL,\n"
				                + "  date date NOT NULL,\n"
				                + "  KEY user_id (user_id),\n"
				                + "  KEY movie_id (movie_id)\n"
				                + ");");
	        
	        statement.executeUpdate("ALTER TABLE ratings\n"
	                + "  ADD CONSTRAINT ratings_to_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE;\n");
	        statement.executeUpdate("ALTER TABLE ratings\n"
	                + "  ADD CONSTRAINT ratings_to_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE;\n");
	        statement.executeUpdate("ALTER TABLE ratings\n"
	                + "  ADD UNIQUE unique_index(user_id, movie_id);\n");
	        
	        String insertQuery = "INSERT INTO ratings (user_id, movie_id, rating, date) VALUES (?, ?, ?, ?)";
	
	        // populate table
	        try (
        		BufferedReader br = new BufferedReader(new FileReader(RATINGS_CSV_FILE));
	        	PreparedStatement insertRatings = connection.prepareStatement(insertQuery);
    		) {
                connection.setAutoCommit(false);
	
                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(CSV_SEPARATOR);
                    int userId = Integer.parseInt(values[0]);
                    int movieId = Integer.parseInt(values[1]);
                    int ratingValue = Integer.parseInt(values[2]);
                    Date date = new Date(Long.parseLong(values[3]) * 1000);
                    insertRatings.setInt(1, userId);
                    insertRatings.setInt(2, movieId);
                    insertRatings.setInt(3, ratingValue);
                    insertRatings.setDate(4, date);
                    insertRatings.executeUpdate();
                }
                connection.commit();
	
	        } catch (IOException | SQLException e) {
            	System.err.println("Cannot insert ratings ; transaction is being roll-backed");
            	e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void commitFriends(Connection connection) throws SQLException {
        // create table
        try (Statement statement = connection.createStatement()) {
	        statement.executeUpdate("CREATE TABLE IF NOT EXISTS friends (\n"
	                + "  user1_id int(11) NOT NULL,\n"
	                + "  user2_id int(11) NOT NULL,\n"
	                + "  KEY user1_id (user1_id),\n"
	                + "  KEY user2_id (user2_id)\n"
	                + ");");
	        statement.executeUpdate("ALTER TABLE friends\n"
	                + "  ADD CONSTRAINT friends_to_user1 FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE;\n");
	        statement.executeUpdate("ALTER TABLE friends\n"
	                + "  ADD CONSTRAINT friends_to_user2 FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE;\n");
	        
	        String insertQuery = "INSERT INTO friends (user1_id, user2_id) VALUES (?, ?)";
	
	        // populate table
	        try (
        		BufferedReader br = new BufferedReader(new FileReader(FRIENDS_CSV_FILE));
	        	PreparedStatement insertFriends = connection.prepareStatement(insertQuery);
    		) {
                connection.setAutoCommit(false);
	
                String line;
                br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(CSV_SEPARATOR);
                    int user1Id = Integer.parseInt(values[0]);
                    int user2Id = Integer.parseInt(values[1]);
                    insertFriends.setInt(1, user1Id);
                    insertFriends.setInt(2, user2Id);
                    insertFriends.executeUpdate();
                }
                connection.commit();
	
            } catch (IOException | SQLException e) {
            	System.err.println("Cannot insert friends ; transaction is being roll-backed");
            	e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

}
