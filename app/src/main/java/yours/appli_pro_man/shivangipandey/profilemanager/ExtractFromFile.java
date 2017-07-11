package yours.appli_pro_man.shivangipandey.profilemanager;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Shiavngi Pandey on 29-06-2017.
 */

public class ExtractFromFile {

    public Profiles deserializeProfile(String profileName, Context context){
        ObjectInputStream in = null;
        Profiles p = null;
        try {
            File file = new File(context.getFilesDir(),profileName+".ser");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (Profiles) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p;
    }

    public ProfileNames deserializedProfileNamesList(Context context){
        ObjectInputStream in = null;
        ProfileNames p = null;
        try {
            File file = new File(context.getFilesDir(),"profileNames");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (ProfileNames) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return p;
    }
    public void serializeProfileNameList(ProfileNames profileNames,Context context){

        try {
            File outputFile = new File(context.getFilesDir(),"profileNames");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
            out.writeObject(profileNames);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void serializeProfiles(String proName,Profiles profile,Context context){
        try {
            File outputFile = new File(context.getFilesDir(),proName+".ser");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
            out.writeObject(profile);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void delProfile(String oldName,Context context){
        File profileFile = new File(context.getFilesDir().getAbsolutePath(),oldName+"ser");
        profileFile.delete();
    }
}
