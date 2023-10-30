import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



// Declaring a WebServlet called MoviesServlet, which maps to url "/api/search"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    public static final String GENRE_QUERY =
    "SELECT G.id, G.name " +
    "FROM genres_in_movies as GIM, genres as G " +
    "WHERE GIM.genreId = G.id AND GIM.movieId = ? " +
    "ORDER BY G.name ASC " +
    "LIMIT 3; ";
    public static final String STAR_QUERY =
            "SELECT S.id, S.name " +
                    "FROM stars as S, stars_in_movies as SIM " +
                    "WHERE S.id = SIM.starId AND SIM.movieId = ? " +
                    "GROUP BY S.id " +
                    "ORDER BY COUNT(SIM.movieId) DESC, S.name " +
                    "LIMIT 3; ";

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
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
        System.out.println("HERE in SearchServlet");
//        String BROWSE_QUERY =
//                "SELECT DISTINCT M.id, M.title, M.year, M.director, R.rating " +
//                "FROM movies M " +
//                "JOIN ratings R ON R.movieId = M.id " +
//                "JOIN genres_in_movies GIM ON GIM.movieId = M.id " +
//                "LEFT JOIN stars_in_movies SIM on SIM.movieId = M.id " +
//                "LEFT JOIN stars S ON S.id = SIM.starID ";
        String BROWSE_QUERY =
                "SELECT DISTINCT M.id, M.title, M.year, M.director, R.rating " +
                        "FROM movies M " +
                        "JOIN ratings R ON R.movieId = M.id " +
                        "JOIN genres_in_movies GIM ON GIM.movieId = M.id ";

        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession(true);

        // Retrieve parameter id from url request.
        String mtitle = request.getParameter("mtitle");
        String myear = request.getParameter("myear");
        String mdirector = request.getParameter("mdirector");
        String mstar = request.getParameter("mstar");

        String filterQuery = "WHERE ";
        ArrayList<String> whereQuery = new ArrayList<>();
        if (!"".equals(mtitle) && !mtitle.equals(session.getAttribute("mtitle"))) {
            whereQuery.add("LOWER(M.title) LIKE '%" + mtitle + "%' ");
//            session.setAttribute("mtitle", mtitle);
        }
        if (!"".equals(myear) && !myear.equals(session.getAttribute("myear"))) {
            whereQuery.add("M.year = " + myear + " ");
//            session.setAttribute("myear", myear);
        }
        if (!"".equals(mdirector) && !mdirector.equals(session.getAttribute("mdirector"))) {
            whereQuery.add("LOWER(M.director) LIKE '%" + mdirector + "%' ");
//            session.setAttribute("mdirector", mdirector);
        }
        if (!"".equals(mstar) && !mstar.equals(session.getAttribute("mstar"))) {
            whereQuery.add("EXISTS ( SELECT SIM.movieId FROM stars_in_movies SIM JOIN stars S ON S.id = SIM.starID WHERE LOWER(S.name) LIKE '"+ mstar + "%') ");

//            session.setAttribute("mstar", mstar);
        }

        filterQuery += String.join(" AND ", whereQuery);

        System.out.println("filterQuery" + filterQuery);

        if (!"WHERE ".equals(filterQuery)) {
            BROWSE_QUERY += filterQuery;
        } else {
            BROWSE_QUERY += "WHERE ";
        }

        System.out.println("line144"+mtitle+" "+myear+" "+mdirector+" "+mstar);

//        mtitle = (String) session.getAttribute("mtitle");
//        myear = (String) session.getAttribute("myear");
//        mdirector = (String) session.getAttribute("mdirector");
//        mstar = (String) session.getAttribute("mstar");


        System.out.println("line151"+mtitle+" "+myear+" "+mdirector+" "+mstar);

        // The log message can be found in localhost log
        request.getServletContext().log("getting mtitle: " + mtitle);
        request.getServletContext().log("getting myear: " + myear);
        request.getServletContext().log("getting mdirector: " + mdirector);
        request.getServletContext().log("getting mstar: " + mstar);

        int rowCount = Integer.parseInt(request.getParameter("rowCount"));
        int pageNum = Integer.parseInt(request.getParameter("page"));
        int offset = rowCount * (pageNum - 1);

        String[] orderByOptions = {"M.title ASC, R.rating ASC", "M.title ASC, R.rating DESC", "M.title DESC, R.rating ASC", "M.title DESC, R.rating DESC",
                "R.rating ASC, M.title ASC", "R.rating ASC, M.title DESC", "R.rating DESC, M.title ASC", "R.rating DESC, M.title DESC"};
        int optionIndex = Integer.parseInt(request.getParameter("sort")) - 1;
        BROWSE_QUERY += "ORDER BY " + orderByOptions[optionIndex] + " "
                + "LIMIT " + rowCount + " "
                + "OFFSET " + offset + "; ";

        System.out.println(BROWSE_QUERY);

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(BROWSE_QUERY)) {

            List<Movie> movies = getMoviesFromMySql(resultSet);
            decorateMoviesWithGenres(movies, connection);
            decorateMoviesWithStars(movies, connection);

            JsonArray jsonArray = new JsonArray();
            for (Movie movie : movies) {
                jsonArray.add(movie.toJsonObject());
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
