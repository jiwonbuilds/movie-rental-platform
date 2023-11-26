package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

    private String mtitle;
    private static int pageNum;
    private ArrayList<Movie> movies = new ArrayList<>();
    private Button prevButton, nextButton;
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project4_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        mtitle = getIntent().getStringExtra("movieTitle");
        movies = (ArrayList<Movie>) getIntent().getSerializableExtra("movies");
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageNum = getIntent().getIntExtra("pageNum", 1);
        if (pageNum == 1) {
            prevButton.setBackgroundColor(Color.parseColor("#c9ced4"));
            prevButton.setEnabled(false);
        }
        if (movies.size() < 10) {
            nextButton.setBackgroundColor(Color.parseColor("#c9ced4"));
            nextButton.setEnabled(false);
        }
        prevButton.setOnClickListener(view -> retrieveMovies(pageNum - 1));
        nextButton.setOnClickListener(view -> retrieveMovies(pageNum + 1));

        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> getMovieDetail(movies.get(position).getMovieId()));
    }

    @SuppressLint("SetTextI18n")
    private void retrieveMovies(int pageNum) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest listRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/search?mtitle=" + mtitle
                        + "&myear=&mdirector=&mstar="
                        + "&sort=1"
                        + "&rowCount=10"
                        + "&page=" + pageNum,
                response -> {
                    try {
                        JSONArray jsonResponse = new JSONArray(response);
                        ArrayList<Movie> newMovies = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            newMovies.add(new Movie(
                                    jsonObject.getString("movieId"),
                                    jsonObject.getString("movieTitle"),
                                    Short.parseShort(String.valueOf(jsonObject.getInt("year"))),
                                    jsonObject.getString("director"),
                                    jsonObject.getDouble("rating"),
                                    formatJSONArray(jsonObject.getJSONArray("genres"), "genreName"),
                                    formatJSONArray(jsonObject.getJSONArray("stars"), "starName")
                            ));
                        }
                        Intent MovieListPage = new Intent(this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", (Serializable) newMovies);
                        MovieListPage.putExtra("movieTitle", mtitle);
                        MovieListPage.putExtra("pageNum", pageNum);
                        startActivity(MovieListPage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("search.error", e.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {

        };
        // important: queue.add is where the login request is actually sent
        queue.add(listRequest);
    }

    @SuppressLint("SetTextI18n")
    private void getMovieDetail(String mid) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest singleMovieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + mid,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                        SingleMoviePage.putExtra("movieId", jsonObject.getString("movieId"));
                        SingleMoviePage.putExtra("movieTitle", jsonObject.getString("movieTitle"));
                        SingleMoviePage.putExtra("year", String.valueOf(jsonObject.getInt("year")));
                        SingleMoviePage.putExtra("director", jsonObject.getString("director"));
                        SingleMoviePage.putExtra("rating", jsonObject.getString("rating"));
                        SingleMoviePage.putExtra("genres", formatJSONArray(jsonObject.getJSONArray("genres"), "genreName"));
                        SingleMoviePage.putExtra("stars", formatJSONArray(jsonObject.getJSONArray("stars"), "starName"));
                        startActivity(SingleMoviePage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("single-movie.error", e.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("single-movie.error", error.toString());
                }) {

        };
        // important: queue.add is where the login request is actually sent
        queue.add(singleMovieRequest);
    }



    private static String formatJSONArray(JSONArray jsonArray, String field) throws JSONException {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject genreObject = jsonArray.getJSONObject(i);
            formatted.append(genreObject.getString(field));
            if (i < jsonArray.length() - 1) {
                formatted.append(", ");
            }
        }
        return formatted.toString();
    }
}