import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT * from movies as m, ratings as r " +
                    "where m.id = r.movieId " +
                    "order by r.rating desc";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonObject infoContainer = new JsonObject();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            infoContainer.add("movies_list", jsonArray);

            //////// STARS LIST
            Statement statement_stars = conn.createStatement();

            String query_stars = "SELECT m.id AS mid, s.id AS sid, s.name from movies as m, stars_in_movies as sim, stars as s " +
                    "where m.id = sim.movieId and s.id = sim.starId";

            ResultSet rs_stars = statement_stars.executeQuery(query_stars);

            JsonObject json_stars = new JsonObject();
            while (rs_stars.next()) {
                JsonObject jsonObjectStar = new JsonObject();
                String star_id = rs_stars.getString("sid");
                String star_name = rs_stars.getString("name");
                jsonObjectStar.addProperty("star_id", star_id);
                jsonObjectStar.addProperty("star_name", star_name);

                JsonArray innerArray = json_stars.getAsJsonArray(rs_stars.getString("mid"));
                if (innerArray == null) {
                    innerArray = new JsonArray();
                    json_stars.add(rs_stars.getString("mid"), innerArray);
                }
                innerArray.add(jsonObjectStar);
            }
            rs_stars.close();
            statement_stars.close();
            infoContainer.add("stars_list", json_stars);


            //////// GENRES LIST
            Statement statement_genres = conn.createStatement();

            String query_genres = "SELECT m.id, g.name from movies as m, genres_in_movies as gim, genres as g " +
                    "where m.id = gim.movieId and g.id = gim.genreId";

            ResultSet rs_genres = statement_genres.executeQuery(query_genres);

            JsonObject json_genres = new JsonObject();
            while (rs_genres.next()) {
                JsonArray innerArray = json_genres.getAsJsonArray(rs_genres.getString("id"));
                if (innerArray == null) {
                    innerArray = new JsonArray();
                    json_genres.add(rs_genres.getString("id"), innerArray);
                }
                innerArray.add(rs_genres.getString("name"));
            }
            rs_genres.close();
            statement_genres.close();
            infoContainer.add("genres_list", json_genres);

            //// Final Cleanup

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(infoContainer.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
