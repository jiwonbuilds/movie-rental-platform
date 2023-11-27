package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    TextView title;
    TextView rating;
    TextView director;
    TextView genres;
    TextView stars;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);

        title = findViewById(R.id.title);
        rating = findViewById(R.id.rating);
        director = findViewById(R.id.director);
        stars = findViewById(R.id.stars);
        genres = findViewById(R.id.genres);

        title.setText(getIntent().getStringExtra("movieTitle") + " (" + getIntent().getStringExtra("year") + ")");
        rating.setText("☆" + getIntent().getStringExtra("rating"));
        director.setText("Director: "+getIntent().getStringExtra("director"));
        stars.setText("Stars" + getIntent().getStringExtra("stars"));
        genres.setText("Genres" + getIntent().getStringExtra("genres"));
    }


}
