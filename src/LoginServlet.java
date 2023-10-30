import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private User getUserFromDB(String username, String password) throws IOException {
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * " +
                    "FROM customers " +
                    "WHERE email = ? AND password = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Get information from result
            if (rs.next()) {
                Integer user_id = rs.getInt("id");
                String user_firstName = rs.getString("firstName");
                String user_lastName = rs.getString("lastName");
                String user_email = rs.getString("email");
                String user_password = rs.getString("password");

                return new User(user_id, user_firstName, user_lastName, user_email, user_password);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        return null;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Verify username/password using the database
        User user = getUserFromDB(username, password);

        JsonObject responseJsonObject = new JsonObject();
        if (user != null) {
            // Login success:

            // set this user into the session
            request.getSession().setAttribute("user", user);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // Tell user invalid username/password
            responseJsonObject.addProperty("message", "Invalid username or password. Please try again.");
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
