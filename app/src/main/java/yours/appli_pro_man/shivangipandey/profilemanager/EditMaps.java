package yours.appli_pro_man.shivangipandey.profilemanager;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

public class EditMaps extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    String mapUrl;
    double lattitude = 0, longitude = 0;
    String name;
    float radius;
    int checkBoxIDS[] = {R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thrusday, R.id.friday, R.id.saturday};
    boolean daysEnable[] = new boolean[7];
    Profiles profiles;
    ProfileNames profileNames;
    String oldProfileName;
    boolean editProfile = false;
    ImageView imageView;
    BoomMenuButton bmb;
    CircularImageView circularImageView;
    int stringIds[] = {R.string.mode, R.string.volume, R.string.to_do_list};
    int stringIdTxts[] = {R.string.mode_Txt, R.string.volume_Txt, R.string.todolist_txt};
    int imgeRes[] = {R.drawable.ic_wb_sunny_black_24dp, R.drawable.ic_speaker_phone_black_24dp, R.drawable.ic_pets_black_24dp};
    Session session;
    ScrollView scrollView;
    SeekBar brightnessSeekbar;
    int brightness = -1;
    Switch brightnessSwitch;
    ExtractFromFile extractFromFile;
    Button delete;
    private EditText mProfileName;
    private CheckBox[] checkBoxes = new CheckBox[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_maps);

        extractFromFile = new ExtractFromFile();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        if (!isOnline()) {
            Toast.makeText(this, "Internet and GPS Connectivity Required", Toast.LENGTH_SHORT).show();
        }

        session = new Session(this);

        bmb = findViewById(R.id.action_bar_right_bmb);
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);

        Button mCancel = findViewById(R.id.cancelProfile);
        profileNames = extractFromFile.deserializedProfileNamesList(this, "profileNames_Maps");
        if (profileNames == null)
            profileNames = new ProfileNames();

        profiles = new Profiles();
        extractFromFile = new ExtractFromFile();
        imageView = findViewById(R.id.mapImage);
        mProfileName = findViewById(R.id.name);
        circularImageView = findViewById(R.id.circle_icon);
        scrollView = findViewById(R.id.scrollView);
        brightnessSeekbar = findViewById(R.id.brightness_seekbar);
        brightnessSeekbar.setMax(225);
        brightnessSwitch = findViewById(R.id.switchBrightness);
        delete = findViewById(R.id.deleteProfile);
        brightnessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profiles.setBrightnessEnabled(isChecked);
                if (!isChecked)
                    brightnessSeekbar.setEnabled(false);
                else
                    brightnessSeekbar.setEnabled(true);
            }
        });

        mProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProfile(profileNames, profiles);
            }
        });
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
                            switch (index) {
                                case 0:
                                    Intent i = new Intent(EditMaps.this, modeDialogActivity.class);
                                    i.putExtra("profile", profiles);
                                    startActivityForResult(i, 2);
                                    overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                                    break;
                                case 1:
                                    Intent intent = new Intent(EditMaps.this, VolumeActivity.class);
                                    intent.putExtra("profile", profiles);
                                    startActivityForResult(intent, 3);
                                    overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                                    break;
                                case 2:
                                    Intent intent1 = new Intent(EditMaps.this, ToDoList.class);
                                    intent1.putExtra("profile", profiles);
                                    startActivityForResult(intent1, 1);
                                    overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                                    break;
                            }
                        }
                    });

            bmb.addBuilder(builder);
        }

        for (int i = 0; i < checkBoxIDS.length; i++) {
            checkBoxes[i] = findViewById(checkBoxIDS[i]);
        }

        final EditText radiusEditText = findViewById(R.id.proximityRadius);

        if (getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
            if (!name.contains("Maps"))
                name += "_Maps";
            profiles = extractFromFile.deserializeProfile(name, this);
            if (profiles == null) {
                Toast.makeText(this, "Cache for this application is removed.", Toast.LENGTH_SHORT).show();
                return;
            }
            radiusEditText.setText(profiles.getRadius() + "");
            longitude = profiles.getLatLng().longitude;
            lattitude = profiles.getLatLng().latitude;
            brightness = profiles.getBrightness();
            mProfileName.setText(profiles.getProfile());
            oldProfileName = profiles.getProfile();
            for (int i = 0; i < checkBoxIDS.length; i++) {
                checkBoxes[i].setChecked(profiles.getDaysOfWeek(i));
                daysEnable[i] = profiles.getDaysOfWeek(i);
                if (checkBoxes[i].isChecked())
                    checkBoxes[i].setBackgroundResource(R.drawable.circle_enabled_resource);
            }
            editProfile = true;
            imageView.setImageResource(profiles.getBackgroundImageId());
            if (profiles.getBitmap() != null)
                circularImageView.setImageBitmap(profiles.getBitmap());
            else
                circularImageView.setImageResource(profiles.getImageId());
        }
        brightnessSwitch.setChecked(profiles.getBrightnessEnabled());

        if (brightness != -1)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(EditMaps.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        Toast.makeText(EditMaps.this, "Enable manage settings to allow device to change brightness", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        try {
                            if (Settings.System.getInt(EditMaps.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
                                Toast.makeText(EditMaps.this, "Switch off auto brightness to see the results", Toast.LENGTH_SHORT).show();
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);

        if (!editProfile) {
            checkBoxes[day - 1].setChecked(true);
            daysEnable[day - 1] = true;
            checkBoxes[day - 1].setBackgroundResource(R.drawable.circle_enabled_resource);
        }

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditMaps.this, "Long press on image, to add your own icon!", Toast.LENGTH_LONG).show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditMaps.this, "Long press on image, to customize background!", Toast.LENGTH_LONG).show();
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
                    // Toast.makeText(EditMaps.this, profiles.getProfile(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EditMaps.this, ProfilePictureDialogActivity.class);
                    i.putExtra("profile", profiles);
                    i.putExtra("isIcon", true);
                    startActivityForResult(i, 4);
                }
                return true;
            }
        });


        Button save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    radius = Float.parseFloat(radiusEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(EditMaps.this, "give proper radius in digits.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lattitude == 0 && longitude == 0) {
                    Toast.makeText(EditMaps.this, "chooose a position from map", Toast.LENGTH_SHORT).show();
                    return;
                }
                String profileName = mProfileName.getText().toString().trim();
                if (!profileName.contains("Maps"))
                    profileName += "_Maps";
                if (TextUtils.isEmpty(profileName)) {
                    Toast.makeText(EditMaps.this, "Enter profile name", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (profileName.length() > 20) {
                        Toast.makeText(EditMaps.this, "Profile name must be under 20 characters limit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (editProfile) {
                        if (profileNames != null && profileNames.containsProfileMap(profileName) && (!profileName.equals(oldProfileName))) {
                            Toast.makeText(EditMaps.this, profileName + " profile already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (profileNames != null && profileNames.containsProfileMap(profileName)) {
                            Toast.makeText(EditMaps.this, profileName + " profile already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (!profiles.getVibrationMode() && !profiles.getGeneralMode() && !profiles.getAlarmMode() && !profiles.getDoNotDisturbMode()) {
                        startAnimation(bmb);
                        Toast.makeText(EditMaps.this, "Choose a mode.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    profiles.resetProfileName(profileName);
                }
                for (int i = 0; i < daysEnable.length; i++) {
                    profiles.setDaysOfWeek(i, daysEnable[i]);
                }
                if (profileNames == null) {
                    profileNames = new ProfileNames();
                }
                if (editProfile) {
                    profileNames.renameProfileNameMaps(profileName, profiles.getProfile());
                    //delFile(oldProfileName);
                    extractFromFile.delProfile(oldProfileName, EditMaps.this);
                } else {
                    profileNames.SetProfileNamesMaps(profileName);
                    int ascii = calculatePendingIntentSilenceId(profileName);
                    profiles.setPendingIntentProximityRecieverId(ascii);
                }
                profiles.setRadius(radius);
                profiles.setLatLng(lattitude, longitude);
                checkForNotificationManager();
                profiles.setBrightness(brightness);
                name = profileName;
                setProximityAlert(lattitude, longitude, radius, profiles.getPendingIntentProximityReieverId());
            }
        });

        if (lattitude != 0 && longitude != 0)
            updateMapImage(lattitude, longitude);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                try {
                    Intent intent = intentBuilder.build(EditMaps.this);
                    startActivityForResult(intent, 6);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.right_out, R.anim.left_in);
            }
        });
    }

    private void returnMain() {
        Intent i = new Intent(this, MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "coarse location permission granted");
                    setProximityAlert(lattitude, longitude, radius, profiles.getPendingIntentProximityReieverId());
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    returnMain();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && data != null && profiles != null) {
            profiles.setGeneralMode(data.getBooleanExtra("gen", false));
            profiles.setVibrationMode(data.getBooleanExtra("vib", false));
            profiles.setAlarmMode(data.getBooleanExtra("ala", false));
            profiles.setDoNotDisturbMode(data.getBooleanExtra("sil", false));
        } else if (requestCode == 3 && data != null && profiles != null) {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            String name[] = {"ringer", "media", "alarm", "system", "voicecall", "notification"};
            int streams[] = {AudioManager.STREAM_RING, AudioManager.STREAM_MUSIC, AudioManager.STREAM_ALARM, AudioManager.STREAM_SYSTEM, AudioManager.STREAM_VOICE_CALL, AudioManager.STREAM_NOTIFICATION};
            int volume[] = new int[name.length];
            for (int i = 0; i < volume.length; i++) {
                volume[i] = data.getIntExtra(name[i], audioManager.getStreamVolume(streams[i]));
            }
            profiles.setVolume(volume);
        } else if (requestCode == 4 && data != null && profiles != null) {
            profiles.setImageId(data.getIntExtra("imageId", R.drawable.default_pic));

            int bitmapActive = data.getIntExtra("bitmap", -1);

            if (bitmapActive == 0) {
                Bitmap bitmap = getBitmapImg();
                profiles.setBitmap(bitmap);
                circularImageView.setImageBitmap(profiles.getBitmap());
            } else {
                profiles.setBitmap(null);
                circularImageView.setImageResource(profiles.getImageId());
            }
        } else if (requestCode == 5 && data != null && profiles != null) {
            profiles.setImageBackgroundIdId(data.getIntExtra("imageId", R.drawable.blurry4));
            imageView.setImageResource(profiles.getBackgroundImageId());
        }
        if (requestCode == 6 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            LatLng latLng = place.getLatLng();
            updateMapImage(lattitude = latLng.latitude, longitude = latLng.longitude);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MapsActivity.class));
    }

    private void setProximityAlert(double longitude, double lattitude, float radius, int requestCode) {
        String proximityIntentAction = "yours.appli_wea_rep.proximityalertmodule.ProximityAlert";

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        IntentFilter intentFilter = new IntentFilter(proximityIntentAction);
        registerReceiver(new ProximityAlertReciever(), intentFilter);
        Intent intent = new Intent(proximityIntentAction);
        intent.putExtra("profile", profiles.getProfile());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Permission");
            builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
        } else {
            extractFromFile.serializeProfiles(name, profiles, EditMaps.this);
            extractFromFile.serializeProfileNameList(profileNames, EditMaps.this, "profileNames_Maps");
            locationManager.addProximityAlert(lattitude, longitude, radius, -1, pendingIntent);
            returnMain();
        }

    }

    private void updateMapImage(double lattitude, double longitude) {
        mapUrl = "https://maps.googleapis.com/maps/api/staticmap?size=400x250" +
                "&zoom=12&center=" + lattitude + "," + longitude +
                "&format=png&style=feature:road.highway%7Celement:geometry" +
                "%7Cvisibility:simplified%7Ccolor:0xc280e9&style=feature:transit.line%7Cvisibility:simplified%7" +
                "Ccolor:0xbababa&style=feature:road.highway%7Celement:labels.text.stroke%7Cvisibility:on%" +
                "7Ccolor:0xb06eba&style=feature:road.highway%7Celement:labels.text.fill%7Cvisibility:on%7Ccolor:0xffffff&" +
                "key=AIzaSyDHdNob3dtGwowGXP3nsNWfAb_cyGqunt4";

        Picasso.with(this).load(mapUrl).into(imageView);
    }

    private int calculatePendingIntentSilenceId(String name) {
        name = name + "_Maps";
        int ascii = 0;
        for (int i = 0; i < name.length(); i++) {
            ascii += (int) name.charAt(i);
        }
        return ascii;
    }

    private Bitmap getBitmapImg() {
        File file = new File(getCacheDir(), "bitmap_file");
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
    }

    private void startAnimation(BoomMenuButton bmb) {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        bmb.startAnimation(animation);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void deleteProfile(final ProfileNames profileNames, final Profiles profiles) {

        if (!profileNames.containsProfileMap(profiles.getProfile())) {
            Toast.makeText(this, "Profile is not created yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String proximityIntentAction = "yours.appli_wea_rep.proximityalertmodule.ProximityAlert";

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditMaps.this);
        builder.setTitle("Delete profile");

        builder.setIcon(R.drawable.ic_border_color_white_18dp)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentFilter intentFilter = new IntentFilter(proximityIntentAction);
                        registerReceiver(new ProximityAlertReciever(), intentFilter);
                        Intent intent = new Intent(proximityIntentAction);
                        intent.putExtra("profile", profiles.getProfile());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), profiles.getPendingIntentProximityReieverId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        if (pendingIntent != null)
                            pendingIntent.cancel();
                        session.setEnabled(false, profiles.getProfile());
                        session.setProfileActive(false, profiles.getProfile());
                        new ActiveProfiles(EditMaps.this).deleteValue(profiles.getProfile());
                        delete(profiles.getProfile(), profileNames);
                    }
                });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void delete(String name, ProfileNames profileNames) {
        profileNames.removeProfileNameMap(name);
        extractFromFile.delProfile(name, EditMaps.this);
        extractFromFile.serializeProfileNameList(profileNames, EditMaps.this, "profileNames_Maps");
        Intent intent = new Intent(EditMaps.this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
