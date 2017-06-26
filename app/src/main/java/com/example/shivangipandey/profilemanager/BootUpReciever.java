package com.example.shivangipandey.profilemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Shiavngi Pandey on 25-06-2017.
 */

public class BootUpReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "HHHIIIIIIIIIIII", Toast.LENGTH_SHORT).show();
    }
}
