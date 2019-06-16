package com.vamsi.popularmoviesstage1final;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.VH> {

    private final ArrayList<YoutubeData> list;

    public YoutubeAdapter(ArrayList<YoutubeData>list, Context context)
    {
        this.list=list;
        this.context=context;
    }
    private final Context context;
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.youtube_video_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        vh.name.setText(list.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView name;
        VH(View itemView)
        {
            super(itemView);
            name=itemView.findViewById(R.id.youtube_textView);
            itemView.setOnClickListener(this);
        }
        public void onClick(View v)
        {
            int position=getAdapterPosition();
            String key=list.get(position).getKey();
            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_URL+key));
            context.startActivity(intent);
        }
    }
}
