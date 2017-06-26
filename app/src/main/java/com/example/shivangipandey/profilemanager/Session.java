package com.example.shivangipandey.profilemanager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by shivangi.pandey on 6/14/2017.
 */

public class Session {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public Session(Context context){

        this.context = context;
        sharedPreferences = context.getSharedPreferences("shivangi_pandey9798_sharedprefs",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setProfile(String profile){
        editor.putString("profile",profile);
        editor.apply();
    }
    public void setCurruntMode(int curruntProfile,String profile){
        editor.putInt("currentState"+profile,curruntProfile);
        editor.apply();
    }
    public void setRingerMode(int currentProfile,String profile){
        editor.putInt("currentRingerMode"+profile,currentProfile);
        editor.apply();
    }
    public void setGeneralMode(boolean generalMode,String profile){
        editor.putBoolean("general_mode"+profile,generalMode);
        editor.apply();
    }
    public void setVibrationMode(boolean vibrationMode,String profile){
        editor.putBoolean("vibration_mode"+profile,vibrationMode);
        editor.apply();
    }
    public void setDoNotDisturbMode(boolean doNotDisturbMode,String profile){
        editor.putBoolean("DoNotDisturb_mode"+profile,doNotDisturbMode);
        editor.apply();
    }
    public void setAlarmMode(boolean alarmMode,String profile){
        editor.putBoolean("alarm_mode"+profile,alarmMode);
        editor.apply();
    }
    public void setProfileActive(boolean active,String profile){
        editor.putBoolean("active"+profile,active);
        editor.commit();
    }
    public void setCurrentVolume(int volume[],String profile){
        String name[] = {"ringer","media","alarm","system","voicecall","notification"};
        for(int i=0;i<name.length;i++)
            editor.putInt(name[i]+profile,volume[i]);
        editor.apply();
    }

    public String getProfile(){
        return sharedPreferences.getString("profile", null);
    }
    public int getCurrentMode(String profile){
        return sharedPreferences.getInt("currentState"+profile, NotificationManager.INTERRUPTION_FILTER_ALL);
    }
    public int getCurrentringerMode(String profile){
        return sharedPreferences.getInt("currentRingerMode"+profile, AudioManager.RINGER_MODE_NORMAL);
    }
    public boolean getGeneralMode(String profile){
        return sharedPreferences.getBoolean("general_mode"+profile,false);
    }
    public boolean getVibrationMode(String profile){
        return sharedPreferences.getBoolean("vibration_mode"+profile,false);
    }
    public boolean getDoNotDisturbMode(String profile){
        return sharedPreferences.getBoolean("DoNotDisturb_mode"+profile,false);
    }
    public boolean getAlarmMode(String profile){
        return sharedPreferences.getBoolean("alarm_mode"+profile,true);
    }
    public int[] getCurrentVolume(String profile){

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        String name[] = {"ringer","media","alarm","system","voicecall","notification"};
        int streams[] ={AudioManager.STREAM_RING,AudioManager.STREAM_MUSIC,AudioManager.STREAM_ALARM,AudioManager.STREAM_SYSTEM,AudioManager.STREAM_VOICE_CALL,AudioManager.STREAM_NOTIFICATION};
        int volume[]=new int[name.length];
        for(int i =0;i<name.length;i++)
            volume[i] = sharedPreferences.getInt(name[i]+profile,audioManager.getStreamVolume(streams[i]));
        return volume;
    }
    public boolean isProfileActive(String profile){
        return sharedPreferences.getBoolean("active"+profile,false);
    }
}
