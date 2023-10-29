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
}

