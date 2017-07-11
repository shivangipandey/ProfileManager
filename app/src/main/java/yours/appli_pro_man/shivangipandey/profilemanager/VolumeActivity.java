package yours.appli_pro_man.shivangipandey.profilemanager;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import yours.appli_pro_man.shivangipandey.profilemanager.R;

public class VolumeActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    int seekBarId[]={R.id.ringer_seekBar,R.id.media_seekBar, R.id.alarm_seekBar,R.id.system_seekBar,R.id.voiceCall_seekBar,R.id.notification_seekBar};
    SeekBar seekBar[] = new SeekBar[seekBarId.length];
    int volume[] = {0,0,0,0,0,0};
    int streams[] ={AudioManager.STREAM_RING,AudioManager.STREAM_MUSIC,AudioManager.STREAM_ALARM,AudioManager.STREAM_SYSTEM,AudioManager.STREAM_VOICE_CALL,AudioManager.STREAM_NOTIFICATION};
    FloatingActionButton floatingActionButton;
    AudioManager audioManager;
    Profiles profiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        profiles = (Profiles)getIntent().getSerializableExtra("profile");
       // Toast.makeText(getApplicationContext(), profiles.getProfile(), Toast.LENGTH_SHORT).show();

        if(profiles == null){
            Toast.makeText(getApplicationContext(),"Restart the application.",Toast.LENGTH_LONG).show();
            return;
        }

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        for(int i=0;i<seekBar.length;i++) {
            seekBar[i] = (SeekBar) findViewById(seekBarId[i]);
            seekBar[i].setMax(audioManager.getStreamMaxVolume(streams[i]));
        }

        if(profiles.getvolumes() != null){
            volume = profiles.getvolumes();
            for(int i =0;i<volume.length;i++)
                seekBar[i].setProgress(volume[i]);
        }
        else {
            volume[4] = audioManager.getStreamMaxVolume(streams[4]);
            seekBar[4].setProgress(volume[4]);
        }

        for (int i=0;i<seekBar.length;i++){
            seekBar[i].setOnSeekBarChangeListener(this);
        }
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_volume);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name[] = {"ringer","media","alarm","system","voicecall","notification"};
                Intent intent = new Intent();
                for(int i=0;i<name.length;i++){
                    intent.putExtra(name[i],volume[i]);
                }
                setResult(3,intent);
                finish();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar1, int progress, boolean fromUser) {
            if (seekBar[0] == seekBar1)
                volume[0] = progress;
            else if (seekBar[1] == seekBar1)
                volume[1] = progress;
            else if (seekBar[2] == seekBar1)
                volume[2] = progress;
            else if (seekBar[3] == seekBar1)
                volume[3] = progress;
            else if (seekBar[4] == seekBar1)
                volume[4] = progress;
            else if (seekBar[5] == seekBar1)
                volume[5] = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
