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


// Declaring a WebServlet called Top20Servlet, which maps to url "/api/top20"
@WebServlet(name = "Top20Servlet", urlPatterns = "/api/top20")
public class Top20Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
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

            String query = "SELECT * " +
                    "FROM movies AS m, ratings AS r " +
                    "WHERE m.id = r.movieId " +
                    "ORDER BY r.rating DESC " +
                    "LIMIT 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

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

                Statement statement_stars = conn.createStatement();

                // Create movie_stars (upto 3 per movie)
                String query_stars = "SELECT s.id, s.name " +
                        "FROM movies AS m, stars_in_movies AS sim, stars AS s " +
                        "WHERE m.id = sim.movieId AND s.id = sim.starId AND m.id = \"" + movie_id + "\" " +
                        "LIMIT 3";

                ResultSet rs_stars = statement_stars.executeQuery(query_stars);

                JsonArray jsonArray_stars = new JsonArray();
                while (rs_stars.next()) {
                    JsonObject jsonObjectStar = new JsonObject();
                    String star_id = rs_stars.getString("id");
                    String star_name = rs_stars.getString("name");
                    jsonObjectStar.addProperty("star_id", star_id);
                    jsonObjectStar.addProperty("star_name", star_name);
                    jsonArray_stars.add(jsonObjectStar);
                }
                rs_stars.close();
                statement_stars.close();
                jsonObject.add("movie_stars", jsonArray_stars);

                // Create movie_genres (upto 3 per movie)
                Statement statement_genres = conn.createStatement();

                String query_genres = "SELECT g.id, g.name " +
                        "FROM movies AS m, genres_in_movies AS gim, genres AS g " +
                        "WHERE m.id = gim.movieId AND g.id = gim.genreId AND m.id = \"" + movie_id + "\" " +
                        "LIMIT 3";

                ResultSet rs_genres = statement_genres.executeQuery(query_genres);

                JsonArray jsonArray_genres = new JsonArray();
                while (rs_genres.next()) {
                    jsonArray_genres.add(rs_genres.getString("name"));
                }
                rs_genres.close();
                statement_genres.close();
                jsonObject.add("movie_genres", jsonArray_genres);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            // out.write(infoContainer.toString());
            out.write(jsonArray.toString());
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
