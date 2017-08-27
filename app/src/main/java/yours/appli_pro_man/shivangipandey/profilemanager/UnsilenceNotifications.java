package yours.appli_pro_man.shivangipandey.profilemanager;

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
import android.provider.Settings;
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
        boolean onlyLeft = false;
        ExtractFromFile extractFromFile = new ExtractFromFile();
        session = new Session(context);
        String profileName = intent.getStringExtra("profiles");
        if(profileName == null){
            Toast.makeText(context,"Restart your App.", Toast.LENGTH_SHORT).show();
            return;
        }

        profiles = extractFromFile.deserializeProfile(profileName,context);
        //String pro = intent.getStringExtra("profiles");

        if(profiles == null){
            Toast.makeText(context,"Restart your App.", Toast.LENGTH_SHORT).show();
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

            if (preProfileName == null) {
                //preProfileName = session.getFirstProfile();
                preProfileName = profileName;
               onlyLeft = true;
            }

            if (session.isProfileActive(preProfileName) && !profileName.equals(preProfileName)) {
                checkPrevoiousAlarms(preProfileName, context);
            }
            else {
                String name;
                if(onlyLeft)
                    name = session.getFirstProfile();
                else
                    name = profileName;

                if (session.getSDKVersion() >= Build.VERSION_CODES.M) {
                    notificationManager.setInterruptionFilter(session.getCurrentMode(name));
                } else
                    audioManager.setRingerMode(session.getCurrentringerMode(name));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(Settings.System.canWrite(context))
                        setBrightness(session.getCurrebtBrightness(name),context);
                }
                else
                    setBrightness(session.getCurrebtBrightness(name),context);

                int volume[] = session.getCurrentVolume(name);
                if (volume != null) {
                        for (int i = 1; i < volume.length; i++) {
                            audioManager.setStreamVolume(streams[i], volume[i], 0);
                        }
                        audioManager.setStreamVolume(streams[0],volume[0],0);
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
            if (session.getSDKVersion() >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
            //audioManager.setStreamVolume(streams[0],2,0);
            Toast.makeText(context, "Do not disturb enabled", Toast.LENGTH_LONG).show();
        } else {
            if (profiles.getGeneralMode()) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Toast.makeText(context, "general mode enabled", Toast.LENGTH_SHORT).show();
            } else {
                if (session.getVibrationMode(preProfileName)) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING,0,0);
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,0);
                    Toast.makeText(context, "Vibration mode enabled", Toast.LENGTH_SHORT).show();
                }
                else if (session.getAlarmMode(preProfileName)) {
                    if (session.getSDKVersion() >= Build.VERSION_CODES.M) {
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
                        Toast.makeText(context, "Everything accept alarms disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (!session.getGeneralMode(preProfileName) && !session.getVibrationMode(preProfileName)) {
                if (session.getSDKVersion() >= Build.VERSION_CODES.M) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }

        Profiles preProfile = getSerializedProfile(preProfileName,context);

        if(preProfile != null) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(Settings.System.canWrite(context))
                    setBrightness(preProfile.getBrightness(),context);
            }
            else
                setBrightness(preProfile.getBrightness(),context);


            int volume[] = preProfile.getvolumes();
            if (volume != null) {
                    for (int i = 1; i < volume.length; i++)
                        audioManager.setStreamVolume(streams[i], volume[i], 0);
                    audioManager.setStreamVolume(streams[0],volume[0],0);
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
    public void setBrightness(int brightness,Context context){
        android.provider.Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
    }
}
