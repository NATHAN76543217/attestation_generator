package fr.attestation_generator.ui.users;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.preference.PreferenceManager;

import fr.attestation_generator.R;
import fr.attestation_generator.ui.attestations.Attestation;
import fr.attestation_generator.ui.home.AttestListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class UsersFragment extends Fragment {

    private UsersListAdapter mUsersAdapter;
    private List<User> mUsersList;
    private UsersFragment.userinterface mUserListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        final Button btAdd = root.findViewById(R.id.usersBTadd);
        final Button btSub = root.findViewById(R.id.usersBTsub);
        final Button btDel = root.findViewById(R.id.userDeleteBt);
        //parametre RecyclerView
        RecyclerView usersView = root.findViewById(R.id.usersView);
        usersView.setHasFixedSize(true);
        usersView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        //uncomment to start with empty SharedPreferences
        //PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();

        //init mUsersList
        mUsersList = new ArrayList<>();
        fillUsersList(getContext(), mUsersList);
        this.mUsersAdapter = new UsersListAdapter(null, null, null, getContext(), this.mUsersList, mUserListener, btDel, null);
        usersView.setAdapter(this.mUsersAdapter);
        //add click listener
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("My TAG", "--Create new user");
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
                for (int i = mUsersList.size() - 1 ; i >= 0 ; i--)
                {
                    User elem = mUsersList.get(i);
                    Log.i("My TAG", elem.getName() + "_checked: " + elem.isChecked());
                    if (elem.isChecked()) {
                        int ndx = mUsersList.indexOf(elem);
                        Log.i("My TAG", "Delete usr" + ndx + " : " + elem.getName());
                        delete_user(getContext(), mUsersList, mUsersList.get(i));
                        mUsersAdapter.notifyItemRemoved(ndx);
                        mUsersAdapter.notifyDataSetChanged();
                    }
                    else
                    {
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
        View popupView = getLayoutInflater().inflate(R.layout.pop_up_add_user, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


        final EditText EName = popupView.findViewById(R.id.popUpGetName);
        final EditText ECity = popupView.findViewById(R.id.popUpGetCity);
        final EditText EAdresse = popupView.findViewById(R.id.popUpGetAdresse);
        final EditText EBirthplace = popupView.findViewById(R.id.popUpGetBirthplace);
        final DatePicker datepicker= popupView.findViewById(R.id.popUpGetBirthday);
        Button back = popupView.findViewById(R.id.back_bt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        Button BT = popupView.findViewById(R.id.popUpButton);
        BT.setText(R.string.newUserBt);
        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View popUpView) {
                //if empty field
                if (EName.getText().toString().length() == 0 || ECity.getText().toString().length() == 0 || EAdresse.getText().toString().length() == 0 || EBirthplace.getText().toString().length() == 0)
                {
                    Toast.makeText(anchorView.getContext(),getString(R.string.empty_fields), Toast.LENGTH_LONG).show();
                    return;
                }
                //create dic and fill it
                Hashtable<String,Object> dic = new Hashtable<>();
                dic.put("Name", EName.getText().toString());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());
                dic.put("Birthday", sdf.format(datepicker.getCalendarView().getDate()));
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
        location[0] = 0;
        location[1] = 0;
        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER,
                0, anchorView.getHeight());

    }

    public void addUserSet(User newU)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = preferences.edit();
        //add a new set
        String user_save = "";
        user_save += "0;";
        user_save += newU.getName() + ";";
        user_save += newU.getBirthday() + ";";
        user_save += newU.getBirthplace() + ";";
        user_save += newU.getAdresse() + ";";
        user_save += newU.getCity()+ ";";
        Log.i("My TAG", "save :" + user_save);
        //push the new set
        String name = "" + mUsersList.indexOf(newU);
        edit.putString(name, user_save);
        edit.putInt("nbUsers", preferences.getInt("nbUsers", 0) + 1);
        edit.apply();
        Log.i("My TAG", " " + name + " set created");
    }
    //charge
    public static void fillUsersList(Context context, List<User> UserList)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int nbUsers = preferences.getInt("nbUsers", 0);
        for (int i = 0; i < nbUsers; i++)
        {
            String Loaded_user = preferences.getString("" + i, "null");
            Log.i("My TAG", "load usr:" + Loaded_user);
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
        editor.remove("" + usersList.size());
        Log.i("My TAG", "new nb users = " + usersList.size());
        editor.putInt("nbUsers", usersList.size());
        editor.apply();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
            mUserListener = (UsersFragment.userinterface) context;
    }

    public interface userinterface {
        void onUserInteraction(List<Attestation> attestationList, AttestListAdapter adapter, Context context, User user);
    }
}

