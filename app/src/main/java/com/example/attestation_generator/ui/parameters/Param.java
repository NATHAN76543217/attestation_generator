package com.example.attestation_generator.ui.parameters;

import android.content.SharedPreferences;
import android.util.Log;

public class Param {
        public final static String INT = "int";
        public final static String STRING = "string";
        public final static String BOOLEAN = "bool";
        SharedPreferences mPrefs;
        SharedPreferences.Editor mEditor;
        String mName;
        String mType;
        String mDescription;
        Object Value;

        public Param (String name, String description, SharedPreferences pref, String type)
        {
            this.mName = name;
            this.mPrefs = pref;
            this.mType = type;
            this.mDescription = description;
            this.mEditor = pref.edit();
            loadParams();

        }

        private void loadParams()
        {
            switch (mType)
            {
                case INT :
                    Value = Integer.valueOf(mPrefs.getInt("param_" + mName, 0));
                    break;
                case STRING:
                    Value = String.valueOf(mPrefs.getString("param_" + mName, "null"));
                    break;
                case BOOLEAN:
                    Value = mPrefs.getBoolean("param_" + mName, false);
                    break;
                    default:
                        Log.e("My TAG", "Param Type: No match found");
            }
            Log.i("My TAG", "load " + mName + ": " + Value);
        }
        public void saveParam()
        {
            switch (mType)
            {
                case INT :
                    mEditor.putInt("param_" + mName, (int) Value);
                    break;
                case STRING:
                    mEditor.putString("param_" + mName, (String) Value);
                    break;
                case BOOLEAN:
                    mEditor.putBoolean("param_" + mName, (Boolean) Value);
                    break;
                default:
                    Log.e("My TAG", "Param Type: No match found");
            }
            mEditor.apply();
        }
        public void setValue(Object value)
        {
            this.Value = value;
        }
    static public Object loadParam(SharedPreferences prefs, String name, String type)
    {
        switch (type)
        {
            case INT :
                return prefs.getInt("param_" + name, 0);
            case STRING:
                return prefs.getString("param_" + name, "null");
            case BOOLEAN:
                return prefs.getBoolean("param_" + name, false);
            default:
                Log.e("My TAG", "Param Type: No match found");
        }
        return null;
    }
}
