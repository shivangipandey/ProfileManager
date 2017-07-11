package yours.appli_pro_man.shivangipandey.profilemanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Shiavngi Pandey on 09-07-2017.
 */

public class OnCheckService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent i = new Intent(this, BootUpReciever.class);
        Log.e("started","                 INSERVICE");
        sendBroadcast(i);
        stopSelf();
    }
}
