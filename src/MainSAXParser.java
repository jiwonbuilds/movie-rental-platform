import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class MainSAXParser {
    private Connection connection;
    private static final String LOGIN_URL = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
    private static final String LOGIN_USER = "mytestuser";
    private static final String LOGIN_PASSWORD = "My6$Password";

    private int insertedMovieCount = 0;
    private int insertedStarCount = 0;
    private int insertedGenresInMoviesCount = 0;
    private int insertedStarsInMoviesCount = 0;
    private int newStarId = 0;
    HashMap<String, Integer> genres = new HashMap<>();
    HashMap<String, String> stars = new HashMap<>();
    private BufferedWriter missingWriter;

    public MainSAXParser() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(LOGIN_URL, LOGIN_USER, LOGIN_PASSWORD);
            System.out.println("db connection success");

            missingWriter = new BufferedWriter(new FileWriter("MissingStars.txt"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeFileWriter() {
        try {
            missingWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeDbConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void getGenresFromDb() {
        String genresQuery = "SELECT * FROM genres";
        try (Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(genresQuery)) {
            while (rs.next()) {
                genres.put(rs.getString("name"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertGenres(Set<String> newGenres) {
        int newId = genres.size() + 1;
        String insertGenresSQL = "INSERT IGNORE INTO genres (name) VALUES (?)";

        try (PreparedStatement preparedStmt = connection.prepareStatement(insertGenresSQL)) {
            for (String newGenre : newGenres) {
                if (!genres.containsKey(newGenre) && !newGenre.isEmpty()) {
                    genres.put(newGenre, newId++);
                    preparedStmt.setString(1, newGenre);
                    preparedStmt.addBatch();
                }
            }
            preparedStmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("now have " + (newId - 1) + " genres.");
    }
    private void insertGenresInMovies(HashMap<String, Movie> newMovies) {
        String insertGenresIntoMoviesSQL = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
        try (PreparedStatement preparedStmt = connection.prepareStatement(insertGenresIntoMoviesSQL)) {
            for (Movie movie : newMovies.values()) {
                List<Genre> movieGenres = movie.getMovieGenres();
                for (Genre genre : movieGenres) {
                    if (!genre.getGenreName().isEmpty()) {
                        preparedStmt.setInt(1, genres.get(genre.getGenreName()));
                        preparedStmt.setString(2, movie.getMovieId());
                        preparedStmt.addBatch();
                        insertedGenresInMoviesCount++;
                    }
                }
            }
            preparedStmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("inserted " + insertedGenresInMoviesCount + " into genres_in_movies.");
    }
    private int insertMovies(HashMap<String, Movie> movies) {
        System.out.println("inserting " + movies.size() + " movies");
        String insertStarsSQL = "INSERT IGNORE INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStmt = connection.prepareStatement(insertStarsSQL)) {
            for (Movie movie : movies.values()) {
                preparedStmt.setString(1, movie.getMovieId());
                preparedStmt.setString(2, movie.getMovieTitle());
                if (movie.getMovieYear() == null) {
                    preparedStmt.setNull(3, Types.INTEGER);
                } else {
                    preparedStmt.setInt(3, movie.getMovieYear());
                }
                preparedStmt.setString(4, movie.getMovieDirector());
                preparedStmt.addBatch();
            }
            int[] insertResult = preparedStmt.executeBatch();
            for (int rowsAffected : insertResult) {
                if (rowsAffected == 1) {
                    insertedMovieCount++;
                } else {
                    System.out.println("inserting movie failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Inserted " + insertedMovieCount + " movies");
        return insertedMovieCount;
    }
    private int insertStars(List<Star> newStars) {
        System.out.println("inserting " + newStars.size() + " stars");
        String insertStarsSQL = "INSERT IGNORE INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStmt = connection.prepareStatement(insertStarsSQL)) {
            for (Star star : newStars) {
                String tempStarId = "ns" + String.format("%07d", newStarId++);
                preparedStmt.setString(1, tempStarId);
                preparedStmt.setString(2, star.getStarName());
                // add to cache
                if (star.getStarYear() == null) {
                    preparedStmt.setNull(3, Types.INTEGER);
                } else {
                    preparedStmt.setInt(3, star.getStarYear());
                }
                preparedStmt.addBatch();
                stars.put(star.getStarName(), tempStarId);
            }
            int[] insertResult = preparedStmt.executeBatch();
            for (int rowsAffected : insertResult) {
                if (rowsAffected == 1) {
                    insertedStarCount++;
                } else if (rowsAffected == 0) {
                    System.out.println("ignored");
                } else {
                    System.out.println("inserting star failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedStarCount;
    }
    private void insertStarsInMovies(HashMap<String, Movie> newMovies) {
        String insertStarsIntoMoviesSQL = "INSERT IGNORE INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
        try (PreparedStatement preparedStmt = connection.prepareStatement(insertStarsIntoMoviesSQL)) {
            for (Movie movie : newMovies.values()) {
                List<Star> movieStars = movie.getMovieStars();
                for (Star star : movieStars) {
                    if (!star.getStarName().isEmpty() && stars.containsKey(star.getStarName())) {
                        preparedStmt.setString(1, stars.get(star.getStarName()));
                        preparedStmt.setString(2, movie.getMovieId());
                        preparedStmt.addBatch();
                        insertedStarsInMoviesCount++;
                    } else {
                        try {
                            missingWriter.write(star.getStarName());
                            missingWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            preparedStmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("inserted " + insertedStarsInMoviesCount + " into stars_in_movies.");
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();


        MainSAXParser mainParser = new MainSAXParser();

        // parse main243.xml
        MoviesSAXParser moviesParser = new MoviesSAXParser();
        moviesParser.runParser();
        HashMap<String, Movie> newMovies = moviesParser.getMovies();
        Set<String> newGenres = moviesParser.getGenres();

        // parse actors63.xml
        ActorsSAXParser actorsParser = new ActorsSAXParser();
        actorsParser.runParser();
        List<Star> newStars = actorsParser.getActors();
        mainParser.insertStars(newStars);

        // parse casts93.xml
        CastsSAXParser castsParser = new CastsSAXParser(newMovies);
        castsParser.runParser();
        newMovies = castsParser.getMovies();

        // get data from genres.db
        mainParser.getGenresFromDb();

        mainParser.insertGenres(newGenres);
        mainParser.insertMovies(newMovies);
        mainParser.insertGenresInMovies(newMovies);
        mainParser.insertStarsInMovies(newMovies);

        mainParser.closeFileWriter();
        mainParser.closeDbConnection();

        long endTime = System.currentTimeMillis();
        long elapsedTimeMillis = endTime - startTime;
        double elapsedTimeMinutes = (double) elapsedTimeMillis / (60_000);
        System.out.println("Elapsed time: " + elapsedTimeMinutes + " minutes");

        System.out.println("hello world");
    }
}

