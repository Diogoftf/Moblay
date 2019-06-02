package com.example.moblay.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.example.moblay.Model.VideoModel;
import com.example.moblay.R;
import com.example.moblay.Vibration;
import com.example.moblay.VideoPlayerActivity;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    ArrayList<VideoModel> arrayListVideos;
    Activity activity;
    Vibration vib;

    public VideoAdapter(Context context, ArrayList<VideoModel> arrayListVideos, Activity activity) {
        this.context = context;
        this.arrayListVideos = arrayListVideos;
        this.activity = activity;

        vib = new Vibration();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load("file://" + arrayListVideos.get(position).getStr_thumb()).skipMemoryCache(false).into(holder._imageView);
        holder._rl_selected.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder._rl_selected.setAlpha(0);

        holder._rl_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vib.vibratePhone(context);

                Intent i = new Intent(context, VideoPlayerActivity.class);

                // create a string array with paths of videos
                ArrayList<String> myPaths = new ArrayList<String>();

                //add paths
                for( int k = 0; k < arrayListVideos.size(); k++){
                    myPaths.add(arrayListVideos.get(k).getStr_path());
                }

                i.putExtra("paths", myPaths);
                i.putExtra("position", position);

                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _imageView;
        RelativeLayout _rl_selected;

        public ViewHolder(View itemView) {
            super(itemView);

            _imageView = (ImageView)itemView.findViewById(R.id.iv_image);
            _rl_selected = (RelativeLayout)itemView.findViewById(R.id.rl_selected);
        }
    }


}
