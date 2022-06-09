package com.rushit.hearit.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPrefrences {
    SharedPreferences prefs;
    private Context gContext;
    private SharedPreferences.Editor editor;
    private final String PREF_NAME = "HEARIT";
    private final int PREF_MODE = 0;
    public final String key_name = "key";

    public MySharedPrefrences(Context context){
        if (context!=null){
            gContext = context;
            prefs = context.getSharedPreferences(PREF_NAME, PREF_MODE);
            editor = prefs.edit();
        }
    }

    public void setFirstTime(boolean getBoolean){
        editor.putBoolean(key_name, getBoolean);
        editor.commit();
    }

    public boolean getFirstTime(){
        return prefs.getBoolean(key_name, false);
    }

}
