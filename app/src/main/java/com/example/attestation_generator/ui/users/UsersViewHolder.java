package com.example.attestation_generator.ui.users;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.home.AttestListAdapter;
import com.example.attestation_generator.ui.home.HomeFragment;

import java.util.Hashtable;
import java.util.List;

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

	private TextView mTitle;
	private CheckBox mCheckBox;


	private UsersFragment.userinterface mOnClickListener;
	private User user;
	private List<Attestation> mAttestationList;
	private List<User> mUserList;
	private AttestListAdapter mAdapter;
	private Context mContext;
	private PopupWindow mPopupWindow;
	private Button mBtDel;
	private Spinner mSpinMotif;
	private UsersListAdapter mUsersAdapter;


	public UsersViewHolder(@NonNull final View itemView, PopupWindow popupWindow, List<Attestation> attestationList, AttestListAdapter adapter, Context context, UsersFragment.userinterface Listener, Button btDel, List<User> userList, Spinner spin, UsersListAdapter usersListAdapter) {
		super(itemView);
		mTitle = itemView.findViewById(R.id.userText);
		mCheckBox = itemView.findViewById(R.id.userCheckBox);
		mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setVisibility();
				setDelText(isNoCheck());

			}
		});
		mOnClickListener = Listener;
		this.mAdapter = adapter;
		this.mAttestationList = attestationList;
		this.mUserList = userList;
		this.mContext = context;
		this.mPopupWindow = popupWindow;
		this.mBtDel = btDel;
		this.mSpinMotif = spin;
		this.mUsersAdapter = usersListAdapter;
		itemView.setOnClickListener(this);

	}

	public TextView getTitle() {
		return mTitle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void onClick(View view) {
		if (mAttestationList != null) {
			//click on user in new attest
			Log.i("My TAG", "New from user");
			user.setDefaultMotif(String.valueOf(mSpinMotif.getSelectedItemId()));
			mOnClickListener.onUserInteraction(mAttestationList, mAdapter, mContext, user);
			mPopupWindow.dismiss();
		}
		else {
			//click on user in Mes utilisateurs
			if (user.isCheckBoxVisible)
			{//if deletion
				mCheckBox.setChecked(!mCheckBox.isChecked());
				setDelText(isNoCheck());
			}
			else
			{//if edition
				editUser(itemView);
			}
		}
	}

	public void editUser(final View anchorView) {
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupView = inflater.inflate(R.layout.pop_up_edit_user, null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


		final EditText EName = (EditText) popupView.findViewById(R.id.popUpGetName);
		EName.setText(user.getName());
		final EditText ECity = (EditText) popupView.findViewById(R.id.popUpGetCity);
		ECity.setText(user.getCity());
		final EditText EAdresse = (EditText) popupView.findViewById(R.id.popUpGetAdresse);
		EAdresse.setText(user.getAdresse());
		final EditText EBirthplace = (EditText) popupView.findViewById(R.id.popUpGetBirthplace);
		EBirthplace.setText(user.getBirthplace());
		final DatePicker datepicker=(DatePicker)popupView.findViewById(R.id.popUpGetBirthday);
		String updateDate[] = user.getBirthday().split("/");
		datepicker.updateDate(Integer.parseInt(updateDate[2].trim()), Integer.parseInt(updateDate[1].trim()), Integer.parseInt(updateDate[0].trim()));

		Button back = (Button) popupView.findViewById(R.id.back_bt);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});
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
				user.setName(EName.getText().toString());
				user.setCity(ECity.getText().toString());
				user.setAdresse(EAdresse.getText().toString());
				user.setBirthplace(EBirthplace.getText().toString());
				user.setBirthday(datepicker.getDayOfMonth() + " / " + (datepicker.getMonth() + 1) + " / " + datepicker.getYear());
				//enregistrement des users
				setUserSet();
				mUsersAdapter.notifyDataSetChanged();
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
		popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
				location[0], location[1] + anchorView.getHeight());

	}
	public void setUserSet()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor edit = preferences.edit();
		//add a new set
		String user_save = "";
		user_save += "0;";
		user_save += user.getName() + ";";
		user_save += user.getBirthday() + ";";
		user_save += user.getBirthplace() + ";";
		user_save += user.getAdresse() + ";";
		user_save += user.getCity()+ ";";
		Log.i("My TAG", "save :" + user_save);
		//push the new set
		String name = "user" + getAdapterPosition();
		edit.putString(name, user_save);
		edit.apply();

		Log.i("My TAG", "user " + name + " set created");
	}
	public void setVisibility() {
		if (user.isCheckBoxVisible)
			mCheckBox.setVisibility(View.VISIBLE);
		else
			mCheckBox.setVisibility(View.INVISIBLE);
		if (mCheckBox.isChecked())
			user.setChecked(true);
		else
			user.setChecked(false);
     /*   Log.i("My TAG", "holder set visibil to: " + user.isCheckBoxVisible());
        Log.i("My TAG", "holder set checked to: " + user.isChecked());
        Log.i("My TAG", "--");
        */
	}

	public void setDelText(Boolean noCheck) {
		if (mBtDel != null)
		{
			if (noCheck)
			{
				mBtDel.setText(mContext.getString(R.string.undo));
			}
			else
				mBtDel.setText(mContext.getString(R.string.delete));
			mBtDel.refreshDrawableState();
		}
	}
	public Boolean isNoCheck()
	{
		for (int i = 0; i < mUserList.size();i++)
		{
			if (mUserList.get(i).isChecked())
				return false;
		}
		return true;
	}
}