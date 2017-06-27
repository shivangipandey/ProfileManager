package com.example.shivangipandey.profilemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shiavngi Pandey on 25-06-2017.
 */

public class BootUpReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction()== Intent.ACTION_BOOT_COMPLETED) {
            midnightAlarmStart(context);
        }

        ProfileNames profileNames = getSerializedList(context);
        if(profileNames != null){
            ArrayList<String> arrayList = profileNames.getProfileNameArrayList();

            for(int i = 0;i<arrayList.size();i++){
                if(new Session(context).isProfileActive(arrayList.get(i))){

                    Profiles profiles = getSerializedProfile(arrayList.get(i),context);

                    if(profiles != null && !checkSilenceIntent(profiles,context)){

                        int hourOfDay_start = profiles.getStartHour();
                        int minute_start = profiles.getStartMin();
                        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent1 = new Intent(context.getApplicationContext(),UnsilenceNotifications.class);
                        intent1.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
                        PendingIntent midPI = PendingIntent.getBroadcast(context.getApplicationContext(),profiles.getPendingIntentUnSilenceId(),intent1,PendingIntent.FLAG_NO_CREATE);
                        if(midPI != null) {
                            midPI.cancel();
                            am.cancel(midPI);
                            new Session(context).setProfileActive(false,profiles.getProfile());
                            new ActiveProfiles(context).deleteValue(arrayList.get(i));
                        }
                        setTimeMethod(hourOfDay_start,minute_start,NotificationSilenceReciever.class,"com.example.shivangipandey.notificationoff.NotificationSilenceReciever",profiles.getPendingIntentSilenceId(),profiles,context);
                        Toast.makeText(context,"Alarm enabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private ProfileNames getSerializedList(Context context){
        ObjectInputStream in = null;
        ProfileNames p = null;
        try {
            File file = new File(context.getFilesDir(),"profileNames");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (ProfileNames) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return p;
    }
    private boolean checkSilenceIntent(Profiles profiles,Context context){
        Intent i2 = new Intent(context,NotificationSilenceReciever.class);
        i2.putExtra("profiles",profiles);
        i2.setAction("com.example.shivangipandey.notificationoff.NotificationSilenceReciever");
        PendingIntent midPI2 = PendingIntent.getBroadcast(context,profiles.getPendingIntentSilenceId(),i2,PendingIntent.FLAG_NO_CREATE);
        return midPI2 != null;
    }

    private Profiles getSerializedProfile(String profileName,Context context){
        ObjectInputStream in = null;
        Profiles p = null;
        try {
            File file = new File(context.getFilesDir(),profileName+".ser");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (Profiles) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p;
    }
    private void setTimeMethod(int hourOfDay, int minute, Class<?> cls, String action, int piID,Profiles profiles,Context context){
        boolean flag = false;
        Toast.makeText(context, "time set for "+hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        Calendar midnightCalender = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midnightCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        midnightCalender.set(Calendar.MINUTE,minute);

        if(midnightCalender.before(now)) {

            Calendar calEnd = Calendar.getInstance();
            calEnd.set(Calendar.HOUR_OF_DAY,profiles.getEndHour());
            calEnd.set(Calendar.MINUTE,profiles.getEndMin());

            if(calEnd.after(now))
                flag = true;

            midnightCalender.add(Calendar.DAY_OF_MONTH, 1);
            Toast.makeText(context, "Your profile will activate at "+midnightCalender.getTime()+"from tomorrow", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(context, "Your profile will activate at "+midnightCalender.getTime()+"from today", Toast.LENGTH_LONG).show();

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,cls);
        intent.setAction(action);
        intent.putExtra("profiles",profiles);
        PendingIntent midPI1 = PendingIntent.getBroadcast(context,piID,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        intent.putExtra("pendingIntObj",midPI1);
        am.setRepeating(AlarmManager.RTC_WAKEUP,midnightCalender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,midPI1);

        if(flag)
            context.sendBroadcast(intent);

    }

    private void midnightAlarmStart(Context context){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,new Intent(Intent.ACTION_BOOT_COMPLETED),PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}
