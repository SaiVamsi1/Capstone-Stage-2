package com.vamsi.popularmoviesstage1final;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.vamsi.popularmoviesstage1final.RoomDatabase.FavMovieViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
{
    private final ArrayList<YoutubeData> list = new ArrayList<>();
    private RecyclerView YoutubeRecycler;
    private YoutubeAdapter youtubeAdapter;


    private List<ReviewData> reviewDataList;
    private RecyclerView reviewRecycler;
    private ReviewAdapter reviewAdapter;


    private FavMovieViewModel viewModel;
    private ModelMovieData movie;

    private Button fav;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        reviewDataList=new ArrayList<>();
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(FavMovieViewModel.class);

        fav=findViewById(R.id.button2);


        YoutubeRecycler = findViewById(R.id.youtube_recycler);
        reviewRecycler=findViewById(R.id.review_recycler_id);

        LinearLayoutManager yl = new LinearLayoutManager(this);
        YoutubeRecycler.setLayoutManager(yl);

        LinearLayoutManager r1 = new LinearLayoutManager(this);
        reviewRecycler.setLayoutManager(r1);

        Intent intent=getIntent();
        ModelMovieData movie = (ModelMovieData) intent.getSerializableExtra(getString(R.string.movie));

        ImageView thumbnailIV= findViewById(R.id.imageView);
        TextView titleTV=findViewById(R.id.movie_title);
        TextView ratingTV=findViewById(R.id.movie_rating);
        TextView releasedateTV=findViewById(R.id.movie_release_date);
        TextView overviewTV=findViewById(R.id.movie_overview);
        setTitle(movie.getOriginalTitle());

        Picasso.with(this)
                .load(getString(R.string.imgbaseurl)+movie.getPosterpath())
                .fit()
                .error(R.drawable.movie_icon)
                .placeholder(R.drawable.movie_icon)
                .into(thumbnailIV);
        titleTV.setText(movie.getOriginalTitle());
        ratingTV.setText(movie.getVoters()+"/10");
        releasedateTV.setText(movie.getReleaseDate());
        overviewTV.setText(movie.getOverview());
        Youtube(movie.getId());
        reviewfetcher(movie.getId());
        updateButton(movie.getId());
    }

    private void Youtube(String id)
    {
        RequestQueue queue=Volley.newRequestQueue(this);
        Uri uri=Uri.parse((getString(R.string.y1))+ id + (getString(R.string.y2)));
        String url=uri.toString();
        StringRequest request=new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject root = new JSONObject(response);
                JSONArray array = root.optJSONArray((getString(R.string.result)));
                int l = array.length();
                for (int i = 0; i < l; i++) {
                    JSONObject object = array.optJSONObject(i);
                    String key = object.optString((getString(R.string.key)));
                    String name = object.optString((getString(R.string.name)));
                    list.add(new YoutubeData(name , key));
                    youtubeAdapter = new YoutubeAdapter(list , DetailActivity.this);
                    YoutubeRecycler.setAdapter(youtubeAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error instanceof TimeoutError
                    || error instanceof AuthFailureError || error instanceof ParseError
                    || error instanceof NetworkError || error instanceof ServerError) {
                new AlertDialog.Builder(DetailActivity.this)
                        .setMessage(R.string.error)
                        .show();
            }

        });
        queue.add(request);
    }

    private void reviewfetcher(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = (getString(R.string.r1))+ id + (getString(R.string.r2));
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray array = root.optJSONArray((getString(R.string.result)));
                        if (array.length() == 0) {
                            Toast.makeText(DetailActivity.this,(getString(R.string.noreviewt)), Toast.LENGTH_SHORT).show();
                        }
                        int len = array.length();
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            String author = jsonObject.optString((getString(R.string.author)));
                            String comment = jsonObject.optString((getString(R.string.content)));

                            ReviewData data=new ReviewData(author,comment);
                            reviewDataList.add(data);
                            reviewAdapter=new ReviewAdapter(reviewDataList,DetailActivity.this);
                            reviewRecycler.setAdapter(reviewAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    if (error instanceof TimeoutError
                            || error instanceof AuthFailureError || error instanceof ParseError
                            || error instanceof NetworkError || error instanceof ServerError) {
                        new AlertDialog.Builder(DetailActivity.this)
                                .setMessage(R.string.error)
                                .show();
                    }

                });
        queue.add(request);
    }

    private void updateButton(String id)
    {
        ModelMovieData r=viewModel.checkMovieInDatabase(id);
        if(r!=null)
        {
            fav.setText((getString(R.string.UnFav)));
        }
        else {
            fav.setText((getString(R.string.Fav)));
        }
    }


    public void loadfav(View view)
    {
        String f=fav.getText().toString();
        if (f.equalsIgnoreCase((getString(R.string.UnFav))))
        {
            delete();
            fav.setText((getString(R.string.Fav)));
        }
        else
        {
            insert();
            fav.setText((getString(R.string.UnFav)));
        }

    }


    private void insert()  {
        Intent intent=getIntent();
        movie = (ModelMovieData) intent.getSerializableExtra((getString(R.string.movie)));
        String name = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String releasedate = movie.getReleaseDate();
        String poster = movie.getPosterpath();
        Double rating = movie.getVoters();
        String id = movie.getId();
        Integer votecount=movie.getVoteCount();
        ModelMovieData movie=new ModelMovieData();
        movie.setId(id);
        movie.setOriginalTitle(name);
        movie.setPosterpath(poster);
        movie.setOverview(overview);
        movie.setReleaseDate(releasedate);
        movie.setVoters(rating);
        movie.setVoteCount(votecount);

        sp=getSharedPreferences((getString(R.string.img)),0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString((getString(R.string.mn)),movie.getOriginalTitle());
        editor.apply();

        Intent widgetIntent=new Intent(this,MoviesWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(
                new ComponentName(getApplication(), MoviesWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(widgetIntent);

        viewModel.insert(movie);
        Toast.makeText(this, movie.getOriginalTitle()+(getString(R.string.af)), Toast.LENGTH_SHORT).show();
    }

    private void delete() {
        Intent intent=getIntent();
        movie = (ModelMovieData) intent.getSerializableExtra((getString(R.string.movie)));
        String name = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String releasedate = movie.getReleaseDate();
        String poster = movie.getPosterpath();
        Double rating = movie.getVoters();
        String id = movie.getId();
        Integer voteCount = movie.getVoteCount();
        ModelMovieData movie=new ModelMovieData();
        movie.setOriginalTitle(name);
        movie.setVoteCount(voteCount);
        movie.setPosterpath(poster);
        movie.setOverview(overview);
        movie.setId(id);
        movie.setVoters(rating);
        movie.setReleaseDate(releasedate);
        viewModel.delete(movie);
        Toast.makeText(this, movie.getOriginalTitle()+(getString(R.string.df)), Toast.LENGTH_SHORT).show();
    }
}