import com.google.gson.JsonObject;

/**
 * This User class  has the username field in this example.
 */

public class Star {
    private String starId;
    private String starName;


    // Assume a builder method for Movie class
    public static StarBuilder newBuilder() {
        return new StarBuilder();
    }

    // Assume a builder class for Movie
    public static class StarBuilder {
        private final Star star;

        private StarBuilder() {
            this.star = new Star();
        }

        public StarBuilder setStarId(String sid) {
            star.starId = sid;
            return this;
        }

        public StarBuilder setStarName(String sname) {
            star.starName = sname;
            return this;
        }

        public Star build() {
            return star;
        }
    }



    public JsonObject toJsonObject() {
        JsonObject starJson = new JsonObject();
        starJson.addProperty("starId", this.starId);
        starJson.addProperty("starName", this.starName);
        return starJson;
    }
}

