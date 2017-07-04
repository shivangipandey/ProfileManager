package com.example.shivangipandey.profilemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Created by shivangi.pandey on 5/31/2017.
 */

public class UnsilenceNotifications extends BroadcastReceiver{
    Profiles profiles;
    Session session;
    NotificationManager notificationManager;
    int streams[] ={AudioManager.STREAM_RING,AudioManager.STREAM_MUSIC,AudioManager.STREAM_ALARM,AudioManager.STREAM_SYSTEM,AudioManager.STREAM_VOICE_CALL,AudioManager.STREAM_NOTIFICATION};
    @Override
    public void onReceive(Context context, Intent intent) {

        ExtractFromFile extractFromFile = new ExtractFromFile();
        session = new Session(context);
        String profileName = intent.getStringExtra("profiles");
        if(profileName == null){
            Toast.makeText(context,"Restart your App", Toast.LENGTH_SHORT).show();
            return;
        }

        profiles = extractFromFile.deserializeProfile(profileName,context);
        //String pro = intent.getStringExtra("profiles");

        if(profiles == null){
            Toast.makeText(context,"Restart your App", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        profileName = profiles.getProfile();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ActiveProfiles activeProfiles = new ActiveProfiles(context);
        String preProfileName;

        preProfileName = activeProfiles.getPreviousProfile(profileName);
        boolean hasNextValue = activeProfiles.hasNextValue(profileName);

        if(!hasNextValue) {

            if (preProfileName == null)
                preProfileName = profileName;

            if (session.isProfileActive(preProfileName) && !profileName.equals(preProfileName)) {
                checkPrevoiousAlarms(preProfileName, context);
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationManager.setInterruptionFilter(session.getCurrentMode(profileName));
                } else
                    audioManager.setRingerMode(session.getCurrentringerMode(profileName));

                int volume[] = session.getCurrentVolume(profileName);
                if (volume != null) {
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

            }
        }
        session.setProfileActive(false,profiles.getProfile());
        activeProfiles.deleteValue(profileName);
        Toast.makeText(context,profiles.getProfile()+" disabled", Toast.LENGTH_SHORT).show();
        startNotification(context,profiles.getProfile(), profiles.getProfile()+" disabled");

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

    private void startNotification(Context context,String contentTitle,String contentText){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.wake_up)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        //  notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(profiles.getPendingIntentSilenceId(), notification);

    }

    private void checkPrevoiousAlarms(String preProfileName,Context context) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (session.getDoNotDisturbMode(preProfileName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
            Toast.makeText(context, "Do not disturb enabled", Toast.LENGTH_LONG).show();
        } else {
            if (profiles.getGeneralMode()) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Toast.makeText(context, "general mode enabled", Toast.LENGTH_SHORT);
            } else {
                if (session.getAlarmMode(preProfileName)) {
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
                        Toast.makeText(context, "Everything accept alarms disabled", Toast.LENGTH_SHORT);
                    }
                    if (session.getVibrationMode(preProfileName)) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        Toast.makeText(context, "Vibration mode enabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (!session.getGeneralMode(preProfileName) && !session.getVibrationMode(preProfileName)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }

        Profiles preProfile = getSerializedProfile(preProfileName,context);

        if(preProfile != null) {
            int volume[] = preProfile.getvolumes();
            if (volume != null) {
                boolean flag = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE) {
                        flag = false;
                        Toast.makeText(context, "Do Not Disturb mode needs to be disabled to change the volume.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (flag)
                    for (int i = 0; i < volume.length; i++)
                        audioManager.setStreamVolume(streams[i], volume[i], 0);
            }
        }
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

}
