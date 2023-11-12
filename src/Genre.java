import com.google.gson.JsonObject;

/**
 * This User class  has the username field in this example.
 */

public class Genre {
    private String genreId;
    private String genreName;

    public Genre() {

    }
    public Genre(String gname) {
        this.genreName = gname;
    }


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

    public String getGenreName() {
        return this.genreName;
    }

    public JsonObject toJsonObject() {
        JsonObject starJson = new JsonObject();
        starJson.addProperty("genreId", this.genreId);
        starJson.addProperty("genreName", this.genreName);
        return starJson;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Genre Details - ");
        sb.append("ID: " + this.genreId);
        sb.append(", ");
        sb.append("Name: " + this.genreName);
        sb.append(". ");
        return sb.toString();
    }
}


