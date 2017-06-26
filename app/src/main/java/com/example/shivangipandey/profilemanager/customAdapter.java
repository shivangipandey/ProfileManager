package com.example.shivangipandey.profilemanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Created by shivangi.pandey on 6/19/2017.
 */

public class customAdapter extends ArrayAdapter<Profiles> {

    int checkBoxIDS[] = {R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thrusday, R.id.friday, R.id.saturday};
    private CheckBox[] checkBoxes = new CheckBox[7];
    private TextView startTime,endTime,profileName;
    private ImageView mode1,mode2,profilePicture;
    private Context context;
    private Switch aSwitch;
    Animation zoomin,zoomOut;

    public customAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Profiles> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if(convertView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.profile_listview_item,null);
        }

        final Profiles profiles = getItem(position);

        startTime = (TextView)listItemView.findViewById(R.id.start_time);
        endTime = (TextView)listItemView.findViewById(R.id.end_time);
        profileName = (TextView)listItemView.findViewById(R.id.profileName);
        mode1 = (ImageView)listItemView.findViewById(R.id.mode1);
        mode2 = (ImageView)listItemView.findViewById(R.id.mode2);
        profilePicture = (ImageView)listItemView.findViewById(R.id.list_item_imageView);
        aSwitch = (Switch)listItemView.findViewById(R.id.switch1);
        zoomin = AnimationUtils.loadAnimation(context,R.anim.zoom_in);
        zoomOut = AnimationUtils.loadAnimation(context,R.anim.zoom_out);
        profilePicture.setAnimation(zoomin);

        boolean flag = checkSilenceIntent(profiles);
        aSwitch.setChecked(flag);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    int hourOfDay_start = profiles.getStartHour();
                    int minute_start = profiles.getStartMin();
                    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(context.getApplicationContext(),UnsilenceNotifications.class);
                    i.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
                    PendingIntent midPI = PendingIntent.getBroadcast(context.getApplicationContext(),profiles.getPendingIntentUnSilenceId(),i,PendingIntent.FLAG_NO_CREATE);
                    if(midPI != null) {
                        midPI.cancel();
                        am.cancel(midPI);
                        new Session(context).setProfileActive(false,profiles.getProfile());
                    }
                    setTimeMethod(hourOfDay_start,minute_start,NotificationSilenceReciever.class,"com.example.shivangipandey.notificationoff.NotificationSilenceReciever",profiles.getPendingIntentSilenceId(),profiles);
                    Toast.makeText(context,"Alarm enabled", Toast.LENGTH_SHORT).show();
                }
                else{
                    cancelIntents(profiles);
                }
            }
        });

        if(profiles.getBitmap() == null)
            profilePicture.setImageResource(profiles.getImageId());
        else
            profilePicture.setImageBitmap(profiles.getBitmap());

       if(profiles.getDoNotDisturbMode()){
            mode2.setVisibility(View.VISIBLE);
            mode1.setImageResource(R.drawable.ic_alarm_off_black_24dp);
            mode2.setImageResource(R.drawable.ic_volume_off_black_24dp);
         }
        else if(profiles.getGeneralMode()){
                mode2.setVisibility(View.VISIBLE);
                mode2.setImageResource(R.drawable.ic_alarm_on_black_24dp);
                mode1.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
        else if(profiles.getVibrationMode()){
            if(profiles.getAlarmMode())
                mode2.setImageResource(R.drawable.ic_alarm_on_black_24dp);
            else {
                mode2.setVisibility(View.INVISIBLE);
            }
            mode1.setImageResource(R.drawable.ic_vibration_black_24dp);
        }
        else if(profiles.getAlarmMode()){
            mode2.setVisibility(View.VISIBLE);
            mode1.setImageResource(R.drawable.ic_volume_off_black_24dp);
            mode2.setImageResource(R.drawable.ic_alarm_on_black_24dp);
        }


        profileName.setText(profiles.getProfile());
        int hourOfDay = profiles.getStartHour();
        int minute = profiles.getStartMin();
        String hd = hourOfDay+"", min= minute+"";
        if(hourOfDay >=0 && hourOfDay <=9)
            hd = "0"+hd;
        if(minute>=0 &&minute<=9)
            min = "0"+min;
        startTime.setText(hd+":"+min);
        hourOfDay = profiles.getEndHour();
        minute = profiles.getEndMin();
        hd = hourOfDay+"";min= minute+"";
        if(hourOfDay >=0 && hourOfDay <=9)
            hd = "0"+hd;
        if(minute>=0 &&minute<=9)
            min = "0"+min;
        endTime.setText(hd+":"+min);

        for (int i = 0; i < checkBoxIDS.length; i++) {
            checkBoxes[i] = (CheckBox)listItemView.findViewById(checkBoxIDS[i]);
            if(profiles.getDaysOfWeek(i))
                checkBoxes[i].setBackgroundResource(R.drawable.circle_enabled_resource);
            else
                checkBoxes[i].setBackgroundResource(R.drawable.circle_background);
        }
        return listItemView;

    }

    private void setTimeMethod(int hourOfDay, int minute, Class<?> cls, String action, int piID,Profiles profiles){
        boolean flag = false;
        Toast.makeText(context, "time set for "+hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        Calendar midnightCalender = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midnightCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        midnightCalender.set(Calendar.MINUTE,minute);

        if(midnightCalender.before(now)) {

            Calendar toCalendar = Calendar.getInstance();
            toCalendar.set(Calendar.HOUR_OF_DAY,profiles.getEndHour());
            toCalendar.set(Calendar.MINUTE,profiles.getEndMin());
            if(!toCalendar.before(now)){
                flag = true;
            }
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

    private void cancelIntents(Profiles profiles){

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context.getApplicationContext(),UnsilenceNotifications.class);
        i.putExtra("profiles",profiles);
        Intent i2 = new Intent(context.getApplicationContext(),NotificationSilenceReciever.class);
        i2.putExtra("profiles",profiles);
        i.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
        i2.setAction("com.example.shivangipandey.notificationoff.NotificationSilenceReciever");
        PendingIntent midPI = PendingIntent.getBroadcast(context.getApplicationContext(),profiles.getPendingIntentUnSilenceId(),i,PendingIntent.FLAG_NO_CREATE);
        PendingIntent midPI2 = PendingIntent.getBroadcast(context.getApplicationContext(),profiles.getPendingIntentSilenceId(),i2,PendingIntent.FLAG_NO_CREATE);
        if(midPI != null){
            am.cancel(midPI);
            midPI.cancel();
            if(new Session(context).isProfileActive(profiles.getProfile()))
                context.sendBroadcast(i);
        }
        if(midPI2 != null){
            am.cancel(midPI2);
            midPI2.cancel();
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(profiles.getPendingIntentSilenceId());
        }
        Toast.makeText(context,"Pending Intents disabled", Toast.LENGTH_SHORT).show();

    }
    private boolean checkSilenceIntent(Profiles profiles){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i2 = new Intent(context,NotificationSilenceReciever.class);
        i2.putExtra("profiles",profiles);
        i2.setAction("com.example.shivangipandey.notificationoff.NotificationSilenceReciever");
        PendingIntent midPI2 = PendingIntent.getBroadcast(context,profiles.getPendingIntentSilenceId(),i2,PendingIntent.FLAG_NO_CREATE);
        return midPI2 != null;
    }

}
