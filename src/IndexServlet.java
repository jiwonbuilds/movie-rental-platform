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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("in indexservlet");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});

            StringBuilder jsonResult = new StringBuilder("[");
            boolean firstTable = true;

            while (tables.next()) {
                if (!firstTable) {
                    jsonResult.append(",");
                }

                String tableName = tables.getString("TABLE_NAME");
                jsonResult.append("{");
                jsonResult.append("\"tableName\":\"").append(tableName).append("\",\"columns\":[");

                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                boolean firstColumn = true;

                while (columns.next()) {
                    if (!firstColumn) {
                        jsonResult.append(",");
                    }

                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");

                    jsonResult.append("{\"columnName\":\"").append(columnName).append("\",");
                    jsonResult.append("\"columnType\":\"").append(columnType).append("\",");
                    jsonResult.append("\"columnSize\":").append(columnSize).append("}");

                    firstColumn = false;
                }

                jsonResult.append("]}");

                firstTable = false;
            }

            jsonResult.append("]");
            out.write(jsonResult.toString());
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
