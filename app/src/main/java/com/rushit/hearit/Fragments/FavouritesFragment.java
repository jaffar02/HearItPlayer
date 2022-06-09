package com.rushit.hearit.Fragments;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.rushit.hearit.Database.DaoHelper;
import com.rushit.hearit.Database.DaoLayer;
import com.rushit.hearit.Model.Song;
import com.rushit.hearit.R;
import com.rushit.hearit.Recyclers.favSongRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FavouritesFragment extends Fragment {

    RecyclerView favRecyclerView;
    ArrayList<Song> songList;
    View view;
    DaoHelper dbHelper;
    DaoLayer db;
    favSongRecyclerAdapter adapter;
    private static String TAG = "favFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favourites, container, false);
        initializeDatabase();
        songList = new ArrayList<>();
        favRecyclerView = view.findViewById(R.id.favListView);
        favRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void initializeDatabase(){
        dbHelper = Room.databaseBuilder(requireContext(), DaoHelper.class, "fav").build();
        db = dbHelper.Dao();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                songList = (ArrayList<Song>) db.getAllSong();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!songList.isEmpty()) {
                            adapter = new favSongRecyclerAdapter(songList, getContext());
                            favRecyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        }).start();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        db.getAllSong().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                songList = (ArrayList<Song>) songs;
                adapter = new favSongRecyclerAdapter(songList, getContext());
                favRecyclerView.setAdapter(adapter);
            }
        });
    }
}