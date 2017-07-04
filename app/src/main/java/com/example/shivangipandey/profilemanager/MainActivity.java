package com.example.shivangipandey.profilemanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{



    private FloatingActionButton floatingActionButton;
    ProfileNames profileNames = null;
    ListView listView;
    TextView empty;
    ExtractFromFile extractFromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractFromFile = new ExtractFromFile();
        midnightAlarmStart();

        listView = (ListView) findViewById(R.id.profileListView);
        final ArrayList<Profiles> profilesArrayList = new ArrayList<>();
        empty = (TextView)findViewById(R.id.empty);
        listView.setEmptyView(empty);

        //profileNames = getSerializedList();
        profileNames = extractFromFile.deserializedProfileNamesList(MainActivity.this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                intent.putExtra("profileNames",profileNames);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
            }
        });

        if (profileNames != null)
            createListView(profilesArrayList);

        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteProfile(profileNames,profilesArrayList.get(position));
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,EditProfileActivity.class);
                i.putExtra("profile",profilesArrayList.get(position));
                i.putExtra("profileNames",profileNames);
                i.putExtra("position",position);
                startActivity(i);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });


    }

 /*   private Profiles getSerializedProfile(String profileName){
        ObjectInputStream in = null;
        Profiles p = null;
        try {
           File file = new File(getFilesDir(),profileName+".ser");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (Profiles) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p;
    }
    private ProfileNames getSerializedList(){
        ObjectInputStream in = null;
        ProfileNames p = null;
        try {
            File file = new File(getFilesDir(),"profileNames");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (ProfileNames) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return p;
    }*/

    private void deleteProfile(final ProfileNames profileNames,final Profiles profiles){
        AlertDialog.Builder builder = new  AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete profile");

        builder.setIcon(R.drawable.ic_border_color_white_18dp)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelIntents(profiles);
                        new Session(MainActivity.this).setEnabled(false,profiles.getProfile());
                        new Session(MainActivity.this).setProfileActive(false,profiles.getProfile());
                        new ActiveProfiles(MainActivity.this).deleteValue(profiles.getProfile());
                        delete(profiles.getProfile(),profileNames);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createListView(ArrayList<Profiles> profilesArrayList){
        final ArrayList<String> arrayList = profileNames.getProfileNameArrayList();

        if (!arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
               // profilesArrayList.add(getSerializedProfile(arrayList.get(i)));
                profilesArrayList.add(extractFromFile.deserializeProfile(arrayList.get(i),MainActivity.this));
            }
            customAdapter cusAdap = new customAdapter(MainActivity.this, R.layout.profile_listview_item, profilesArrayList);
            listView.setAdapter(cusAdap);
        }
    }
    private void delete(String name,ProfileNames profileNames){
        profileNames.removeProfileName(name);
            extractFromFile.delProfile(name,MainActivity.this);
           // File profileFile = new File(getFilesDir().getAbsolutePath(),name+"ser");
           // profileFile.delete();
            extractFromFile.serializeProfileNameList(profileNames,MainActivity.this);
          /*  File outputFile = new File(getFilesDir(),"profileNames");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
            out.writeObject(profileNames);
            out.close();*/
            Intent intent = new Intent(MainActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }
    private void cancelIntents(Profiles profiles){

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(getApplicationContext(),UnsilenceNotifications.class);
        i.putExtra("profiles",profiles.getProfile());
        Intent i2 = new Intent(getApplicationContext(),NotificationSilenceReciever.class);
        i2.putExtra("profiles",profiles.getProfile());
        i.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
        i2.setAction("com.example.shivangipandey.notificationoff.NotificationSilenceReciever");
        PendingIntent midPI = PendingIntent.getBroadcast(getApplicationContext(),profiles.getPendingIntentUnSilenceId(),i,PendingIntent.FLAG_NO_CREATE);
        PendingIntent midPI2 = PendingIntent.getBroadcast(getApplicationContext(),profiles.getPendingIntentSilenceId(),i2,PendingIntent.FLAG_NO_CREATE);
        if(midPI != null){
            am.cancel(midPI);
            midPI.cancel();
            String name = profiles.getProfile();
            if(new Session(MainActivity.this).isProfileActive(name)) {
                sendBroadcast(i);
            }
        }
        if(midPI2 != null){
            am.cancel(midPI2);
            midPI2.cancel();
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(profiles.getPendingIntentSilenceId());
        }
        Toast.makeText(MainActivity.this,"Pending Intents disabled", Toast.LENGTH_SHORT).show();

    }

    private void midnightAlarmStart(){
        Intent i = new Intent(this,BootUpReciever.class);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,i,PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent == null)
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        if(calendar.before(cal))
            calendar.add(Calendar.DAY_OF_MONTH,1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}
