package com.example.attestation_generator.ui.parameters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.attestation_generator.R;

import java.util.Hashtable;
import java.util.List;

public class parameters extends AppCompatActivity {

    List<Param> mParamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        getSupportActionBar().setTitle(getString(R.string.parameters));
        Load_parameters();
    }

    private void Load_parameters() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parameters.this);
        SharedPreferences.Editor edit = preferences.edit();
        mParamList.add(new Param("auto_create", preferences, "bool"));
    }

    private class Param {
        String mName;
        SharedPreferences mPrefs;
        String mType;
        Object Value;

        public Param (String name, SharedPreferences pref, String type)
        {
            this.mName = name;
            this.mPrefs = pref;
            this.mType = type;
            loadParam();

        }

        private void loadParam()
        {
            switch (mType)
            {
                case "int" :
                    Value = mPrefs.getInt("param_" + mName, 0);
                    break;
                case "string":
                    Value = mPrefs.getString("param_" + mName, "");
                    break;
                case "bool":
                    Value = mPrefs.getBoolean("param_" + mName, false);
                    break;
                    default:
                        Log.e("My TAG", "Param Type: No match found");
            }
        }
        private void saveParam()
        {
            switch (mType)
            {
                case "int" :
                    Value = mPrefs.edit().putInt("param_" + mName, (int) Value);
                    break;
                case "string":
                    Value = mPrefs.edit().putString("param_" + mName, (String) Value);
                    break;
                case "bool":
                    Value = mPrefs.edit().putBoolean("param_" + mName, (Boolean) Value);
                    break;
                default:
                    Log.e("My TAG", "Param Type: No match found");
            }        }
    }
}