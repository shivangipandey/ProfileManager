package com.example.shivangipandey.profilemanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by shivangi.pandey on 6/1/2017.
 */

@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {



    int ID;
    Context context;
    Button textView;
    Profiles profiles;

    TimePickerFragment(int ID,Context context,Button textView,Profiles profiles){
        super();
        this.ID = ID;
        this.context = context;
        this.textView = textView;
        this.profiles = profiles;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String hd = hourOfDay+"", min= minute+"";
        if(hourOfDay >=0 && hourOfDay <=9)
            hd = "0"+hd;
        if(minute>=0 &&minute<=9)
            min = "0"+min;
        switch (ID){
            case R.id.start_time :
                textView.setText(hd+":"+min);
                profiles.setStartTime(hourOfDay,minute);
                break;

            case R.id.endTime :
                textView.setText(hd+":"+min);
                profiles.setEndTime(hourOfDay,minute);
                break;
        }

    }
}
