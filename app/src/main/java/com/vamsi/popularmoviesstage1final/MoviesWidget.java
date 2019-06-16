package com.vamsi.popularmoviesstage1final;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
/**
 * Implementation of App Widget functionality.
 */
public class MoviesWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sharedPreferences=context.getSharedPreferences((context.getString(R.string.sdfg)),0);
        String MovieName=sharedPreferences.getString((context.getString(R.string.jhvg)),(context.getString(R.string.kljh)));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.movies_widget);
        views.setTextViewText(R.id.widget_movie_name,MovieName);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

