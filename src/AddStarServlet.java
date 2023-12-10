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


@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add-star")
public class AddStarServlet extends HttpServlet {


    // Create a dataSource which registered in web.xml
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    private String addStarToDb(String starName, String starYear) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{CALL InsertStar(?, ?, ?)}")) {
            statement.setString(1, starName);
            if (starYear.isEmpty()) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, Integer.parseInt(starYear));
            }
            statement.registerOutParameter(3, Types.VARCHAR);
            if (statement.executeUpdate() > 0) {
                return statement.getString(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("starName");
        String starYear = request.getParameter("starYear");

        String starId = addStarToDb(starName, starYear);

        response.setContentType("text/plain");
        if (starId == null) {
            response.getWriter().write("failed");
        } else {
            response.getWriter().write("Successfully added Star with id: " + starId);
        }

    }

}
