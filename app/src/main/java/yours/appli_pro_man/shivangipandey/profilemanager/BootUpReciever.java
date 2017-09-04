package yours.appli_pro_man.shivangipandey.profilemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shiavngi Pandey on 25-06-2017.
 */

public class BootUpReciever extends BroadcastReceiver {
    ExtractFromFile extractFromFile;
    @Override
    public void onReceive(Context context, Intent intent) {

        extractFromFile = new ExtractFromFile();
        Log.e("started","                 BOOTRECIEVERSTARTED");
        if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            midnightAlarmStart(context);
           // Toast.makeText(context,"Boot Up reciever starts", Toast.LENGTH_SHORT).show();
        }

        //ProfileNames profileNames = getSerializedList(context);
        ProfileNames profileNames = extractFromFile.deserializedProfileNamesList(context, "profileNames");
        if(profileNames != null){
            ArrayList<String> arrayList = profileNames.getProfileNameArrayList();
            Session session = new Session(context);
            for(int i = 0;i<arrayList.size();i++){
                Profiles profiles = extractFromFile.deserializeProfile(arrayList.get(i),context);

                if( profiles != null && session.getEnabled(profiles.getProfile())){
                    if(!checkSilenceIntent(profiles,context)){
                        Log.e("started","                 BOOTRECIEVERSTARTED in");
                        int hourOfDay_start = profiles.getStartHour();
                        int minute_start = profiles.getStartMin();
                        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent1 = new Intent(context,UnsilenceNotifications.class);
                        intent1.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
                        PendingIntent midPI = PendingIntent.getBroadcast(context.getApplicationContext(),profiles.getPendingIntentUnSilenceId(),intent1,PendingIntent.FLAG_NO_CREATE);
                        if(midPI != null) {
                            midPI.cancel();
                            am.cancel(midPI);
                            new Session(context).setProfileActive(false,profiles.getProfile());
                            new ActiveProfiles(context).deleteValue(arrayList.get(i));
                        }
                        setTimeMethod(hourOfDay_start,minute_start,NotificationSilenceReciever.class,"com.example.shivangipandey.notificationoff.NotificationSilenceReciever",profiles.getPendingIntentSilenceId(),profiles,context);
                    }
                }
            }
        }
        profileNames = (ProfileNames)extractFromFile.deserializedProfileNamesList(context,"profileNames_Maps");
        if(profileNames != null){
            ArrayList<String> arrayList = profileNames.getProfileArrayListMap();
            Session session = new Session(context);
            for(int i = 0;i<arrayList.size();i++){
                Profiles profiles = extractFromFile.deserializeProfile(arrayList.get(i),context);

                if( profiles != null && session.getEnabled(profiles.getProfile())){
                    if(!checkSilenceIntent(profiles,context)){
                        Log.e("started","                 BOOTRECIEVERSTARTED in");
                        setProximityAlert(profiles.getLatLng().longitude,profiles.getLatLng().latitude,profiles.getRadius(),profiles.getPendingIntentProximityReieverId(),context,profiles.getProfile());
                    }
                }
            }
        }
    }
    private void setProximityAlert(double longitude, double lattitude, float radius, int requestCode,Context context,String profileName) {
        String proximityIntentAction = "yours.appli_wea_rep.proximityalertmodule.ProximityAlert";

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent(proximityIntentAction);
        intent.putExtra("profile", profileName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
        } else {
            locationManager.addProximityAlert(lattitude, longitude, radius, -1, pendingIntent);
        }

    }

    private boolean checkSilenceIntent(Profiles profiles,Context context){
        Intent i2 = new Intent(context,NotificationSilenceReciever.class);
        i2.putExtra("profiles",profiles);
        i2.setAction("com.example.shivangipandey.notificationoff.NotificationSilenceReciever");
        PendingIntent midPI2 = PendingIntent.getBroadcast(context,profiles.getPendingIntentSilenceId(),i2,PendingIntent.FLAG_NO_CREATE);
        return midPI2 != null;
    }

    private void setTimeMethod(int hourOfDay, int minute, Class<?> cls, String action, int piID,Profiles profiles,Context context){
        boolean flag = false;
     //   Toast.makeText(context, "time set for "+hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        Calendar midnightCalender = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midnightCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        midnightCalender.set(Calendar.MINUTE,minute);

        if(midnightCalender.before(now)) {

            Calendar calEnd = Calendar.getInstance();
            calEnd.set(Calendar.HOUR_OF_DAY,profiles.getEndHour());
            calEnd.set(Calendar.MINUTE,profiles.getEndMin());

            if(calEnd.after(now)) {
                flag = true;
            }
            midnightCalender.add(Calendar.DAY_OF_MONTH, 1);
        }
        //Toast.makeText(context,profiles.getProfile()+" activated",Toast.LENGTH_SHORT).show();

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,cls);
        intent.setAction(action);
        intent.putExtra("profiles",profiles.getProfile());
        PendingIntent midPI1 = PendingIntent.getBroadcast(context,piID,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP,midnightCalender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,midPI1);

        if(flag)
            context.sendBroadcast(intent);

    }

    private void midnightAlarmStart(Context context){
        Intent i = new Intent(context,BootUpReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 2);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        if(calendar.before(cal))
            calendar.add(Calendar.DAY_OF_MONTH,1);
        //calendar.add(Calendar.MINUTE,3);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}
