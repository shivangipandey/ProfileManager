package com.example.shivangipandey.profilemanager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class modeDialogActivity extends AppCompatActivity {

    CheckBox checkBox[] = new CheckBox[4];
    Profiles profiles;
    Boolean general,vibration,silent,alarm;
    Button okay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modes_item_dialog);
        setFinishOnTouchOutside(true);
        int id[] = {R.id.ringer_checkbox, R.id.vibration_checkbox, R.id.alarm_checkbox, R.id.doNotDisturb_checkbox};
        okay = (Button)findViewById(R.id.okButton);

        for (int i = 0; i < checkBox.length; i++)
            checkBox[i] = (CheckBox) findViewById(id[i]);

        profiles = (Profiles)getIntent().getSerializableExtra("profile");
   //     Toast.makeText(this, profiles.getProfile(), Toast.LENGTH_SHORT).show();

        if(profiles == null){
            Toast.makeText(getApplicationContext(),"Restart the application",Toast.LENGTH_LONG).show();
            return;
        }

        checkBox[0].setChecked(profiles.getGeneralMode());
        general = profiles.getGeneralMode();
        checkBox[1].setChecked(profiles.getVibrationMode());
        vibration = profiles.getVibrationMode();
        checkBox[2].setChecked(profiles.getAlarmMode());
        alarm = profiles.getAlarmMode();
        checkBox[3].setChecked(profiles.getDoNotDisturbMode());
        silent = profiles.getDoNotDisturbMode();

        if(checkBox[0].isChecked()){
            checkBox[1].setEnabled(false);
            checkBox[1].setChecked(false);
        }

        if(checkBox[3].isChecked()){
            checkBox[0].setEnabled(false);
            checkBox[1].setEnabled(false);
            checkBox[2].setEnabled(false);
            checkBox[0].setChecked(false);
            checkBox[1].setChecked(false);
            checkBox[2].setChecked(false);
        }

        for (int i = 0; i < checkBox.length; i++) {

            checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView == checkBox[0]) {
                        if (isChecked) {
                            general = true;
                            vibration = false;
                            checkBox[1].setChecked(false);
                            checkBox[1].setEnabled(false);
                        }
                        else {
                            general = false;
                            checkBox[1].setEnabled(true);
                            }
                        }
                    else if (buttonView == checkBox[1]) {
                        if (isChecked)
                            vibration = true;
                        else
                            vibration = false;
                        }
                    else if (buttonView == checkBox[2]) {
                        if (isChecked)
                            alarm = true;
                        else
                            alarm = false;
                        }
                    else if (buttonView == checkBox[3]) {
                        if (isChecked) {
                            checkBox[0].setChecked(false);
                            checkBox[1].setChecked(false);
                            checkBox[2].setChecked(false);
                            general = false;
                            vibration = false;
                            alarm = false;
                            silent = true;
                            checkBox[0].setEnabled(false);
                            checkBox[1].setEnabled(false);
                            checkBox[2].setEnabled(false);
                        } else {
                            checkBox[0].setEnabled(true);
                            checkBox[1].setEnabled(true);
                            checkBox[2].setEnabled(true);
                            silent = false;
                        }
                    }
                }
            });
        }
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox[0].isChecked()
                        && !checkBox[1].isChecked()
                        && !checkBox[2].isChecked()
                        && !checkBox[3].isChecked()){
                    Toast.makeText(getApplicationContext(),"Choose atleast 1 mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent();
                i.putExtra("gen",general);
                i.putExtra("vib",vibration);
                i.putExtra("sil",silent);
                i.putExtra("ala",alarm);
                setResult(2,i);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

