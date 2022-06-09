package com.rushit.hearit.Util;

import android.media.MediaPlayer;

public class myMediaPlayer {
   static MediaPlayer instance;
   public static MediaPlayer getInstance(){
      if (instance == null) {
          instance = new MediaPlayer();
      }
      return instance;
   }
   public static int currentIndex = -1;
}
