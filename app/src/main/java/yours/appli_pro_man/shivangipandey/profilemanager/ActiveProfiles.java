package yours.appli_pro_man.shivangipandey.profilemanager;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shivangi.pandey on 6/22/2017.
 */

public class ActiveProfiles implements Serializable{

    Context context;
    ArrayList<String> activeProfiles;

    public ActiveProfiles(Context context){
        this.context = context;
    }

    public void addValue(String profileName){
        activeProfiles = readFile();
        if(activeProfiles == null)
            activeProfiles = new ArrayList<>();
        if(!activeProfiles.contains(profileName))
            activeProfiles.add(profileName);
        createFile(activeProfiles);
    }
    public void deleteValue(String profileName){
        activeProfiles = readFile();
        if(activeProfiles != null){
            if (activeProfiles.size() != 0) {
                activeProfiles.remove(profileName);
                createFile(activeProfiles);
            }
        }
    }
    public String getPreviousProfile(String profileName){
        int index = -1;
        activeProfiles = readFile();
        if(activeProfiles != null) {
            for (int i = 0; i < activeProfiles.size(); i++) {
                if (activeProfiles.get(i).equals(profileName)) {
                    index = i - 1;
                    break;
                }
            }
            if (index >=0)
                return activeProfiles.get(index);
        }
        return null;
    }
    public boolean hasNextValue(String profileName){
        activeProfiles = readFile();
        if(activeProfiles != null) {
            int index = activeProfiles.size();
            for (int i = 0; i < activeProfiles.size(); i++) {
                if (activeProfiles.get(i).equals(profileName)) {
                    index = i + 1;
                    break;
                }
            }
            if (index < activeProfiles.size())
                return true;
        }
        return false;
    }

    public ArrayList<String> readFile(){
        ObjectInputStream in = null;
        ArrayList<String> p = null;
        try {
            File file = new File(context.getFilesDir(),"activeProfiles");
            in = new ObjectInputStream(new FileInputStream(file));
            p = (ArrayList<String>) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return p;
    }

    public void createFile(ArrayList<String> activeProfiles){
        try {
            File outputFile = new File(context.getFilesDir(),"activeProfiles");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
            out.writeObject(activeProfiles);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
