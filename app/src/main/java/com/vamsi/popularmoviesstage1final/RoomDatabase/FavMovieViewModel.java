package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.vamsi.popularmoviesstage1final.ModelMovieData;

import java.util.List;

public class FavMovieViewModel extends AndroidViewModel
{
    private final FavMoviesRepository MyMovieRepository;
    private final LiveData<List<ModelMovieData>> AllResults;

    public FavMovieViewModel (Application application)
    {
        super(application);
        MyMovieRepository = new FavMoviesRepository(application);
        AllResults = MyMovieRepository.getAllmovieResults();
    }

     public LiveData<List<ModelMovieData>> getAllResults() { return AllResults; }
     public ModelMovieData checkMovieInDatabase(String id)
    {
        return MyMovieRepository.checkMovieInDatabase(id);

    }
    public void insert(ModelMovieData result) { MyMovieRepository.insert(result); }
    public void delete(ModelMovieData result) { MyMovieRepository.delete(result); }
}
