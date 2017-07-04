package com.example.shivangipandey.profilemanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by shivangi.pandey on 5/31/2017.
 */

public class NotificationSilenceReciever extends BroadcastReceiver {

    Profiles profiles;
    String profileName;
    Session session;
    @Override
    public void onReceive(Context context, Intent intent) {
        ExtractFromFile extractFromFile = new ExtractFromFile();
        boolean flag1 = true;
        session = new Session(context);
        profileName = intent.getStringExtra("profiles");
        if(profileName == null){
                Toast.makeText(context,"ProfilesName = null in nottificationSilenceReciever",Toast.LENGTH_SHORT).show();
                return;
        }
        profiles = extractFromFile.deserializeProfile(profileName,context);
        if(profiles == null){
            Toast.makeText(context,"Profiles = null in nottificationSilenceReciever",Toast.LENGTH_SHORT).show();
            return;
        }
        profileName = profiles.getProfile();

        if(checkForDays()) {
            try {
                if (intent.getBooleanExtra("FLAGG", false)) {
                    stopP(context);
                }
                else {

                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!notificationManager.isNotificationPolicyAccessGranted()) {
                            Intent intent1 = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                        while (!notificationManager.isNotificationPolicyAccessGranted())
                            flag1 = false;
                        flag1 = true;
                    }
                    if(flag1)
                        setAllThings(context,notificationManager,audioManager);
                }
            }
            catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, "Do not disturb can't be setup due to some internal error", Toast.LENGTH_SHORT).show();
            }
            }
        else{
                checkUnsilencePIExist(context, true);
            }

    }

    private void setAllThings(Context context,NotificationManager notificationManager,AudioManager audioManager) throws Exception{

        startNotification(context);

        session.setProfile(profiles.getProfile());
        session.setProfileActive(true, profileName);
        new ActiveProfiles(context).addValue(profileName);
        session.setAlarmMode(false, profileName);
        session.setDoNotDisturbMode(false, profileName);
        session.setGeneralMode(false, profileName);
        session.setVibrationMode(false, profileName);

        int streams[] ={AudioManager.STREAM_RING,AudioManager.STREAM_MUSIC,AudioManager.STREAM_ALARM,AudioManager.STREAM_SYSTEM,AudioManager.STREAM_VOICE_CALL,AudioManager.STREAM_NOTIFICATION};
        int volume[] = new int[streams.length];

        for(int i =0;i<streams.length;i++){
            volume[i]=audioManager.getStreamVolume(streams[i]);
        }

        session.setCurrentVolume(volume,profileName);

        if (!(checkUnsilencePIExist(context, false))) {
            setPendingIntentUnSilenceRecievr(context);
        }

        volume = profiles.getvolumes();
        if(volume!=null) {
            boolean flag = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE) {
                    flag = false;
                    Toast.makeText(context,"Do Not Disturb mode needs to be disabled to change the volume.", Toast.LENGTH_SHORT).show();
                }
            }
            if(flag)
                for (int i = 0; i < volume.length; i++)
                    audioManager.setStreamVolume(streams[i], volume[i], 0);
        }

        session.setRingerMode(audioManager.getRingerMode(), profileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            session.setCurruntMode(notificationManager.getCurrentInterruptionFilter(), profileName);
        }

        if (profiles.getDoNotDisturbMode()) {
            session.setDoNotDisturbMode(true, profileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            } else
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(context, "Do not disturb enabled", Toast.LENGTH_LONG).show();
        } else {
            if (profiles.getGeneralMode()) {
                session.setGeneralMode(true, profileName);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Toast.makeText(context, "general mode enabled", Toast.LENGTH_SHORT);
            } else {
                if (profiles.getAlarmMode()) {
                    session.setAlarmMode(true, profileName);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
                    } else {
                        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        if (alert == null) {
                            // alert is null, using backup
                            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            if (alert == null) {
                                // alert backup is null, using 2nd backup
                                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                            }
                        }
                        RingtoneManager.getRingtone(context, alert).setStreamType(AudioManager.STREAM_ALARM);
                    }
                    Toast.makeText(context, "Everything accept alarms disabled", Toast.LENGTH_SHORT);
                }
                if (profiles.getVibrationMode()) {
                    session.setVibrationMode(true, profileName);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    Toast.makeText(context, "Vibration mode enabled", Toast.LENGTH_SHORT).show();
                }
            }
            if (!profiles.getGeneralMode() && !profiles.getVibrationMode()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }

    }

    private boolean checkUnsilencePIExist(Context context,boolean disable){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(context,UnsilenceNotifications.class);
            i.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
            PendingIntent midPI = PendingIntent.getBroadcast(context,profiles.getPendingIntentUnSilenceId(),i,PendingIntent.FLAG_NO_CREATE);
            if(midPI != null) {
                if(disable){
                    am.cancel(midPI);
                    midPI.cancel();
                    if(midPI == null)
                     Toast.makeText(context,"Do not Disturb disabled in between.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        else
            return false;

    }

    private void startNotification(Context context){

        Intent i = new Intent(context,NotificationSilenceReciever.class);
        i.putExtra("profiles",profileName);
        i.putExtra("FLAGG",true);

        PendingIntent pi = PendingIntent.getBroadcast(context,0,i,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.sleep)
                .setContentTitle(profileName)
                .setContentText(profileName+" activated")
                .addAction(R.mipmap.ic_launcher,"press to stop",pi)
                .setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(profiles.getPendingIntentSilenceId(),notification);

    }


    private boolean checkForDays(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(profiles.getDaysOfWeek(day-1))
            return true;
        else
            return false;
    }

    private void stopP(Context context){
        checkUnsilencePIExist(context,true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(profiles.getPendingIntentSilenceId());
        Intent i = new Intent("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
        i.putExtra("profiles",profileName);
        context.sendBroadcast(i);

    }

    private void setPendingIntentUnSilenceRecievr(Context context){
        int hourOfDay_end = profiles.getEndHour();
        int minute_end = profiles.getEndMin();
        setTimeMethod(hourOfDay_end,minute_end, UnsilenceNotifications.class, "com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever",profiles.getPendingIntentUnSilenceId(),context);
    }

    private void setTimeMethod(int hourOfDay, int minute, Class<?> cls, String action, int piID,Context context){
        Toast.makeText(context, "End time set for "+hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        Calendar midnightCalender = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midnightCalender.set(Calendar.SECOND,0);
        midnightCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        midnightCalender.set(Calendar.MINUTE,minute);
        if(midnightCalender.before(now))
            midnightCalender.add(Calendar.DAY_OF_MONTH,1);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context,cls);
        intent.setAction(action);
        intent.putExtra("profiles",profileName);
       // intent.putExtra("profiles","hey");

        PendingIntent midPI = PendingIntent.getBroadcast(context,piID,intent,PendingIntent.FLAG_CANCEL_CURRENT);
    //    intent.putExtra("pendingIntObj",midPI);
        am.setRepeating(AlarmManager.RTC_WAKEUP,midnightCalender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,midPI);

    }

}
