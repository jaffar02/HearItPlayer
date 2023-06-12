package com.rushit.hearit.Recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.rushit.hearit.Database.DaoHelper;
import com.rushit.hearit.Database.DaoLayer;
import com.rushit.hearit.Model.Song;
import com.rushit.hearit.R;

import java.io.Serializable;
import java.util.ArrayList;

public class favSongRecyclerAdapter extends RecyclerView.Adapter<favSongRecyclerAdapter.fViewHolder>{
    private static final String TAG = "FavRecycler";
    Context context;
    ArrayList<Song> songList;

    public favSongRecyclerAdapter(ArrayList<Song> songList, Context context) {
        this.context = context;
        this.songList = songList;
        Intent forService = new Intent("FavForService");
        Bundle bundle = new Bundle();
        bundle.putSerializable("FavSongListBundle", (Serializable) songList);
        forService.putExtra("FavSongList", bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(forService);
    }

    @NonNull
    @Override
    public fViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item_face, parent, false);
        return new fViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull fViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int p = position;
        String songTrimmedName = allSongsRecyclerAdapter.truncate(songList.get(position).getSongName(), 30);
        holder.songName.setText(songTrimmedName);
        holder.favourite.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.favourite_icon_foreground));

        holder.songClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("playClickedSongFav");
                intent.putExtra("positionFav", String.valueOf(p));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class fViewHolder extends RecyclerView.ViewHolder{
        TextView songName;
        ImageView favourite;
        ConstraintLayout songClick;
        public fViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            favourite = itemView.findViewById(R.id.favouriteBtn);
            songClick = itemView.findViewById(R.id.eachSongClick);
        }

    }



}

