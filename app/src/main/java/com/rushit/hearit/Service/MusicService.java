package com.rushit.hearit.Service;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.media.session.MediaSession.Token;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.app.NotificationCompat;

import com.rushit.hearit.Model.Song;
import com.rushit.hearit.R;
import com.rushit.hearit.Util.myMediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicService extends Service {
    public static final String TAG = "myService";
    ArrayList<Song> songList;
    int currentSong;
    public MediaPlayer mPlayer;
    IBinder binder = new LocalBinder();
    public boolean isSelected;
    private String status="not";
    public final String playPauseIntentString = "PLAY_MUSIC";
    public final String nextIntentString = "NEXT_MUSIC";
    public final String previousIntentString = "PREVIOUS_MUSIC";
    public final String closeIntentString = "CLOSE_PLAYER";
    Notification notification;
    public NotificationManager notificationManager;

    public MusicService(ArrayList<Song> songList){
        this.songList = songList;
    }
    public MusicService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = myMediaPlayer.getInstance();
        Log.d(TAG, "Player initialized");

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        BroadcastReceiver FavBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra("FavSongList");
                songList = (ArrayList<Song>) bundle.getSerializable("FavSongListBundle");
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(FavBroadcastReceiver, new IntentFilter("FavForService"));

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Yes");
                Bundle bundle = intent.getBundleExtra("songList");
                songList = (ArrayList<Song>) bundle.getSerializable("songListBundle");
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter("ForService"));

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                status = "done";
                Intent intent = new Intent("songCompleted");
                intent.putExtra("songStatus", status);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case playPauseIntentString:
                if (isSelected){
                    if (isPlaying()) {
                        pauseMusic();
                        Intent intent1 = new Intent("notiP");
                        intent1.putExtra("pauseIt", "yes");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
                        //ShowNotification(getCurPlayStateIcon());
                    }else{
                        playMusic();
                        Intent intent1 = new Intent("notiP");
                        intent1.putExtra("pauseIt", "no");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
                        //ShowNotification(getCurPlayStateIcon());
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Select a song", Toast.LENGTH_SHORT).show();
                }
                break;

            case previousIntentString:
                if (isSelected){
                    playPreviousSong();
                    Intent intent1 = new Intent("noti");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
                }else {
                    Toast.makeText(getApplicationContext(), "Select a song", Toast.LENGTH_SHORT).show();
                }
                break;

            case nextIntentString:
                if (isSelected){
                    playNextSong();
                    Intent intent1 = new Intent("noti");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
                }else {
                    Toast.makeText(getApplicationContext(), "Select a song", Toast.LENGTH_SHORT).show();
                }
                break;

            case closeIntentString:
                stopSelf();
                stopForeground(true);
                onDestroy();
                break;
        }
        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playThisSong(int position){
           isSelected = true;
           currentSong = position;
           Song song = songList.get(position);
           mPlayer.reset();
           try {
               mPlayer.setDataSource(song.getPath());
               mPlayer.prepare();
               Log.d(TAG, "Player prepared");
               mPlayer.start();
               ShowNotification(getCurPlayStateIcon());
               startForeground(1001, notification);
           } catch (IOException e) {
               Log.d(TAG, "playThisSong: " + e.getMessage());
           }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playNextSong(){
        if (currentSong>=0 && currentSong<songList.size()-1 && mPlayer!=null){
            currentSong+=1;
            Song song = songList.get(currentSong);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(song.getPath());
                mPlayer.prepare();
                mPlayer.start();
                ShowNotification(getCurPlayStateIcon());
                startForeground(1001, notification);
            } catch (IOException e) {
                Log.d(TAG, "playThisSong: "+e.getMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playPreviousSong(){
        if (currentSong>0 && currentSong<songList.size() && mPlayer!=null){
            currentSong-=1;
            Song song = songList.get(currentSong);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(song.getPath());
                mPlayer.prepare();
                mPlayer.start();
                ShowNotification(getCurPlayStateIcon());
                startForeground(1001, notification);
            } catch (IOException e) {
                Log.d(TAG, "playThisSong: "+e.getMessage());
            }
        }
    }

    public void pauseMusic(){
        if (mPlayer!=null){
            mPlayer.pause();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ShowNotification(getCurPlayStateIcon());
                startForeground(1001, notification);
            }
        }
    }

    public void playMusic(){
        if (mPlayer!=null){
            if (isSelected) {
                mPlayer.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ShowNotification(getCurPlayStateIcon());
                    startForeground(1001, notification);
                }
            }
        }
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public class LocalBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public String getCurrentSongName(){
        if (mPlayer!=null && songList!=null){
                return songList.get(currentSong).getSongName();
        }
        return null;
    }

    public String getTDuration(){
        String duration = calculateAbsoluteDuration(songList.get(currentSong).getDuration());
        if (mPlayer!=null && songList!=null){
            return duration;
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public String  calculateAbsoluteDuration(String duration){
        Long millis = Long.parseLong(duration);
        return  String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    public int getMDuration(){
        if (mPlayer!=null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public int progress(){
        return mPlayer.getCurrentPosition();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ShowNotification(int curPlayState){
        Intent playIntent = new Intent(getApplicationContext(), MusicService.class);
        playIntent.setAction(playPauseIntentString);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 1001, playIntent, PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(getApplicationContext(), MusicService.class);
        nextIntent.setAction(nextIntentString);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 1001, nextIntent, PendingIntent.FLAG_MUTABLE);

        Intent previousIntent = new Intent(getApplicationContext(), MusicService.class);
        previousIntent.setAction(previousIntentString);
        PendingIntent previousPendingIntent = PendingIntent.getService(this, 1001, previousIntent, PendingIntent.FLAG_MUTABLE);

        Intent closeIntent = new Intent(getApplicationContext(), MusicService.class);
        previousIntent.setAction(closeIntentString);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 1001, closeIntent, PendingIntent.FLAG_MUTABLE);


        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon_test);

        MediaSession mediaSession = new MediaSession(getApplicationContext(), "player");
        MediaSession.Token token = mediaSession.getSessionToken();

        String CHANNEL_ID = "channelId";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText(getCurrentSongName())
                .setContentTitle(getCurrentSongName())
                .setSmallIcon(R.drawable.launcher_icon_test)
                .setLargeIcon(bm)
                .addAction(new Notification.Action(R.drawable.previous_icon_foreground, "Previous", previousPendingIntent))
                .addAction(new Notification.Action(curPlayState, "Play", playPendingIntent))
                .addAction(new Notification.Action(R.drawable.next_icon_foreground, "Next", nextPendingIntent))
                .addAction(new Notification.Action(R.drawable.close_icon_foreground, "Exit", closePendingIntent))
                .setStyle(new Notification.MediaStyle().setMediaSession(token))
                .setOnlyAlertOnce(true).build();
        //notificationManager.notify(0, notification);
    }

    private int getCurPlayStateIcon() {
        if (isPlaying()){
            return R.drawable.pause_icon_foreground;
        }else{
            return R.drawable.play_icon_foreground;
        }
    }

}

