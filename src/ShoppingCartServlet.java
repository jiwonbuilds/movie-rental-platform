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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        System.out.println("HERE IN ShoppingCart: " + action);

        User user = (User) request.getSession().getAttribute("user");

        switch (action) {
            case "add": {
                System.out.println("adding to cart");

                String movieId = request.getParameter("mid");
                String movieTitle = request.getParameter("mtitle");

                CartItem cartItem = new CartItem(movieId, movieTitle, 3);
                user.addCartItem(cartItem);
                break;
            }
            case "show":
                System.out.println("making json of shopping cart");
                response.setContentType("application/json"); // Response mime type

                response.getWriter().write(user.cartToJson());
                System.out.println(user.cartToJson());
                break;
            case "update": {
                System.out.println("updating the count");
                String movieId = request.getParameter("mid");
                int amount = Integer.parseInt(request.getParameter("amount"));
                user.updateCartItem(movieId, amount);
                break;
            }
            case "delete": {
                System.out.println("deleting movie");
                String movieId = request.getParameter("mid");
                user.removeCartItem(movieId);
                System.out.println("cart:" + user.cartToJson());
                break;
            }
            case "confirm": {
                System.out.println("confirmation page info");
                response.setContentType("application/json"); // Response mime type
                response.getWriter().write(user.cartToJson());
                System.out.println(user.cartToJson());
                user.clearShppingCart();
                System.out.println("cart cleared");
                break;
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);

    }

}
