package com.example.shivangipandey.profilemanager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shivangi.pandey on 6/19/2017.
 */

public class ProfileNames implements Serializable {

     static final long serialVersionUID =  615299648607350905L;
     ArrayList<String> arrayList;

    public ProfileNames(){
        arrayList = new ArrayList<>();
    }
    public void SetProfileNames(String name){
        arrayList.add(name);
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
    public boolean containsProfile(String name){
        return arrayList.contains(name);
    }
    public String getItem(int index){
        return arrayList.get(index);
    }

}
