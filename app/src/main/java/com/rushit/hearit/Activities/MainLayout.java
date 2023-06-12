
package com.rushit.hearit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.rushit.hearit.Adapters.fragmentAdapter;
import com.rushit.hearit.Fragments.FavouritesFragment;
import com.rushit.hearit.Fragments.allSongsFragment;
import com.rushit.hearit.Model.Song;
import com.rushit.hearit.R;
import com.rushit.hearit.Service.MusicService;

import java.util.ArrayList;

public class MainLayout extends AppCompatActivity {

    private static final String TAG = "mainLayout";
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 VP;
    fragmentAdapter adapter;
    BroadcastReceiver musicOnCompleteBroadcast, playClickedSong, favPlayClickedSong;
    ImageView play, previous, next, pause;
    MusicService musicService;
    ServiceConnection serviceConnection;
    TextView currentSongName, totalLength, currentLength;
    SeekBar progress;
    DrawerLayout drawer;
    NavigationView navView;
    boolean check = false;
    ArrayList<Song> songsList;
    boolean isOk = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tab);
        VP = findViewById(R.id.viewPager);
        play = findViewById(R.id.playBtn);
        next = findViewById(R.id.nextBtn);
        previous = findViewById(R.id.previousBtn);
        pause = findViewById(R.id.pauseBtn);
        currentSongName = findViewById(R.id.currentSongPlaying);
        totalLength = findViewById(R.id.endingMusic);
        currentLength = findViewById(R.id.startingMusic);
        progress = findViewById(R.id.seekBar);
        songsList = new ArrayList<>();

        configureTabsAndFragments();

        musicOnCompleteBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("songStatus").toString().equals("done")){
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }else{
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
            }
        };

        playClickedSong = new BroadcastReceiver() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onReceive(Context context, Intent intent) {
                if (!intent.getStringExtra("position").isEmpty()){
                    if (Integer.parseInt(intent.getStringExtra("position"))>=0){
                        int position = Integer.parseInt(intent.getStringExtra("position"));
                        Log.d(TAG, "onReceive: all broadcast called");
                        musicService.playThisSong(position);
                        if (musicService!=null && check) {
                            Log.d(MusicService.TAG, "Setting Resources");
                            setResources();
                            setSeekBarConfiguration();
                            progress.setProgress(0);
                            progress.setMax(musicService.getMDuration());
                            if (musicService.isPlaying()) {
                                play.setVisibility(View.GONE);
                                pause.setVisibility(View.VISIBLE);
                            } else {
                                pause.setVisibility(View.GONE);
                                play.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        };

        favPlayClickedSong = new BroadcastReceiver() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onReceive(Context context, Intent intent) {
                if (!intent.getStringExtra("positionFav").isEmpty()){
                    if (Integer.parseInt(intent.getStringExtra("positionFav"))>=0){
                        int position = Integer.parseInt(intent.getStringExtra("positionFav"));
                        Log.d(TAG, "onReceive: fav broadcast called");
                        musicService.playThisSong(position);
                        if (musicService!=null && check) {
                            setResources();
                            setSeekBarConfiguration();
                            progress.setProgress(0);
                            progress.setMax(musicService.getMDuration());
                            if (musicService.isPlaying()) {
                                play.setVisibility(View.GONE);
                                pause.setVisibility(View.VISIBLE);
                            } else {
                                pause.setVisibility(View.GONE);
                                play.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        };


        play.setOnClickListener(v->{
           if (!musicService.isPlaying() && musicService.isSelected) {
               play.setVisibility(View.GONE);
               pause.setVisibility(View.VISIBLE);
               musicService.playMusic();
           }else{
               Toast.makeText(getApplicationContext(), "Select a song first", Toast.LENGTH_SHORT).show();
           }
        });

        pause.setOnClickListener(v->{
            if (musicService.isPlaying()) {
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            musicService.pauseMusic();
            }
        });

        next.setOnClickListener(v->{
            if (musicService!=null && check) {
                if (musicService.isSelected) {
                    if (musicService.isPlaying()) {
                        progress.setProgress(0);
                        progress.setMax(musicService.getMDuration());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            musicService.playNextSong();
                        }
                        setResources();
                        setSeekBarConfiguration();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Select a song first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BroadcastReceiver notiBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setResources();
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(notiBroadCast, new IntentFilter("noti"));

        BroadcastReceiver notiPBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: "+intent.getStringExtra("pauseIt"));
                if (intent.getStringExtra("pauseIt").equals("yes")){
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }else{
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(notiPBroadCast, new IntentFilter("notiP"));


        previous.setOnClickListener(v->{
            if (musicService!=null && check) {
                if (musicService.isSelected) {
                    if (musicService.isPlaying()) {
                        progress.setProgress(0);
                        progress.setMax(musicService.getMDuration());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            musicService.playPreviousSong();
                        }
                        setResources();
                        setSeekBarConfiguration();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Select a song first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (musicService!=null && check && b && musicService.mPlayer!=null){
                    musicService.mPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
                musicService = binder.getService();
                check = true;
                Log.d(TAG, "onServiceConnected: service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                check = false;
                Log.d(TAG, "onServiceConnected: service not connected");
            }
        };

        Intent startIntent = new Intent(this, MusicService.class);
        bindService(startIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    public void setResources(){
        currentSongName.setText(musicService.getCurrentSongName());
        totalLength.setText(musicService.getTDuration());
    }

    public void setSeekBarConfiguration(){
        MainLayout.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService!=null && check){
                    progress.setMax(musicService.getMDuration());
                    progress.setProgress(musicService.progress());
                    currentLength.setText(musicService.calculateAbsoluteDuration(musicService.progress()+""));
                }
                new Handler().postDelayed(this, 180);
            }
        });
    }


    private void configureTabsAndFragments(){
        tabLayout.addTab(tabLayout.newTab().setText("ALL SONGS"));
        tabLayout.addTab(tabLayout.newTab().setText("FAVOURITES"));

        FragmentManager fm = getSupportFragmentManager();
        adapter = new fragmentAdapter(fm, getLifecycle());
        VP.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                VP.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        VP.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(musicOnCompleteBroadcast, new IntentFilter("songCompleted"));
        LocalBroadcastManager.getInstance(this).registerReceiver(playClickedSong, new IntentFilter("playClickedSong"));
        LocalBroadcastManager.getInstance(this).registerReceiver(favPlayClickedSong, new IntentFilter("playClickedSongFav"));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicOnCompleteBroadcast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playClickedSong);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(favPlayClickedSong);
    }
}