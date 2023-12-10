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

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employee/login")
public class EmployeeLoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }

        System.out.println("hello here in employee servlet");
    }

    private User getUserFromDB(String username, String password) throws IOException {
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * " +
                    "FROM employees " +
                    "WHERE email = ?; ";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String password_encrypted = rs.getString("password");
                boolean success = new StrongPasswordEncryptor().checkPassword(password, password_encrypted);
                if (success) {
                    String user_fullName = rs.getString("fullName");
                    String user_email = rs.getString("email");
                    rs.close();
                    statement.close();
                    return new User(122, user_fullName, "Employee", user_email, password_encrypted);
                }
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
