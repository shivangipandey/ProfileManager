package yours.appli_pro_man.shivangipandey.profilemanager;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.List;

public class IntroActivity extends AppIntro {
    Fragment permissionPage;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        permissionPage = AppIntroFragment.newInstance("Permissions","This app requires DO NOT DISTURB permission to keep" +
                " your profiles interactive.", R.drawable.dndper, Color.parseColor("#f1c40f"));

        addSlide(AppIntroFragment.newInstance("Customize Profile", "Customize your own profile, that suits your needs.", R.drawable.screen1, Color.parseColor("#3498db")));
        addSlide(AppIntroFragment.newInstance("Pin Point location radius", "Add ", R.drawable.intro_map, Color.parseColor("#1abc9c")));
        addSlide(AppIntroFragment.newInstance("Reminder", "Don't miss out on your daily responsibilities, add reminder.", R.drawable.screen2, Color.parseColor("#34495e")));
        addSlide(AppIntroFragment.newInstance("Mode","Choose your own level of volume for every profile, afterall" +
                " everyone has different needs.",R.drawable.screen3,getResources().getColor(R.color.screen3)));
        addSlide(AppIntroFragment.newInstance("Days","Choose your separate profile activation days depending on your" +
                " schedule.", R.drawable.screen4, Color.parseColor("#2ecc71")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(permissionPage);
        }
        else{
            new Session(this).putSDKVersion(Build.VERSION.SDK_INT);
        }
        addSlide(AppIntroFragment.newInstance("Done","Great! Now let the app" +
                " help you make your day easier one at a time.",R.drawable.screen5,getResources().getColor(R.color.screen5)));
        setFadeAnimation();
        showSkipButton(false);
    }
    @Override
    public void onSkipPressed(Fragment currentFragment) {

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        new Session(this).isLaunched(false);
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        List<Fragment> slides = getSlides();
        final Session session = new Session(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if(!notificationManager.isNotificationPolicyAccessGranted()) {
                if (slides.indexOf(newFragment) == 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle("Do not disturb access").setMessage(String.format("Do Not Disturb requires to be enabled in %s.%n", getString(R.string.app_name)))
                            .setPositiveButton("go to settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent1 = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    if (intent1.resolveActivity(IntroActivity.this.getPackageManager()) != null) {
                                        session.putSDKVersion(Build.VERSION.SDK_INT);
                                        startActivity(intent1);
                                    } else {
                                        session.putSDKVersion(Build.VERSION_CODES.KITKAT);
                                        Toast.makeText(IntroActivity.this, "Manually allow do not disturb access for Profile Manager in settings.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).create().show();
                }
            }
            else
                session.putSDKVersion(Build.VERSION.SDK_INT);
        }
    }
}
