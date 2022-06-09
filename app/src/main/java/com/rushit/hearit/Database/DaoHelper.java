package com.rushit.hearit.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rushit.hearit.Model.Song;

@Database(entities = {Song.class}, version = 1)
public abstract class DaoHelper extends RoomDatabase {
    public abstract DaoLayer Dao();
}
