package com.example.android.movie_app_stage_two.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movie_app_stage_two.R;
import com.example.android.movie_app_stage_two.model.Trailers;
import com.squareup.picasso.Picasso;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private TextView tv_trailer_name, tv_no_trailer;
    private ImageView iv_trailer_thumbnail;
    private Trailers tTrailers[];
    private Context tContext;

    public TrailerAdapter(Trailers[] trailers, Context context){this.tTrailers = trailers; this.tContext = context;}

    @NonNull
    @Override
    public TrailerAdapter.TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_trailers_items, parent, false);

        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerHolder holder, int position) {

        final String trailer_key, youtube_base_url, vnd_base_url;
        String trailer_name;

        trailer_name = tTrailers[position].getName();
        trailer_key = tTrailers[position].getKey();

        tv_trailer_name.setText(trailer_name);

        youtube_base_url= "https://www.youtube.com/watch?v=";
        vnd_base_url= "vnd.youtube:";

        iv_trailer_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vnd_base_url.concat(trailer_key)));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(youtube_base_url.concat(trailer_key)));
                try {
                    tContext.startActivity(youtubeIntent);
                } catch (ActivityNotFoundException ex) {
                    tContext.startActivity(webIntent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return tTrailers.length;
    }

    class TrailerHolder extends RecyclerView.ViewHolder{

        TrailerHolder(View itemView){
            super(itemView);

            tv_trailer_name = itemView.findViewById(R.id.tv_trailer_name);
            tv_no_trailer = itemView.findViewById(R.id.tv_no_trailer);

            iv_trailer_thumbnail = itemView.findViewById(R.id.iv_trailer_thumbnail);
        }

    }
}
