package fr.attestation_generator.ui.users;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class User implements Serializable {

    private Hashtable<String,Object> mDic;

    private String mName;
    private String mBirthday;
    private String mBirthplace;
    private String mAdresse;
    private String mCity;
    private String mDefaultMotif;

    private Boolean isChecked = false;
    public Boolean isCheckBoxVisible = false;
    public Boolean isAutoCreate = false;


    public User(Hashtable<String,Object> dic)
    {
        mDefaultMotif = (String) dic.get("Motif");
        mName = (String) dic.get("Name");
        mBirthday = (String) dic.get("Birthday");
        mBirthplace = (String) dic.get("Birthplace");
        mAdresse = (String) dic.get("Adresse");
        mCity = (String) dic.get("City");
        mDic = dic;
    }

    public User(String set)
    {
        String[] values = set.split(";");
        mDefaultMotif = values[0];
        mName = values[1];
        mBirthday = values[2];
        mBirthplace = values[3];
        mAdresse = values[4];
        mCity = values[5];
    }

    public String getDefaultMotif() {
        return mDefaultMotif;
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

    public Hashtable<String, Object> getDic(Boolean auto_create) {
        if (mDic == null || auto_create)
            makeDic(auto_create);
        return mDic;
    }

    private void makeDic(Boolean auto_create) {
        mDic = new Hashtable<>();
        mDic.put("Motif", mDefaultMotif);
        mDic.put("Name", mName);
        mDic.put("Birthday", mBirthday);
        mDic.put("Birthplace", mBirthplace);
        mDic.put("Adresse", mAdresse);
        mDic.put("City", mCity);
        Date now;
        if (auto_create)
        {
            Calendar cal = Calendar.getInstance();
            Log.i("My TAG", cal.getTime().toString());
            cal.add(Calendar.MINUTE, -30);
            Log.i("My TAG", cal.getTime().toString());
            now = cal.getTime();
        }
        else {
            now = new Date();
        }
        mDic.put("Date", new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault()).format(now));
        mDic.put("Time", new SimpleDateFormat("HH mm", Locale.getDefault()).format(now).replace(' ', 'h'));

    }

    public Boolean isChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public void setCheckBoxVisible(Boolean checkBoxVisible) {
        isCheckBoxVisible = checkBoxVisible;
    }

    public void setIsAutoCreate(Boolean AutoCreate)
    {
        this.isAutoCreate = AutoCreate;
    }

    public void setDefaultMotif(String defaultMotif) {
        mDefaultMotif = defaultMotif;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAdresse(String adresse) {
        mAdresse = adresse;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setBirthplace(String birthplace) {
        mBirthplace = birthplace;
    }

}
