package yours.appli_pro_man.shivangipandey.profilemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Shiavngi Pandey on 26-08-2017.
 */

public class ProximityAlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Startedd    ddllk;dlf;dlfffffffffffffff  fefededed", Toast.LENGTH_SHORT).show();

        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        String name = intent.getStringExtra("profile");
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Proximity sensors not working", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(context, UnsilenceNotifications.class);
        i.putExtra("profiles", name);
        Intent i2 = new Intent(context, NotificationSilenceReciever.class);
        i2.putExtra("profiles", name);
        i.setAction("com.example.shivangipandey.notificationoff.UnsilenceNotificationReciever");
        i2.setAction("com.example.shivangipandey.notificationoff.NotificationSilenceReciever");

        if (entering)
            context.sendBroadcast(i2);
        else
            context.sendBroadcast(i);
    }
}
