package com.camillepradel.movierecommender.model;

import java.util.List;

public class Movie {

    private int id;
    private String title;
    private List<Genre> genres;

    public Movie(int id, String title, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.genres = genres;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public List<Genre> getGenres() {
        return this.genres;
    }
}
