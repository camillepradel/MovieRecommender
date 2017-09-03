package com.camillepradel.movierecommender.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CsvToMySql {

    public static void main(String[] args) {
        // load JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // db connection info
        String url = "jdbc:mysql://localhost:3306/movie_recommender?zeroDateTimeBehavior=convertToNull&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String login = "root";
        String password = "";
        
        String pathToCsvFiles = "D:\\MovieRecommender\\src\\main\\java\\com\\camillepradel\\movierecommender\\utils\\";
        String userCsvFile = pathToCsvFiles + "users.csv";
        String cvsSplitBy = ",";

        Connection connexion = null;

        try {
            connexion = DriverManager.getConnection(url, login, password);

            // users.csv
            
            // create table
            Statement statement = connexion.createStatement();
            int status = statement.executeUpdate("CREATE TABLE IF NOT EXISTS `user` (\n"
                    + "  `id` int(11) NOT NULL AUTO_INCREMENT,\n"
                    + "  `age` int(11) NOT NULL,\n"
                    + "  `sex` varchar(1) NOT NULL,\n"
                    + "  `occupation` varchar(60) NOT NULL,\n"
                    + "  `zip` varchar(6) NOT NULL,\n"
                    + "  PRIMARY KEY (`id`)\n"
                    + ");");

            // populate table
            try (BufferedReader br = new BufferedReader(new FileReader(userCsvFile))) {

                String line = br.readLine(); // skip first line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(cvsSplitBy);

                    System.out.println("User [id= " + values[0] + "\t, age=" + values[1] + "\t, sex=" + values[2] + "\t, occupation=" + values[3] + "\t, zip_code=" + values[4] + "]");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // movies.csv
            // genres.csv
            // mov_genre.csv
            // ratings.csv
            // friends.csv
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ignore) {
                    // exception occured while closing connexion -> nothing else can be done
                }
            }
        }

        System.out.println("done");
    }

}
