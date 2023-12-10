import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/autocomplete")
public class AutoCompleteServlet extends HttpServlet {

    public static final HashSet<String> stopWords = new HashSet<>(Arrays.asList(
            "a", "about", "an", "are", "as", "at", "be", "by", "com", "de", "en", "for", "from", "how", "i", "in", "is",
            "it", "la", "of", "on", "or", "that", "the", "this", "to", "was", "what", "when", "where", "who", "will",
            "with", "und", "the", "www"));


    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String FTS_QUERY =
                "SELECT DISTINCT M.id, M.title, M.year, M.director " +
                        "FROM movies M " +
                        "WHERE MATCH (M.title) AGAINST (? IN BOOLEAN MODE) " +
                        "ORDER BY M.title " +
                        "LIMIT 10";

        response.setContentType("application/json");

        HttpSession session = request.getSession(true);

        // Retrieve parameter id from url request.
        String mtitle = request.getParameter("mtitle");

        List<String> filteredTokens = Arrays.stream(mtitle.split(" "))
                .map(String::toLowerCase)
                .filter(token -> !stopWords.contains(token))
                .collect(Collectors.toList());
        String tokensQuery = filteredTokens.stream()
                .map(token -> "+" + token + "*")
                .collect(Collectors.joining(" "));

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FTS_QUERY)) {

            preparedStatement.setString(1, tokensQuery);

            JsonArray jsonArray = new JsonArray();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String movieTitle = resultSet.getString("title");
                    String movieId = resultSet.getString("id");
                    String movieYear = resultSet.getString("year");
                    jsonArray.add(generateJsonObject(movieId, movieTitle + "(" + movieYear + ")"));
                }
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

    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}