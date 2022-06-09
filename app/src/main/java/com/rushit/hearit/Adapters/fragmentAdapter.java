package com.rushit.hearit.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rushit.hearit.Fragments.FavouritesFragment;
import com.rushit.hearit.Fragments.allSongsFragment;

import java.util.HashMap;
import java.util.Map;

public class fragmentAdapter extends FragmentStateAdapter {

    public fragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       if (position==0){
           return new allSongsFragment();
       }
       return new FavouritesFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
