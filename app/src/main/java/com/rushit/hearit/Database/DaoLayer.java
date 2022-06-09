package com.rushit.hearit.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.rushit.hearit.Model.Song;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DaoLayer {

    @Query("select * from song where songName = :songName")
    Song getSong(String songName);

    @Insert
    void insertSong(Song song);

    @Query("select * from song where songName = :songName")
    Song isAlready(String songName);

    @Delete
    void deleteSong(Song song);

    @Query("select * from song")
    LiveData<List<Song>> getAllSong();
}
