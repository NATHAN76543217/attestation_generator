package com.example.attestation_generator.ui.users;

import android.app.ListFragment;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.home.AttestListAdapter;
import com.example.attestation_generator.ui.home.HomeFragment;

import java.util.List;

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTitle;
    private CheckBox mCheckBox;


    private UsersFragment.userinterface mOnClickListener;
    private User user;
    private List<Attestation> mAttestationList;
    private AttestListAdapter mAdapter;
    private Context mContext;
    private PopupWindow mPopupWindow;

    public UsersViewHolder(@NonNull final View itemView, PopupWindow popupWindow, List<Attestation> attestationList, AttestListAdapter adapter, Context context, UsersFragment.userinterface Listener) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.userText);
        mCheckBox = itemView.findViewById(R.id.userCheckBox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility();
            }
        });
        mOnClickListener = Listener;
        this.mAdapter = adapter;
        this.mAttestationList = attestationList;
        this.mContext = context;
        this.mPopupWindow = popupWindow;

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
        if (mContext != null) {
            //click on user in new attest
            Log.i("My TAG", "New from user");
            mOnClickListener.onUserInteraction(mAttestationList, mAdapter, mContext, user);
            mPopupWindow.dismiss();
        }
        else {
            //click on user in Mes utilisateurs
            mCheckBox.setChecked(!mCheckBox.isChecked());
        }
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
}