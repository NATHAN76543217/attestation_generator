package com.example.attestation_generator.ui.parameters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.users.User;
import com.example.attestation_generator.ui.users.UsersFragment;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.Inflater;

public class parameters extends AppCompatActivity {

    List<Param> mParamList;
    ListView mListView;
    List<User> userList;
    ParamAdapter mParamAdapter;
    UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        final ViewSwitcher switcher = findViewById(R.id.paramSwitcher);
        getSupportActionBar().setTitle(getString(R.string.parameters));

        if (getStorageRight() == 1)
            Log.i("My TAG", "Right granted!");

        //listView
        mListView = findViewById(R.id.paramListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long ID) {
                Log.i("My TAG", "clicked");
                switcher.showNext();
                Button backBT = findViewById(R.id.paramBack);
                backBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String value = ";";
                        for (int i = 0; i < userList.size(); i++)
                        {
                            if (userList.get(i).isAutoCreate)
                                value += userList.get(i).getName() + ";";
                        }
                        mParamList.get(position).setValue(value);
                        Log.i("My TAG", "set Ulist: " + value);
                        switcher.showPrevious();
                    }
                });
                ListView userLV = findViewById(R.id.paramUserListView);
                mUserAdapter = new UserAdapter(view.getContext(), userList);
                userLV.setAdapter(mUserAdapter);
            }
        });
        mParamList = getListOfParam();
        //userList
        userList = LoadUserList(mParamList);
        mParamAdapter = new ParamAdapter(this, mParamList);
        mListView.setAdapter(mParamAdapter);
    }

    private int getStorageRight() {
        Log.i("My TAG", "Ask Write permission");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // request the permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
        else
            return 1;
        return 0;
    }


    private List<User> LoadUserList(List<Param> paramList) {
        List<User> userList;
        userList  = new ArrayList<>();
        UsersFragment.fillUsersList(this, userList);
        String paramValue = (String) paramList.get(1).Value;
        for (int i = 0; i < userList.size(); i++)
        {
            if (paramValue.contains(";" + userList.get(i).getName() + ";"))
            {
                Log.i("My TAG", "FIND " + userList.get(i).getName() + " IN " + paramValue);
                userList.get(i).setIsAutoCreate(true);
            }
        }
        return userList;
    }

    private List<Param> getListOfParam(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parameters.this);
        ArrayList<Param> listParam = new ArrayList<Param>();
        listParam.add(new Param(getString(R.string.auto_create), getString(R.string.paramAutoCreate) , preferences, Param.BOOLEAN));
        listParam.add(new Param(getString(R.string.create_for_users), getString(R.string.paramUserList) , preferences, Param.STRING));
        return  listParam;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sauvegarde les parametres
        for (int i = 0; i < mParamList.size(); i++)
        {
            mParamList.get(i).saveParam();
            Log.i("My TAG", "Save param: " + mParamList.get(i).Value);
        }
    }

    public class ParamAdapter extends BaseAdapter {
        Context ctx;
        List<Param> mParamList;
        private LayoutInflater mInflater;

        public ParamAdapter(Context context, List<Param> paramList) {
            ctx = context;
            mParamList = paramList;
            mInflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return  mParamList.size();
        }

        @Override
        public Object getItem(int i) {
            return mParamList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout layoutItem;

                //(1) : Réutilisation des layouts
                if (convertView == null) {
                    //Initialisation de notre item à partir du  layout XML "personne_layout.xml"
                    layoutItem = (LinearLayout) mInflater.inflate(R.layout.activity_parameters_item, parent, false);
                } else {
                    layoutItem = (LinearLayout) convertView;
                }
                final Param param = mParamList.get(position);
                //(2) : Récupération des TextView de notre layout
                TextView param_name = (TextView)layoutItem.findViewById(R.id.paramName);
                TextView param_desc = (TextView) layoutItem.findViewById(R.id.paramDescription);

                //(3) : Renseignement des valeurs
                param_name.setText(mParamList.get(position).mName);
                param_desc.setText(mParamList.get(position).mDescription);

                //(4) Changement de la couleur du fond de notre item
                if (param.mType == Param.BOOLEAN)
                {
                    CheckBox checkBox = (CheckBox) layoutItem.findViewById(R.id.paramCheckBox);
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked((Boolean) mParamList.get(position).Value);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            param.setValue(b);
                        }
                    });
                }
                else if(param.mType == Param.STRING)
                {

                }
                //On retourne l'item créé.
                return layoutItem;
            }
    }

    public class UserAdapter extends BaseAdapter
    {
        List<User> mUserList;
        Context mCtx;
        private LayoutInflater mInflater;

        public UserAdapter(Context context, List<User> userList)
        {
            this.mUserList = userList;
            this.mCtx = context;
            this.mInflater = LayoutInflater.from(mCtx);
        }

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public Object getItem(int i) {
            return mUserList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layoutItem;

            //(1) : Réutilisation des layouts
            if (convertView == null) {
                //Initialisation de notre item à partir du  layout XML "personne_layout.xml"
                layoutItem = (LinearLayout) mInflater.inflate(R.layout.param_user_list_item, parent, false);
            } else {
                layoutItem = (LinearLayout) convertView;
            }
            final User usr = mUserList.get(position);
            //(2) : Récupération des TextView de notre layout
            TextView usr_name = (TextView)layoutItem.findViewById(R.id.paramUserName);
            CheckBox usr_checkbox = (CheckBox) layoutItem.findViewById(R.id.paramUserCheckBox);

            //(3) : Renseignement des valeurs
            usr_name.setText(mUserList.get(position).getName());
            Log.i("My TAG", "check is" + usr.isAutoCreate);
            usr_checkbox.setChecked(usr.isAutoCreate);
            usr_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    usr.setIsAutoCreate(isChecked);
                }
            });
            //(4) Changement de la couleur du fond de notre item

            //On retourne l'item créé.
            return layoutItem;        }
    }
}