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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    public static final String GENRE_QUERY =
            "SELECT G.id, G.name " +
                    "FROM genres_in_movies as GIM, genres as G " +
                    "WHERE GIM.genreId = G.id AND GIM.movieId = ? " +
                    "ORDER BY G.name ASC ;";
    public static final String STAR_QUERY =
            "SELECT S.id, S.name " +
                    "FROM stars as S, stars_in_movies as SIM " +
                    "WHERE S.id = SIM.starId AND SIM.movieId = ? " +
                    "GROUP BY S.id " +
                    "ORDER BY COUNT(SIM.movieId) DESC, S.name;";

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource =
                    (DataSource) new InitialContext()
                            .lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private List<Movie> getMoviesFromMySql(ResultSet resultSet) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        while (resultSet.next()) {
            Movie movie =
                    Movie.newBuilder()
                            .setMovieId(resultSet.getString("id"))
                            .setMovieTitle(resultSet.getString("title"))
                            .setMovieYear(resultSet.getInt("year"))
                            .setMovieDirector(resultSet.getString("director"))
                            .setMovieRating(resultSet.getFloat("rating"))
                            .build();
            movies.add(movie);
        }
        return movies;
    }

    void decorateMoviesWithGenres(List<Movie> movies, Connection connection) throws SQLException {
        for (Movie movie : movies) {
            try (PreparedStatement genreStatement = connection.prepareStatement(GENRE_QUERY)) {
                genreStatement.setString(1, movie.getMovieId());
                try (ResultSet genreResultSet = genreStatement.executeQuery()) {
                    while (genreResultSet.next()) {
                        Genre genre =
                                Genre.newBuilder()
                                        .setGenreId(genreResultSet.getString("id"))
                                        .setGenreName(genreResultSet.getString("name"))
                                        .build();
                        movie.addGenre(genre);
                    }
                }
            }

        }
    }

    void decorateMoviesWithStars(List<Movie> movies, Connection connection) throws SQLException {
        for (Movie movie : movies) {
            try (PreparedStatement starStatement = connection.prepareStatement(STAR_QUERY)) {
                starStatement.setString(1, movie.getMovieId());
                try (ResultSet starsResultSet = starStatement.executeQuery()) {
                    while (starsResultSet.next()) {
                        Star star =
                                Star.newBuilder()
                                        .setStarId(starsResultSet.getString("id"))
                                        .setStarName(starsResultSet.getString("name"))
                                        .build();
                        movie.addStar(star);
                    }
                }
            }
        }
    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String id = request.getParameter("id");

        String MOVIE_QUERY =
                "SELECT DISTINCT M.id, M.title, M.year, M.director, R.rating " +
                        "FROM movies M " +
                        "JOIN ratings R ON R.movieId = M.id " +
                        "JOIN genres_in_movies GIM ON GIM.movieId = M.id " +
                        "WHERE M.id = '" + id + "';";

        System.out.println(MOVIE_QUERY);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(MOVIE_QUERY)) {

            List<Movie> movies = getMoviesFromMySql(resultSet);
            System.out.println("got movies: "+movies.size());
            decorateMoviesWithGenres(movies, connection);
            decorateMoviesWithStars(movies, connection);

            JsonArray jsonArray = new JsonArray();
            for (Movie movie : movies) {
                jsonArray.add(movie.toJsonObject());
                System.out.println("here");
            }

            response.getWriter().write(jsonArray.toString());
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            response.getWriter().close();
        }
    }
}

