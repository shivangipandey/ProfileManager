package yours.appli_pro_man.shivangipandey.profilemanager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shivangi.pandey on 6/19/2017.
 */

public class ProfileNames implements Serializable {

     static final long serialVersionUID =  615299648607350905L;
     ArrayList<String> arrayList;
    ArrayList<String> arrayListMap;

    public ProfileNames(){
        arrayList = new ArrayList<>();
        arrayListMap = new ArrayList<>();
    }
    public void SetProfileNames(String name){
        arrayList.add(name);
    }

    public void SetProfileNamesMaps(String name) {
        arrayListMap.add(name);
    }
    public ArrayList<String> getProfileNameArrayList(){
        return arrayList;
    }
    public void removeProfileName(String name){
        arrayList.remove(name);
    }
    public void renameProfileName(String newName,int index){
        arrayList.set(index,newName);
    }

    public void renameProfileNameMaps(String newName, String oldName) {
        arrayListMap.set(arrayListMap.indexOf(oldName), newName);
    }
    public boolean containsProfile(String name){
        return arrayList.contains(name);
    }

    public boolean containsProfileMap(String name) {
        return arrayListMap.contains(name);
    }

    public ArrayList<String> getProfileArrayListMap() {
        return arrayListMap;
    }

    public void removeProfileNameMap(String name) {
        arrayListMap.remove(name);
    }
}
