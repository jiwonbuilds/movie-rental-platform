import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String movieId;
    private String movieTitle;
    private Integer year;
    private String director;
    private float rating;
    private List<Star> stars = new ArrayList<>();
    private List<Genre> genres = new ArrayList<>();

    public Movie() {
    }

    public Movie(String mid) {
        this.movieId = mid;
    }

    public Movie(String mid, String mtitle, int year, String mdirector, List categories) {
        this.movieId = mid;
        this.movieTitle = mtitle;
        this.year = year;
        this.director = mdirector;
        this.genres = categories;
    }

    // Assume a builder method for Movie class
    public static MovieBuilder newBuilder() {
        return new MovieBuilder();
    }




    // Assume a builder class for Movie
    public static class MovieBuilder {
        private final Movie movie;

        private MovieBuilder() {
            this.movie = new Movie();
        }
        public MovieBuilder setMovieId(String mid) {
            movie.movieId = mid;
            return this;
        }
        public MovieBuilder setMovieTitle(String mtitle) {
            movie.movieTitle = mtitle;
            return this;
        }
        public MovieBuilder setMovieYear(Integer myear) {
            movie.year = myear;
            return this;
        }
        public MovieBuilder setMovieDirector(String mdirector) {
            movie.director = mdirector;
            return this;
        }
        public MovieBuilder setMovieRating(float mrating) {
            movie.rating = mrating;
            return this;
        }

        public Movie build() {
            return movie;
        }
    }

    public void addGenre(Genre mgenre) {
        this.genres.add(mgenre);
    }
    public void addStar(Star mstar) {
        this.stars.add(mstar);
    }

    public String getMovieId() {
        return this.movieId;
    }
    public String getMovieTitle() { return this.movieTitle; }
    public Integer getMovieYear() { return this.year; }
    public String getMovieDirector() { return this.director; }
    public List<Genre> getMovieGenres() { return this.genres; }
    public List<Star> getMovieStars() { return this.stars; }

    public void setMovieId(String mid) {
        this.movieId = mid;
    }
    public void setMovieTitle(String mtitle) {
        this.movieTitle = mtitle;
    }
    public void setYear(Integer myear) {
        this.year = myear;
    }
    public void setDirector(String mdirector) {
        this.director = mdirector;
    }

    public JsonObject toJsonObject() {
        JsonObject movieJson = new JsonObject();
        movieJson.addProperty("movieId", this.movieId);
        movieJson.addProperty("movieTitle", this.movieTitle);
        movieJson.addProperty("year", this.year);
        movieJson.addProperty("director", this.director);
        movieJson.addProperty("rating", this.rating);

        JsonArray starsJsonArray = new JsonArray();
        for (Star star : this.stars) {
            starsJsonArray.add(star.toJsonObject());
        }
        movieJson.add("stars", starsJsonArray);

        JsonArray genresJsonArray = new JsonArray();
        for (Genre genre : this.genres) {
            genresJsonArray.add(genre.toJsonObject());
        }
        movieJson.add("genres", genresJsonArray);

        return movieJson;
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("ID: " + this.movieId);
        sb.append(", ");
        sb.append("Title: " + this.movieTitle);
        sb.append(", ");
        sb.append("Year: " + this.year);
        sb.append(", ");
        sb.append("Director: " + this.director);
        sb.append(".");
        return sb.toString();
    }
}


