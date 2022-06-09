package com.rushit.hearit.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.rushit.hearit.Database.MySharedPrefrences;
import com.rushit.hearit.Model.Song;
import com.rushit.hearit.R;
import com.rushit.hearit.Recyclers.allSongsRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class allSongsFragment extends Fragment {

    View view;
    RecyclerView allSongsRecycler;
    allSongsRecyclerAdapter adapter;
    ArrayList<Song> songsList;
    final int MY_PERMISSION_CODE = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        allSongsRecycler = view.findViewById(R.id.allSongsRecycler);
        allSongsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        songsList = new ArrayList<>();

        return view;
    }

    private ArrayList<Song> getSongList(){
        ArrayList<Song> tempList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                ,MY_PERMISSION_CODE);
            }else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        ,MY_PERMISSION_CODE);
            }
        }else{

            String [] projection = {
                    MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION
            };

            String selection = MediaStore.Audio.Media.IS_MUSIC+" !=0";

            ContentResolver cr = requireActivity().getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cs = cr.query(uri, projection, selection, null, null);
            if (cs!=null && cs.moveToFirst()){
                //int SongTitle = cs.getColumnIndex(MediaStore.Audio.Media.TITLE);

                do {
                    //String currentTitle = cs.getString(0);
                    Song song = new Song(cs.getString(0), cs.getString(1), cs.getString(2));
                    if (new File(song.getPath()).exists()){
                        tempList.add(song);
                    }

                }while (cs.moveToNext());
            }
        }
        return tempList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case MY_PERMISSION_CODE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                                songsList = getSongList();
                        }else {
                            Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                }
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        songsList = getSongList();
         //compulsory to set adapter on resume to refresh list to service and also content in fragment
        if (songsList.size()>0) {
            adapter = new allSongsRecyclerAdapter(songsList, requireContext());
            allSongsRecycler.setAdapter(adapter);
        }else {
            Toast.makeText(getContext(), "No Songs found!", Toast.LENGTH_SHORT).show();
        }
    }

}