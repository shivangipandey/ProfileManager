package com.example.shivangipandey.profilemanager;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity{

    private EditText mProfileName;
    private Button startTime, endTime,mMode,mSave,mCancel,mVolume;
    int checkBoxIDS[] = {R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thrusday, R.id.friday, R.id.saturday};
    private CheckBox[] checkBoxes = new CheckBox[7];
    boolean daysEnable[] = new boolean[7];
    Profiles profiles;
    ProfileNames profileNames;
    String oldProfileName;
    boolean editProfile = false;
    int position = -1;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profiles = new Profiles();

        startTime = (Button) findViewById(R.id.start_time);
        endTime = (Button) findViewById(R.id.endTime);
        mProfileName = (EditText) findViewById(R.id.profileNameEditText);
        mSave = (Button)findViewById(R.id.saveProfile);
        mCancel = (Button)findViewById(R.id.cancelProfile);
        profileNames = (ProfileNames)getIntent().getSerializableExtra("profileNames");
        imageView = (ImageView)findViewById(R.id.imageView);

        for (int i = 0; i < checkBoxIDS.length; i++) {
            checkBoxes[i] = (CheckBox) findViewById(checkBoxIDS[i]);
        }

        if(getIntent().hasExtra("profile")){
            profiles = (Profiles)getIntent().getSerializableExtra("profile");
            mProfileName.setText(profiles.getProfile());
            oldProfileName = profiles.getProfile();
            position = getIntent().getIntExtra("position",0);
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
            for (int i=0;i<checkBoxIDS.length;i++){
                checkBoxes[i].setChecked(profiles.getDaysOfWeek(i));
                daysEnable[i]=profiles.getDaysOfWeek(i);
                if(checkBoxes[i].isChecked())
                    checkBoxes[i].setBackgroundResource(R.drawable.circle_enabled_resource);
            }
            editProfile = true;
            if(profiles.getBitmap() != null)
                imageView.setImageBitmap(profiles.getBitmap());
            else
                imageView.setImageResource(profiles.getImageId());
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "Long press on image, to add your own icon!", Toast.LENGTH_LONG).show();
            }
        });

        for (int i = 0; i < checkBoxIDS.length; i++) {
            checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView == checkBoxes[0]) {
                        if (isChecked) {
                            daysEnable[0] = true;
                            checkBoxes[0].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[0] = false;
                            checkBoxes[0].setBackgroundResource(R.drawable.circle_background);
                        }
                    } else if (buttonView == checkBoxes[1]) {
                        if (isChecked) {
                            daysEnable[1] = true;
                            checkBoxes[1].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[1] = false;
                            checkBoxes[1].setBackgroundResource(R.drawable.circle_background);
                        }
                    } else if (buttonView == checkBoxes[2]) {
                        if (isChecked) {
                            daysEnable[2] = true;
                            checkBoxes[2].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[2] = false;
                            checkBoxes[2].setBackgroundResource(R.drawable.circle_background);
                        }
                    } else if (buttonView == checkBoxes[3]) {
                        if (isChecked) {
                            daysEnable[3] = true;
                            checkBoxes[3].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[3] = false;
                            checkBoxes[3].setBackgroundResource(R.drawable.circle_background);
                        }
                    } else if (buttonView == checkBoxes[4]) {
                        if (isChecked) {
                            daysEnable[4] = true;
                            checkBoxes[4].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[4] = false;
                            checkBoxes[4].setBackgroundResource(R.drawable.circle_background);
                        }
                    } else if (buttonView == checkBoxes[5]) {
                        if (isChecked) {
                            daysEnable[5] = true;
                            checkBoxes[5].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[5] = false;
                            checkBoxes[5].setBackgroundResource(R.drawable.circle_background);
                        }
                    } else if (buttonView == checkBoxes[6]) {
                        if (isChecked) {
                            daysEnable[6] = true;
                            checkBoxes[6].setBackgroundResource(R.drawable.circle_enabled_resource);
                        } else {
                            daysEnable[6] = false;
                            checkBoxes[6].setBackgroundResource(R.drawable.circle_background);
                        }
                    }

                }
            });

        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (profiles != null) {
                    Toast.makeText(EditProfileActivity.this, profiles.getProfile(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EditProfileActivity.this,ProfilePictureDialogActivity.class);
                    i.putExtra("profile",profiles);
                    startActivityForResult(i,4);
                }
                return true;
            }
        });

        mMode = (Button) findViewById(R.id.mode_button);
        mMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfileActivity.this, modeDialogActivity.class);
                i.putExtra("profile", profiles);
                startActivityForResult(i,2);
            }
        });

        mVolume = (Button)findViewById(R.id.volume_button);
        mVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfileActivity.this, VolumeActivity.class);
                i.putExtra("profile", profiles);
                startActivityForResult(i,3);
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(R.id.start_time, EditProfileActivity.this, startTime, profiles);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });


        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(R.id.endTime, EditProfileActivity.this, endTime, profiles);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileName = mProfileName.getText().toString();
                if(TextUtils.isEmpty(profileName)){
                    Toast.makeText(EditProfileActivity.this, "Enter profile name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if(profileName.length() > 10){
                        Toast.makeText(EditProfileActivity.this,"Profile name must be under 10 characters limit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(editProfile) {
                        if (profileNames != null && profileNames.containsProfile(profileName)&& (!profileName.equals(oldProfileName))) {
                            Toast.makeText(EditProfileActivity.this, profileName + " profile already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    else {
                        if (profileNames != null && profileNames.containsProfile(profileName)) {
                            Toast.makeText(EditProfileActivity.this, profileName + " profile already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(!profiles.getVibrationMode() && !profiles.getGeneralMode() && !profiles.getAlarmMode() && !profiles.getDoNotDisturbMode()){
                        Toast.makeText(EditProfileActivity.this,"Choose a mode.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(profiles.getStartHour()-profiles.getEndHour()==0 && profiles.getStartMin()-profiles.getEndMin()==0){
                        Toast.makeText(EditProfileActivity.this,"Set a finite interval", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    profiles.resetProfileName(profileName);
                }
                for(int i =0;i<daysEnable.length;i++) {
                    profiles.setDaysOfWeek(i, daysEnable[i]);
                }
                if(profileNames == null) {
                    profileNames = new ProfileNames();
                }
                if(editProfile) {
                    profileNames.renameProfileName(profileName, position);
                    delFile(oldProfileName);
                }
                else {
                    profileNames.SetProfileNames(profileName);
                    int ascii = calculatePendingIntentSilenceId(profileName);
                    profiles.setPendingSilenceIntent(ascii);
                    profiles.setPendingIntentUnSilenceId(ascii+profileName.length());
                }

                int hourOfDay_start = profiles.getStartHour();
                int minute_start = profiles.getStartMin();

                AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(EditProfileActivity.this,UnsilenceNotifications.class);
                i.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
                PendingIntent midPI = PendingIntent.getBroadcast(EditProfileActivity.this,profiles.getPendingIntentUnSilenceId(),i,PendingIntent.FLAG_NO_CREATE);
                if(midPI != null) {
                    am.cancel(midPI);
                    midPI.cancel();
                    new Session(EditProfileActivity.this).setProfileActive(false,profiles.getProfile());
                }

                setTimeMethod(hourOfDay_start,minute_start,NotificationSilenceReciever.class,"com.example.shivangipandey.notificationoff.NotificationSilenceReciever",profiles.getPendingIntentSilenceId());

                makeListSerializable(profileNames);
                makeProfileObjSerializable(profileName,profiles);
                returnMain();
            }

        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.right_out,R.anim.left_in);
            }
        });
    }

    private void returnMain(){
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
    private void makeProfileObjSerializable(String proName,Profiles profile){
        try {
            File outputFile = new File(getFilesDir(),proName+".ser");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
            out.writeObject(profile);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void makeListSerializable(ProfileNames profileNames){

        try {
            File outputFile = new File(getFilesDir(),"profileNames");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
            out.writeObject(profileNames);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void delFile(String oldName){
       File profileFile = new File(getFilesDir().getAbsolutePath(),oldName+"ser");
        profileFile.delete();
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && data!= null && profiles!=null){
            profiles.setGeneralMode(data.getBooleanExtra("gen",false));
            profiles.setVibrationMode(data.getBooleanExtra("vib",false));
            profiles.setAlarmMode(data.getBooleanExtra("ala",false));
            profiles.setDoNotDisturbMode(data.getBooleanExtra("sil",false));
        }
        if(requestCode == 3 && data!=null && profiles!=null){
            AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
            String name[] = {"ringer","media","alarm","system","voicecall","notification"};
            int streams[] ={AudioManager.STREAM_RING,AudioManager.STREAM_MUSIC,AudioManager.STREAM_ALARM,AudioManager.STREAM_SYSTEM,AudioManager.STREAM_VOICE_CALL,AudioManager.STREAM_NOTIFICATION};
            int volume[] = new int[name.length];
            for(int i =0;i<volume.length;i++){
                volume[i] = data.getIntExtra(name[i],audioManager.getStreamVolume(streams[i]));
            }
            profiles.setVolume(volume);
        }
        if(requestCode == 4 && data!=null && profiles!=null){
            profiles.setImageId(data.getIntExtra("imageId",R.drawable.default_pic));

            int bitmapActive = data.getIntExtra("bitmap",-1);

            if(bitmapActive == 0) {
                Bitmap bitmap = getBitmapImg();
                profiles.setBitmap(bitmap);
                imageView.setImageBitmap(profiles.getBitmap());
            }
            else {
                profiles.setBitmap(null);
                imageView.setImageResource(profiles.getImageId());
            }
        }
    }

    private void setTimeMethod(int hourOfDay, int minute, Class<?> cls, String action, int piID){
        boolean flag = false;
        Toast.makeText(this, "time set for "+hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        Calendar midnightCalender = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midnightCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        midnightCalender.set(Calendar.MINUTE,minute);

        if(midnightCalender.before(now)) {
            midnightCalender.add(Calendar.DAY_OF_MONTH, 1);
            Toast.makeText(this, "Your profile will activate at "+midnightCalender.getTime()+" from tomorrow", Toast.LENGTH_LONG).show();

            Calendar toCalendar = Calendar.getInstance();
            toCalendar.set(Calendar.HOUR_OF_DAY,profiles.getEndHour());
            toCalendar.set(Calendar.MINUTE,profiles.getEndMin());
            if(!toCalendar.before(now)){
                flag = true;
            }
        }
        else
            Toast.makeText(this, "Your profile will activate at "+midnightCalender.getTime()+" from today", Toast.LENGTH_LONG).show();
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,cls);
        intent.setAction(action);
        intent.putExtra("profiles",profiles);
        PendingIntent midPI = PendingIntent.getBroadcast(this,piID,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        intent.putExtra("pendingIntObj",midPI);
        am.setRepeating(AlarmManager.RTC_WAKEUP,midnightCalender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,midPI);

        if(flag)
            sendBroadcast(intent);
    }

    private int calculatePendingIntentSilenceId(String name) {
        int ascii = 0;
        for (int i = 0; i < name.length(); i++) {
            ascii += (int) name.charAt(i);
        }
        return ascii;
    }
    private Bitmap getBitmapImg(){
        File file = new File(getCacheDir(),"bitmap_file");
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}
