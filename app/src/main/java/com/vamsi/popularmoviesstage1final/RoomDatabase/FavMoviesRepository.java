package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.vamsi.popularmoviesstage1final.ModelMovieData;

import java.util.List;

class FavMoviesRepository {

    private final FavMovieDao FavMovieDao;
    private final LiveData<List<ModelMovieData>> AllmovieResults;

    FavMoviesRepository(Application application) {
        FavMovieDatabase moviedb = FavMovieDatabase.getDatabase(application);
        FavMovieDao = moviedb.myfavMoviedao();
        AllmovieResults = FavMovieDao.getlivedataMovies();
    }


    public LiveData<List<ModelMovieData>> getAllmovieResults() {
        return AllmovieResults;
    }

    public void insert (ModelMovieData movieResult) {
        new insertTask(FavMovieDao).execute(movieResult);
    }
    public void delete (ModelMovieData movieResult) {
        new deleteTask(FavMovieDao).execute(movieResult);
    }
    public ModelMovieData checkMovieInDatabase(String id)
    {
        return FavMovieDao.checkMovieInDatabase(id);
    }
    private static class insertTask extends AsyncTask<ModelMovieData, Void, Void> {

        private final FavMovieDao AsyncTaskDao;

        insertTask(FavMovieDao dao) {
            AsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ModelMovieData... params) {
            AsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class deleteTask extends AsyncTask<ModelMovieData, Void, Void> {

        private final FavMovieDao AsyncTaskDao;

        deleteTask(FavMovieDao dao) {
            AsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ModelMovieData... params) {
            AsyncTaskDao.delete(params[0]);
            return null;
        }
    }

}