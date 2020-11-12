package com.example.attestation_generator.ui.users;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.home.AttestListAdapter;
import com.example.attestation_generator.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class UsersFragment extends Fragment {

    private UsersListAdapter mUsersAdapter;
    private RecyclerView mUsersView;
    private List<User> mUsersList;
    private UsersFragment.userinterface mUserListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        final Button btAdd = root.findViewById(R.id.usersBTadd);
        final Button btSub = root.findViewById(R.id.usersBTsub);
        final Button btDel = root.findViewById(R.id.userDeleteBt);
        //parametre RecyclerView
        mUsersView = root.findViewById(R.id.usersView);
        mUsersView.setHasFixedSize(true);
        mUsersView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        //uncomment to start with empty SharedPreferences
        //PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();

        //init mUsersList
        mUsersList = new ArrayList<>();
        fillUsersList(getContext(), mUsersList);
        this.mUsersAdapter = new UsersListAdapter(null, null, null, this.mUsersList, mUserListener);
        this.mUsersView.setAdapter(this.mUsersAdapter);
        //add click listener
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("My TAG", "User:\tCreate new user");
                newUser(view);
            }
        });
        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btDel.setVisibility(View.VISIBLE);
                btAdd.setVisibility(View.INVISIBLE);
                btSub.setVisibility(View.INVISIBLE);
                for (int i = 0; i < mUsersList.size(); i++)
                {
                    mUsersList.get(i).setCheckBoxVisible(true);
                }
                mUsersAdapter.notifyDataSetChanged();
            }
        });
        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsersAdapter.notifyDataSetChanged();
                for (int i = 0; i < mUsersList.size(); i++)
                {
                    User elem = mUsersList.get(i);
                    Log.i("My TAG", "usr_checked: " + elem.isChecked());
                    if (elem.isChecked()) {
                        int ndx = mUsersList.indexOf(elem);
                        Log.i("My TAG", "Delete usr" + ndx + " : " + elem.getName());
                        delete_user(getContext(), mUsersList, mUsersList.get(i));
                        mUsersAdapter.notifyItemRemoved(ndx);

                    }
                    else {
                        elem.setCheckBoxVisible(false);
                        elem.setChecked(false);
                    }
                }
                btAdd.setVisibility(View.VISIBLE);
                btSub.setVisibility(View.VISIBLE);
                btDel.setVisibility(View.GONE);
                mUsersAdapter.notifyDataSetChanged();
                Log.i("My TAG", "Hide Del button");
            }
        });
        return root;
    }

    public void newUser(final View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.pop_up_new_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView title = (TextView) popupView.findViewById(R.id.popUpTitle);
        title.setText(R.string.newUserTitle);
        final EditText EName = (EditText) popupView.findViewById(R.id.popUpGetName);
        final EditText ECity = (EditText) popupView.findViewById(R.id.popUpGetCity);
        final EditText EAdresse = (EditText) popupView.findViewById(R.id.popUpGetAdresse);
        final EditText EBirthplace = (EditText) popupView.findViewById(R.id.popUpGetBirthplace);
        final DatePicker datepicker=(DatePicker)popupView.findViewById(R.id.popUpGetBirthday);
        Button BT = (Button) popupView.findViewById(R.id.popUpButton);
        BT.setText(R.string.newUserBt);
        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View popUpView) {
                //if empty field
                if (EName.getText().toString().length() == 0 || ECity.getText().toString().length() == 0 || EAdresse.getText().toString().length() == 0 || EBirthplace.getText().toString().length() == 0)
                {
                    Toast.makeText(anchorView.getContext(),"Certain champs sont vide", Toast.LENGTH_LONG).show();
                    return;
                }
                //create dic and fill it
                Hashtable dic = new Hashtable();
                dic.put("Name", EName.getText().toString());
                dic.put("Birthday", datepicker.getDayOfMonth() + " / " + (datepicker.getMonth() + 1) + " / " + datepicker.getYear());
                dic.put("Birthplace", EBirthplace.getText().toString());
                dic.put("Adresse", EAdresse.getText().toString());
                dic.put("City", ECity.getText().toString().substring(0, 1).toUpperCase() + ECity.getText().toString().substring(1));
                User newU = new User(dic);
                mUsersList.add(newU);
                //enregistrement des users
                addUserSet(newU);
                mUsersAdapter.notifyDataSetChanged();
                mUsersAdapter.notifyItemInserted(mUsersList.size() - 1);
                popupWindow.dismiss();

            }
        });
        //Parameters
        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);
        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        int[] location = new int[2];
        location[0] = 100;
        location[1] = 100;
        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());

    }

    public void addUserSet(User newU)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = preferences.edit();
        //add a new set
        String user_save = "";
        user_save += newU.getName() + ";";
        user_save += newU.getBirthday() + ";";
        user_save += newU.getBirthplace() + ";";
        user_save += newU.getAdresse() + ";";
        user_save += newU.getCity()+ ";";
        Log.i("My TAG", "save :" + user_save);
        //push the new set
        String name = "user" + mUsersList.indexOf(newU);
        edit.putString(name, user_save);
        edit.putInt("nbUsers", preferences.getInt("nbUsers", 0) + 1);
        edit.apply();

        Log.i("My TAG", "user " + name + " set created");
    }
    public static void fillUsersList(Context context, List<User> UserList)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Integer nbUsers = preferences.getInt("nbUsers", 0);
        for (int i = 0; i < nbUsers; i++)
        {
            Log.i("My TAG", "Load user: " + i);
            String Loaded_user = preferences.getString("user" + i, "null");
            UserList.add(new User(Loaded_user));
        }
    }

    public void delete_user(Context context, List<User> usersList,  User usr)
    {
        if (!usersList.contains(usr))
            return;
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mySPrefs.edit();
        usersList.remove(usr);
        for (int i = 0; i < usersList.size(); i++)
        {
             addUserSet(usersList.get(i));
        }
        editor.remove("user" + usersList.size());
        Log.i("My TAG", "new nb users = " + usersList.size());
        editor.putInt("nbUsers", usersList.size());
        editor.apply();



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            mUserListener = (UsersFragment.userinterface) context;
    }

    public interface userinterface {
        void onUserInteraction(List<Attestation> attestationList, AttestListAdapter adapter, Context context, User user);
    }
}

