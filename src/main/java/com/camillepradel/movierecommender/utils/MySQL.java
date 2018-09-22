package com.camillepradel.movierecommender.utils;

/**
 * Stores constants related to the connection with the MySQL database.
 * 
 * TODO [Refactor] Make MySQL constants configurable
 */
public interface MySQL {
	
	static final String URL = "jdbc:mysql://127.0.0.1:3306/movie_recommender"
            				+ "?zeroDateTimeBehavior=CONVERT_TO_NULL"
            				+ "&useUnicode=true"
            				+ "&useJDBCCompliantTimezoneShift=true"
            				+ "&useLegacyDatetimeCode=false"
            				+ "&serverTimezone=UTC";
	
	static final String LOGIN = "root";
	
	static final String PASSWORD = "root";

}
