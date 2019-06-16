package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.vamsi.popularmoviesstage1final.ModelMovieData;
import com.vamsi.popularmoviesstage1final.R;

@Database(entities = {ModelMovieData.class}, version = 1, exportSchema = false)
public abstract class FavMovieDatabase extends RoomDatabase {

    public abstract FavMovieDao myfavMoviedao();

    private static FavMovieDatabase V;

    public static FavMovieDatabase getDatabase(final Context context) {
        if (V == null) {
            synchronized (FavMovieDatabase.class) {
                if (V == null) {
                    V = Room.databaseBuilder(context.getApplicationContext(),
                            FavMovieDatabase.class, context.getString(R.string.ff))
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return V;
    }

}