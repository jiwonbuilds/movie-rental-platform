package edu.uci.ics.fabflixmobile.data.model;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie implements Serializable {
    private final String movieId;
    private final String movieTitle;
    private final short year;
    private final String director;
    private final double rating;
    private final String genres;
    private final String stars;

    public Movie(String mid, String mtitle, short myear, String mdirector, double mrating, String mgenres, String mstars) {
        this.movieId = mid;
        this.movieTitle = mtitle;
        this.year = myear;
        this.director = mdirector;
        this.rating = mrating;
        this.genres = mgenres;
        this.stars = mstars;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public short getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        return genres;
    }

    public String getStars() {
        return stars;
    }

    public String getMovieId() {
        return movieId;
    }
}