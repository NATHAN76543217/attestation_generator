package com.example.attestation_generator.ui.users;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.home.AttestListAdapter;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> mUserList;
    final private UsersFragment.userinterface mOnClickListener;
    private List<Attestation> mAttestationList;
    private AttestListAdapter mAdapter;
    private Context mcontext;

    public UsersListAdapter(List<Attestation> attestationList, AttestListAdapter adapter, Context context, List<User> UserList, UsersFragment.userinterface onClickListener) {
        this.mUserList = UserList;
        this.mOnClickListener = onClickListener;
        this.mAttestationList = attestationList;
        this.mAdapter = adapter;
        this.mcontext = context;
    }

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_user_item, parent, false);
        return new UsersViewHolder(view, mAttestationList, mAdapter, mcontext, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Log.i("My TAG", "BIND:  " + this.mUserList.get(position).getName());
        holder.getTitle().setText(this.mUserList.get(position).getName());
        holder.setUser(this.mUserList.get(position));
        holder.setVisibility();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


}
