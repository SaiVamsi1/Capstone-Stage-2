package com.vamsi.popularmoviesstage1final;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.vamsi.popularmoviesstage1final.RoomDatabase.FavMovieViewModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private RecyclerView mainRecyclerView;

    private List<ModelMovieData> results;
    private FavMovieViewModel favMovieViewModel;
    private GridLayoutManager gridLayoutManager;
    String total_json_data = null;
    SharedPreferences sharedPreferences;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    private AdView adView;
    ImageView dp;
    Button signOutButton;
    TextView displayNametv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dp=findViewById(R.id.photo);
        signOutButton=findViewById(R.id.signoutbutton);
        signOutButton.setVisibility(View.INVISIBLE);
        displayNametv=findViewById(R.id.dpname);
        displayNametv.setVisibility(View.INVISIBLE);

        MobileAds.initialize(this, (getString(R.string.aaid)));
        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
        signInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(view -> signIn());
        mainRecyclerView=findViewById(R.id.recycler_view);
        sharedPreferences = getSharedPreferences((getString(R.string.ded)),MODE_PRIVATE);
        results=new ArrayList<>();
        gridLayoutManager=new GridLayoutManager(this,2);
        favMovieViewModel= ViewModelProviders.of(this).get(FavMovieViewModel.class);
        RecyclerView.LayoutManager mainLayoutManager;
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
        {
            mainLayoutManager =new GridLayoutManager(this,3);
        }
        else
        {
            mainLayoutManager =new GridLayoutManager(this,2);
        }
        mainRecyclerView.setLayoutManager(mainLayoutManager);
        if(sharedPreferences!=null && sharedPreferences.getBoolean((getString(R.string.faav)),false)){
            openFavorites();
        }else if(savedInstanceState!=null && savedInstanceState.containsKey(Constants.JSON_DATA)){
            total_json_data = savedInstanceState.getString(Constants.JSON_DATA);
            setDataOnRecyclerView(total_json_data);
        }else if(amIConnected())
        {
            fetchMovies();
        }
        else{
            Toast.makeText(this, (getString(R.string.noi)), Toast.LENGTH_LONG).show();
            openFavorites();
        }
        signOutButton.setOnClickListener(view -> signOut());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(MainActivity.this,(getString(R.string.sot)),Toast.LENGTH_SHORT).show();
                    dp.setVisibility(View.INVISIBLE);
                    signOutButton.setVisibility(View.INVISIBLE);
                    signInButton.setVisibility(View.VISIBLE);
                    displayNametv.setVisibility(View.INVISIBLE);
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            Toast.makeText(this, (getString(R.string.hi))+acct.getDisplayName(), Toast.LENGTH_SHORT).show();
            Uri personPhoto = acct.getPhotoUrl();
            Glide.with(this).load(personPhoto).into(dp);
            displayNametv.setVisibility(View.VISIBLE);
            String profileName=acct.getDisplayName();
            displayNametv.setText(profileName);
            dp.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w((getString(R.string.se)), (getString(R.string.se2)) + e.getStatusCode());
            Toast.makeText(MainActivity.this, (getString(R.string.fa)), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            Uri personPhoto = acct.getPhotoUrl();
            Glide.with(this).load(personPhoto).into(dp);
            displayNametv.setVisibility(View.VISIBLE);
            String profileName=acct.getDisplayName();
            displayNametv.setText(profileName);
            dp.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }

    private void fetchMovies()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.faav),false);
        editor.apply();
        new FetchingData().execute(Constants.POPULAR_QUERY);
    }

    private boolean amIConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network=connectivityManager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.sort_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.popular_menu:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.faav),false);
                editor.apply();
                setTitle(getString(R.string.popular_menu));
                if(amIConnected())
            {
                new FetchingData().execute(Constants.POPULAR_QUERY);
            }
            else{
                Toast.makeText(this, (getString(R.string.noii)), Toast.LENGTH_LONG).show();
            }

                break;
            case R.id.top_rated_menu:
                SharedPreferences.Editor editortop = sharedPreferences.edit();
                editortop.putBoolean(getString(R.string.faav),false);
                editortop.apply();
                setTitle(getString(R.string.top_rated_menu));
                if(amIConnected())
                {
                    new FetchingData().execute(Constants.TOP_RATED_QUERY);
                }
                else{
                    Toast.makeText(this, (getString(R.string.nooooi)), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.favourites:
                SharedPreferences.Editor editorfav = sharedPreferences.edit();
                editorfav.putBoolean(getString(R.string.faav),true);
                editorfav.apply();
                setTitle(R.string.favm);
                openFavorites();

        }
        return true;
    }

    private void openFavorites() {
        setTitle(Constants.FM);
       favMovieViewModel.getAllResults().observe(this, modelMovieData -> {
           results = modelMovieData;
           FavAdapter favAdapter = new FavAdapter(MainActivity.this, modelMovieData);
           mainRecyclerView.setLayoutManager(gridLayoutManager);
           mainRecyclerView.setAdapter(favAdapter);
       });

    }


    public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewInformations>
    {
        final Context context;
        final List<ModelMovieData> lists;


        FavAdapter(Context context, List<ModelMovieData> lists) {
            this.context = context;
            this.lists = lists;
        }

        @NonNull
        @Override
        public FavAdapter.ViewInformations onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewInformations(LayoutInflater.from(context).inflate(R.layout.movie_thumbnail,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FavAdapter.ViewInformations viewInformations, int i)
        {
            Picasso.with(context).load((getString(R.string.iimgu))+lists.get(i).getPosterpath()).into(viewInformations.imageView);

            viewInformations.itemView.setOnClickListener(view ->
            {
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra(Constants.MOVIE,lists.get(i));
                context.startActivity(intent);
            });
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }

        public class ViewInformations extends RecyclerView.ViewHolder {
            final ImageView imageView;
            ViewInformations(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.movie_main_image_view);

            }
        }
    }

    private ModelMovieData[] moviesDataToArray(String movieJsonResults) throws JSONException
    {
        JSONObject movieJson = new JSONObject(movieJsonResults);
        JSONArray resultsArray = movieJson.getJSONArray(Constants.Results);

        ModelMovieData[] movies = new ModelMovieData[resultsArray.length()];

        for (int i=0;i<resultsArray.length();i++)
        {
            movies[i] = new ModelMovieData();

            JSONObject movie=resultsArray.getJSONObject(i);
            movies[i].setOriginalTitle(movie.getString(Constants.Original_title));
            movies[i].setOverview(movie.getString(Constants.Overview));
            movies[i].setPosterpath(movie.getString(Constants.Poster_path));
            movies[i].setVoters(movie.getDouble(Constants.Voter_Average));
            movies[i].setReleaseDate(movie.getString(Constants.Release_Date));
            movies[i].setId(movie.getString(Constants.Id));
            movies[i].setVoteCount(movie.getInt(Constants.Vote_Count));
        }
        return movies;

    }


    class FetchingData extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            String movieResults=null;
            try {
                URL url =NetworkTask.buildURL(strings);
                movieResults=NetworkTask.getResponsefromurl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return movieResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String movies)
        {
            total_json_data = movies;
            setDataOnRecyclerView(movies);
        }

    }

    public void setDataOnRecyclerView(String result){
        try {
            ModelMovieData[] movies = moviesDataToArray(result);
            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), movies);
            mainRecyclerView.setAdapter(imageAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(Constants.JSON_DATA,total_json_data);
    }
}
