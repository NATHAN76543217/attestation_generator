package com.example.attestation_generator.ui.users;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

public class User implements Serializable {

    private String mName;
    private String mBirthday;
    private String mBirthplace;
    private String mAdresse;
    private String mCity;
    private Boolean isChecked = false;
    public Boolean isCheckBoxVisible = false;

    public User(Hashtable dic)
    {
        mName = (String) dic.get("Name");
        mBirthday = (String) dic.get("Birthday");
        mBirthplace = (String) dic.get("Birthplace");
        mAdresse = (String) dic.get("Adresse");
        mCity = (String) dic.get("City");
    }

    public User(String set)
    {
        String values[] = set.split(";");
        mName = values[0];
        mBirthday = values[1];
        mBirthplace = values[2];
        mAdresse = values[3];
        mCity = values[4];
    }

    public String getName() {
        return mName;
    }
    public String getBirthday() {
        return mBirthday;
    }
    public String getBirthplace() {
        return mBirthplace;
    }
    public String getAdresse() {
        return mAdresse;
    }
    public String getCity() {
        return mCity;
    }
    public Boolean isChecked() {
        return isChecked;
    }
    public Boolean isCheckBoxVisible()
    {
        return  isCheckBoxVisible;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public void setCheckBoxVisible(Boolean checkBoxVisible) {
        isCheckBoxVisible = checkBoxVisible;
    }
}
