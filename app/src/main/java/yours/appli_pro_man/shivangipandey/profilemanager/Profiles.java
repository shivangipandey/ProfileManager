package yours.appli_pro_man.shivangipandey.profilemanager;

import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import yours.appli_pro_man.shivangipandey.profilemanager.R;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by shivangi.pandey on 6/23/2017.
 */

public class Profiles implements Serializable{

    static final long serialVersionUID =-9079599650181085918L;

    private String name;
    private int startHour = 0,startMin = 0;
    private int endHour = 0,endMin = 0;
    private int currentMode = NotificationManager.INTERRUPTION_FILTER_ALL;
    private boolean generalMode=false,vibrationMode=false,alarmMode=false,doNotDisturbMode=false;
    private boolean days[] = new boolean[7];
    private int pendingIntentSilenceId = -1;
    private int pendingIntentUnSilenceId = -2;
    private int imageId = R.drawable.study;
    private int progress[] = null;
    private byte[] bitmpaArray = null;
    private int imageBackgroundId = R.drawable.blurry4;
    private boolean[] isToDoListEnabled = {false,false,false,false,false};
    private String[] works = new String[5];
    public Profiles(){
    }

    public void resetProfileName(String profile){
        name = profile;
    }
    public void setStartTime(int startHour,int startMin){
        this.startHour = startHour;
        this.startMin = startMin;
    }
    public void setEndTime(int endHour,int endMin){
        this.endHour = endHour;
        this.endMin = endMin;
    }

    public void setPendingSilenceIntent(int pendingIntentSilenceId){
        this.pendingIntentSilenceId = pendingIntentSilenceId;
    }

    public void setPendingIntentUnSilenceId(int pendingIntenUnSilenceId){
        this.pendingIntentUnSilenceId = pendingIntenUnSilenceId;
    }
    public void setCurruntMode(int curruntMode){
        this.currentMode = curruntMode;
    }
    public void setGeneralMode(boolean generalMode){
        this.generalMode = generalMode;
    }
    public void setVibrationMode(boolean vibrationMode){
        this.vibrationMode = vibrationMode;
    }
    public void setDoNotDisturbMode(boolean doNotDisturbMode){
        this.doNotDisturbMode = doNotDisturbMode;
    }
    public void setAlarmMode(boolean alarmMode){
        this.alarmMode = alarmMode;
    }

    public void setDaysOfWeek(int index,boolean isEnable){
        days[index] = isEnable;
    }
    public void setImageId(int imageId){
        this.imageId = imageId;
    }
    public void setImageBackgroundIdId(int imageBackgroundId){
        this.imageBackgroundId = imageBackgroundId;
    }
    public void setVolume(int[] volume){
        progress = volume;
    }
    public void setBitmap(Bitmap bitmap){
        if(bitmap != null) {
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
            bitmpaArray = blob.toByteArray();
        }
        else bitmpaArray = null;
    }
    public void setIsToDoListEnabled(boolean[] isToDoListEnabled){
        this.isToDoListEnabled = isToDoListEnabled;
    }
    public void setWorks(String[] works){
        this.works = works;
    }

    public String getProfile(){
        return name;
    }
    public int getStartHour(){
        return startHour;
    }
    public int getStartMin(){
        return startMin;
    }
    public int getEndMin(){
        return endMin;
    }
    public int getEndHour(){
        return endHour;
    }
    public int getCurrentMode(){
        return currentMode;
    }
    public boolean getGeneralMode(){
        return generalMode;
    }
    public boolean getVibrationMode(){
        return vibrationMode;
    }
    public boolean getDoNotDisturbMode(){
        return doNotDisturbMode;
    }
    public boolean getAlarmMode(){
        return alarmMode;
    }
    public int getPendingIntentSilenceId(){
        return pendingIntentSilenceId;
    }
    public int getPendingIntentUnSilenceId(){
        return pendingIntentUnSilenceId;
    }
    public boolean getDaysOfWeek(int index){
        return days[index];
    }
    public int getImageId(){
        return imageId;
    }
    public int getBackgroundImageId(){
        return imageBackgroundId;
    }
    public int[] getvolumes(){
        return progress;
    }
    public Bitmap getBitmap(){
        if(bitmpaArray != null)
            return BitmapFactory.decodeByteArray(bitmpaArray,0,bitmpaArray.length);
        else
            return null;
    }
    public boolean[] getToDoListEnabled(){
        return isToDoListEnabled;
    }
    public String[] getWorks(){
        return works;
    }
}
