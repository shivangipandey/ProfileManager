package yours.appli_pro_man.shivangipandey.profilemanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.io.File;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity{

    ExtractFromFile extractFromFile;
    int checkBoxIDS[] = {R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thrusday, R.id.friday, R.id.saturday};
    boolean daysEnable[] = new boolean[7];
    Profiles profiles;
    ProfileNames profileNames;
    String oldProfileName;
    boolean editProfile = false;
    int position = -1;
    ImageView imageView;
    BoomMenuButton bmb;
    CircularImageView circularImageView;
    int stringIds[] = {R.string.mode,R.string.volume,R.string.to_do_list};
    int stringIdTxts[] = {R.string.mode_Txt,R.string.volume_Txt,R.string.todolist_txt};
    int imgeRes[] = {R.drawable.ic_wb_sunny_black_24dp,R.drawable.ic_speaker_phone_black_24dp,R.drawable.ic_pets_black_24dp};
    Session session;
    ScrollView scrollView;
    SeekBar brightnessSeekbar;
    int brightness = -1;
    Switch brightnessSwitch;
    private EditText mProfileName;
    private Button startTime, endTime, mSave, mCancel;
    private CheckBox[] checkBoxes = new CheckBox[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        session = new Session(this);

        bmb = findViewById(R.id.action_bar_right_bmb);
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);

        extractFromFile = new ExtractFromFile();
        profiles = new Profiles();

        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.endTime);
        mProfileName = findViewById(R.id.profileNameEditText);
        mSave = findViewById(R.id.saveProfile);
        mCancel = findViewById(R.id.cancelProfile);
        profileNames = (ProfileNames)getIntent().getSerializableExtra("profileNames");
        imageView = findViewById(R.id.imageView);
        circularImageView = findViewById(R.id.circle_icon);
        scrollView = findViewById(R.id.scrollView);
        brightnessSeekbar = findViewById(R.id.brightness_seekbar);
        brightnessSeekbar.setMax(225);
        brightnessSwitch = findViewById(R.id.switchBrightness);
        brightnessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profiles.setBrightnessEnabled(isChecked);
                if(!isChecked)
                    brightnessSeekbar.setEnabled(false);
                else
                    brightnessSeekbar.setEnabled(true);
            }
        });

        mProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.post(new Runnable() { public void run() { scrollView.fullScroll(View.FOCUS_UP); } });
            }
        });

        mProfileName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // bmb = (BoomMenuButton)findViewById(R.id.bmb);

        for (int i = 0; i < checkBoxIDS.length; i++) {
            checkBoxes[i] = findViewById(checkBoxIDS[i]);
        }

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            HamButton.Builder builder = new HamButton.Builder()
                    .normalImageRes(imgeRes[i])
                    .normalTextRes(stringIds[i])
                    .subNormalTextRes(stringIdTxts[i])
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            // When the boom-button corresponding this builder is clicked.
                            bmb.clearAnimation();
                            switch (index){
                                case 0 :
                                    Intent i = new Intent(EditProfileActivity.this, modeDialogActivity.class);
                                    i.putExtra("profile", profiles);
                                    startActivityForResult(i,2);
                                    overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
                                    break;
                                case 1 :
                                    Intent intent = new Intent(EditProfileActivity.this, VolumeActivity.class);
                                    intent.putExtra("profile", profiles);
                                    startActivityForResult(intent,3);
                                    overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
                                    break;
                                case 2 :
                                    Intent intent1 = new Intent(EditProfileActivity.this,ToDoList.class);
                                    intent1.putExtra("profile",profiles);
                                    startActivityForResult(intent1,1);
                                    overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
                                    break;
                            }
                        }
                    });

            bmb.addBuilder(builder);
        }

        for (int i = 0; i < checkBoxIDS.length; i++) {
            checkBoxes[i] = findViewById(checkBoxIDS[i]);
        }

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);

        if(getIntent().hasExtra("profile")){
            profiles = (Profiles)getIntent().getSerializableExtra("profile");
            if(profiles == null){
                Toast.makeText(this,"Cache for this application is removed.", Toast.LENGTH_SHORT).show();
                return;
            }
            brightness = profiles.getBrightness();
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
            imageView.setImageResource(profiles.getBackgroundImageId());
            if(profiles.getBitmap() != null)
                circularImageView.setImageBitmap(profiles.getBitmap());
            else
                circularImageView.setImageResource(profiles.getImageId());
        }
        brightnessSwitch.setChecked(profiles.getBrightnessEnabled());

        if(brightness != -1)
            brightnessSeekbar.setProgress(brightness);
        else
            brightness = 1;

        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(!Settings.System.canWrite(EditProfileActivity.this)){
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" +getPackageName()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        Toast.makeText(EditProfileActivity.this, "Enable manage settings to allow device to change brightness", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        try {
                            if(Settings.System.getInt(EditProfileActivity.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
                                Toast.makeText(EditProfileActivity.this,"Switch off auto brightness to see the results", Toast.LENGTH_SHORT).show();
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        if(!editProfile) {
            checkBoxes[day - 1].setChecked(true);
            daysEnable[day - 1] = true;
            checkBoxes[day - 1].setBackgroundResource(R.drawable.circle_enabled_resource);
        }

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "Long press on image, to add your own icon!", Toast.LENGTH_LONG).show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "Long press on image, to customize background!", Toast.LENGTH_LONG).show();
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

        circularImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (profiles != null) {
                   // Toast.makeText(EditProfileActivity.this, profiles.getProfile(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EditProfileActivity.this,ProfilePictureDialogActivity.class);
                    i.putExtra("profile",profiles);
                    i.putExtra("isIcon",true);
                    startActivityForResult(i,4);
                }
                return true;
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (profiles != null) {
                  //  Toast.makeText(EditProfileActivity.this, profiles.getProfile(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EditProfileActivity.this,ProfilePictureDialogActivity.class);
                    i.putExtra("isIcon",false);
                    i.putExtra("profile",profiles);
                    startActivityForResult(i,5);
                }
                return true;
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
                String profileName = mProfileName.getText().toString().trim();
                if(TextUtils.isEmpty(profileName)){
                    Toast.makeText(EditProfileActivity.this, "Enter profile name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if(profileName.length() > 20){
                        Toast.makeText(EditProfileActivity.this,"Profile name must be under 20 characters limit.", Toast.LENGTH_SHORT).show();
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
                        startAnimation(bmb);
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
                    //delFile(oldProfileName);
                    extractFromFile.delProfile(oldProfileName,EditProfileActivity.this);
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
                    new ActiveProfiles(EditProfileActivity.this).deleteValue(profiles.getProfile());
                }

                setTimeMethod(hourOfDay_start,minute_start,NotificationSilenceReciever.class,"com.example.shivangipandey.notificationoff.NotificationSilenceReciever",profiles.getPendingIntentSilenceId());
                profiles.setBrightness(brightness);
          //      makeListSerializable(profileNames);
                extractFromFile.serializeProfileNameList(profileNames, EditProfileActivity.this, "profileNames");
                // makeProfileObjSerializable(profileName,profiles);
                extractFromFile.serializeProfiles(profileName, profiles, EditProfileActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && data!= null && profiles!=null){
            profiles.setGeneralMode(data.getBooleanExtra("gen",false));
            profiles.setVibrationMode(data.getBooleanExtra("vib",false));
            profiles.setAlarmMode(data.getBooleanExtra("ala",false));
            profiles.setDoNotDisturbMode(data.getBooleanExtra("sil",false));
        }
        else if(requestCode == 3 && data!=null && profiles!=null){
            AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
            String name[] = {"ringer","media","alarm","system","voicecall","notification"};
            int streams[] ={AudioManager.STREAM_RING,AudioManager.STREAM_MUSIC,AudioManager.STREAM_ALARM,AudioManager.STREAM_SYSTEM,AudioManager.STREAM_VOICE_CALL,AudioManager.STREAM_NOTIFICATION};
            int volume[] = new int[name.length];
            for(int i =0;i<volume.length;i++){
                volume[i] = data.getIntExtra(name[i],audioManager.getStreamVolume(streams[i]));
            }
            profiles.setVolume(volume);
        }
        else if(requestCode == 4 && data!=null && profiles!=null){
            profiles.setImageId(data.getIntExtra("imageId",R.drawable.default_pic));

            int bitmapActive = data.getIntExtra("bitmap",-1);

            if(bitmapActive == 0) {
                Bitmap bitmap = getBitmapImg();
                profiles.setBitmap(bitmap);
                circularImageView.setImageBitmap(profiles.getBitmap());
            }
            else {
                profiles.setBitmap(null);
                circularImageView.setImageResource(profiles.getImageId());
            }
        }
        else if(requestCode == 5 && data!=null && profiles!=null){
            profiles.setImageBackgroundIdId(data.getIntExtra("imageId",R.drawable.blurry4));
            imageView.setImageResource(profiles.getBackgroundImageId());
        }
        else if(requestCode == 1 && data!=null && profiles!=null ){
            profiles.setIsToDoListEnabled(data.getBooleanArrayExtra("isChecked"));
            profiles.setWorks(data.getStringArrayExtra("works"));
        }
    }

    private void setTimeMethod(int hourOfDay, int minute, Class<?> cls, String action, int piID){
        session.setEnabled(true,profiles.getProfile());
        boolean flag = false;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
     //   Toast.makeText(this, "time set for "+hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        Calendar midnightCalender = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midnightCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        midnightCalender.set(Calendar.MINUTE,minute);


        if (session.getSDKVersion() >= Build.VERSION_CODES.M) {
            if(notificationManager.isNotificationPolicyAccessGranted())
                Toast.makeText(this,profiles.getProfile()+" Activated", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,profiles.getProfile()+" Activated", Toast.LENGTH_SHORT).show();

        if(midnightCalender.before(now) || (midnightCalender.getTimeInMillis()==now.getTimeInMillis())) {

            Calendar calEnd = Calendar.getInstance();
            calEnd.set(Calendar.HOUR_OF_DAY,profiles.getEndHour());
            calEnd.set(Calendar.MINUTE,profiles.getEndMin());

            midnightCalender.add(Calendar.DAY_OF_MONTH, 1);

            if(calEnd.after(now))
                flag = true;
            else
                Toast.makeText(this,"Your profile is going to activate from tomorrow.", Toast.LENGTH_SHORT).show();
        }
        if(!flag)
            checkForNotificationManager();

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,cls);
        intent.setAction(action);
        intent.putExtra("profiles",profiles.getProfile());
        PendingIntent midPI = PendingIntent.getBroadcast(this,piID,intent,PendingIntent.FLAG_CANCEL_CURRENT);
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

    private void checkForNotificationManager() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (session.getSDKVersion() >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent1 = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                    //startActivityForResult(intent1,0);
                } else {
                    session.putSDKVersion(Build.VERSION_CODES.KITKAT);
                    Toast.makeText(this, "Manually enable do not disturb in settings.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
            return;
    }
    private void startAnimation(BoomMenuButton bmb){
        Animation animation = new AlphaAnimation(1,0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        bmb.startAnimation(animation);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
