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

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {


    // Create a dataSource which registered in web.xml
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private boolean confirmFromMySql(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    private void insertSaleRecord(Integer customerId, String movieId, Date date) throws SQLException {

        String insertion = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertion);) {

            statement.setInt(1, customerId);
            statement.setString(2, movieId);
            statement.setDate(3, new java.sql.Date(date.getTime()));

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String CARD_QUERY =
                "SELECT * " +
                "FROM creditcards " +
                "WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?; ";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CARD_QUERY)) {
            preparedStatement.setString(1, request.getParameter("cardNum"));
            preparedStatement.setString(2, request.getParameter("firstName"));
            preparedStatement.setString(3, request.getParameter("lastName"));
            preparedStatement.setDate(4, Date.valueOf(request.getParameter("expDate")));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                JsonObject jsonObject = new JsonObject();
                boolean infoExists = confirmFromMySql(resultSet);

                if (infoExists) {
                    jsonObject.addProperty("status", "success");
                    jsonObject.addProperty("message", "success!");
                    User user = (User) request.getSession().getAttribute("user");
                    java.sql.Date timestamp = new java.sql.Date(System.currentTimeMillis());
                    for (Map.Entry<String, CartItem> itemEntry : user.getShoppingCart().entrySet()) {
                        String mid = itemEntry.getKey();
                        insertSaleRecord(user.getId(), mid, timestamp);
                    }
                } else {
                    jsonObject.addProperty("status", "fail");
                    jsonObject.addProperty("message", "Payment information incorrect!");
                }
                response.getWriter().write(jsonObject.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            }
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
