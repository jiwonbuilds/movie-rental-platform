package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText mtitle;
    private final String host = "18.118.141.11";
    private final String port = "8443";
    private final String domain = "cs122b-project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mtitle = binding.movieTitle;
        final Button searchButton = binding.buttonSearch;
        int pageNum = getIntent().getIntExtra("page", 1);
        searchButton.setOnClickListener(view -> retrieveMovies(pageNum));
    }

    @SuppressLint("SetTextI18n")
    private void retrieveMovies(int pageNum) {

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/search?mtitle=" + mtitle.getText().toString()
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
                        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", (Serializable)newMovies);
                        MovieListPage.putExtra("movieTitle", mtitle.getText().toString());
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
        queue.add(loginRequest);
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