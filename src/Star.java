import com.google.gson.JsonObject;

/**
 * This User class  has the username field in this example.
 */

public class Star {
    private String starId;
    private String starName;
    private Integer starYear;

    public Star () {

    }

    public Star(String sname) {
        this.starName = sname;
    }

    public Star(String sid, String sname, Integer syear) {
        this.starId = sid;
        this.starName = sname;
        this.starYear = syear;
    }


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

    public String getStarName() { return this.starName; }
    public Integer getStarYear() { return this.starYear; }
    public void setStarName(String sname) {
        this.starName = sname;
    }
    public void setStarYear(Integer syear) {
        this.starYear = syear;
    }

    public JsonObject toJsonObject() {
        JsonObject starJson = new JsonObject();
        starJson.addProperty("starId", this.starId);
        starJson.addProperty("starName", this.starName);
        return starJson;
    }
}

