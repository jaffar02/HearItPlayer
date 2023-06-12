package com.rushit.hearit.Recyclers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.util.concurrent.TimeUnit;

public class allSongsRecyclerAdapter extends RecyclerView.Adapter<allSongsRecyclerAdapter.mViewHolder>{
    private static final String TAG = "recycler";
    Context context;
    ArrayList<Song> songList;
    DaoHelper dbHelper;
    DaoLayer db;

    public allSongsRecyclerAdapter(ArrayList<Song> songList, Context context) {
        this.context = context;
        this.songList = songList;

        Intent forService = new Intent("ForService");
        Bundle bundle = new Bundle();
        bundle.putSerializable("songListBundle", (Serializable) songList);
        forService.putExtra("songList", bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(forService);

        initializeDatabase();
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item_face, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        int p = position;
        String songTrimmedName = truncate(songList.get(position).getSongName(), 30);
        //holder.songName.setText(songList.get(position).getSongName());
        holder.songName.setText(songTrimmedName);
        songList.get(position).setFavEnabled(false);

        holder.songClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("playClickedSong");
                intent.putExtra("position", String.valueOf(p));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (db.isAlready(songList.get(p).getSongName())!=null){
                    songList.get(p).setFavEnabled(true);
                    holder.favourite.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.favourite_icon_foreground));
                }
            }
        }).start();

        holder.favourite.setOnClickListener(v->{
            Log.d(TAG, "onBindViewHolder: "+position);
            if (songList.get(position).isFavEnabled()){
                songList.get(position).setFavEnabled(false);
                holder.favourite.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.favourite_icon_unselect_foreground));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.deleteSong(songList.get(p));
                    }
                }).start();

            }else {
                songList.get(position).setFavEnabled(true);
                holder.favourite.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.favourite_icon_foreground));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.insertSong(songList.get(p));
                    }
                }).start();
            }
        });


    }

    public static String truncate(String str, int len){
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }}


    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class mViewHolder extends RecyclerView.ViewHolder{
        TextView songName;
        ImageView favourite;
        ConstraintLayout songClick;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            favourite = itemView.findViewById(R.id.favouriteBtn);
            songClick = itemView.findViewById(R.id.eachSongClick);
        }

    }

    private void initializeDatabase(){
        dbHelper = Room.databaseBuilder(context, DaoHelper.class, "fav").build();
        db = dbHelper.Dao();
    }
}
