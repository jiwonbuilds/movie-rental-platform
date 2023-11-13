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
import java.util.Map;


@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {


    // Create a dataSource which registered in web.xml
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    private String addMovieToDb(String movieTitle, String movieYear, String movieDirector,
                                String movieStarName, String movieStarYear, String movieGenre) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{CALL InsertMovie(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setString(1, movieTitle);
            statement.setInt(2, Integer.parseInt(movieYear));
            statement.setString(3, movieDirector);
            statement.setString(4, movieStarName);
            if (movieStarYear.isEmpty()) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, Integer.parseInt(movieStarYear));
            }
            statement.setString(6, movieGenre);

            statement.registerOutParameter(7, Types.VARCHAR);
            statement.registerOutParameter(8, Types.VARCHAR);
            statement.registerOutParameter(9, Types.INTEGER);

            if (statement.executeUpdate() > 0) {
                String movieId =  statement.getString(7);
                String starId =  statement.getString(8);
                String genreId =  statement.getString(9);

                return "Successfully inserted movie with movieId: " + movieId +
                        ", starId: " + starId + ", and genreId: " + genreId;
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("45000")) {
                return "Movie already exists!";
            } else {
                e.printStackTrace();
            }
        }
        return "Failed to add the movie.";
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieTitle = request.getParameter("movieTitle");
        String movieYear = request.getParameter("movieYear");
        String movieDirector = request.getParameter("movieDirector");
        String movieStarName = request.getParameter("movieStarName");
        String movieStarYear = request.getParameter("movieStarYear");
        String movieGenre = request.getParameter("movieGenre");

        String addMovieResult = addMovieToDb(movieTitle, movieYear, movieDirector,
                movieStarName, movieStarYear, movieGenre);

        response.setContentType("text/plain");

        response.getWriter().write(addMovieResult);
    }

}
