import com.google.gson.JsonObject;

/**
 * This User class  has the username field in this example.
 */

public class Genre {
    private String genreId;
    private String genreName;


    // Assume a builder method for Movie class
    public static GenreBuilder newBuilder() {
        return new GenreBuilder();
    }

    // Assume a builder class for Movie
    public static class GenreBuilder {
        private final Genre genre;

        private GenreBuilder() {
            this.genre = new Genre();
        }

        public GenreBuilder setGenreId(String gid) {
            genre.genreId = gid;
            return this;
        }

        public GenreBuilder setGenreName(String gname) {
            genre.genreName = gname;
            return this;
        }

        public Genre build() {
            return genre;
        }
    }

    public JsonObject toJsonObject() {
        JsonObject starJson = new JsonObject();
        starJson.addProperty("genreId", this.genreId);
        starJson.addProperty("genreName", this.genreName);
        return starJson;
    }
}

