package com.rushit.hearit.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Song implements Serializable {
    @PrimaryKey @NonNull
    String songName;
    String path;
    String duration;
    boolean favEnabled;

    public Song(String songName, String path, String duration){
        this.songName = songName;
        this.path = path;
        this.duration = duration;
    }

    public boolean isFavEnabled() {
        return favEnabled;
    }

    public void setFavEnabled(boolean favEnabled) {
        this.favEnabled = favEnabled;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
