package com.vamsi.popularmoviesstage1final;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>
{
    private final ModelMovieData[] movies;
    private final Context context;
    ImageAdapter(Context context, ModelMovieData[] movies)
    {
        this.movies=movies;
        this.context=context;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        final ImageView imageView;
        ViewHolder(ImageView v)
        {
            super(v);
            imageView=v;
        }
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int positin)
    {
        ImageView v=(ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_thumbnail,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        Picasso.with(context)
                .load((context.getString(R.string.ii))+movies[i].getPosterpath())
                .fit()
                .placeholder(R.drawable.movie_icon)
                .error(R.mipmap.ic_launcher_round)
                .into((ImageView)viewHolder.imageView.findViewById(R.id.movie_main_image_view));

        viewHolder.itemView.setOnClickListener(view ->
        {
            Intent intent=new Intent(context,DetailActivity.class);
            intent.putExtra((context.getString(R.string.ede)),movies[i]);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (movies==null||movies.length==0)
        {
            return -1;
        }
        return movies.length;
    }
}