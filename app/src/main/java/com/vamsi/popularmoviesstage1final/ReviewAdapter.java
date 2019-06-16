package com.vamsi.popularmoviesstage1final;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>
{
    private final ArrayList<ReviewData> dataList;

    public ReviewAdapter(List<ReviewData> dataList, Context context) {
        this.dataList = (ArrayList<ReviewData>) dataList;
        this.context = context;
    }

    private final Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.review_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        holder.author.setText(dataList.get(position).getAuthor());
        holder.content.setText(dataList.get(position).getComment());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView author;
        final TextView content;
        private ViewHolder(View itemView) {
            super(itemView);
            author=itemView.findViewById(R.id.author_tv_id);
            content=itemView.findViewById(R.id.comment_tv_id);
        }
    }
}