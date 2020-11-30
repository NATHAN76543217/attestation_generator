package fr.attestation_generator.ui.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.attestation_generator.R;
import fr.attestation_generator.ui.attestations.Attestation;
import fr.attestation_generator.ui.home.AttestListAdapter;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private final List<User> mUserList;
    final private UsersFragment.userinterface mOnClickListener;
    private final List<Attestation> mAttestationList;
    private final AttestListAdapter mAdapter;
    private final Context mcontext;
    private final PopupWindow mPopupWindow;
    private final Button mBtDel;
    private final Spinner mSpinMotif;

    public UsersListAdapter(PopupWindow popupWindow, List<Attestation> attestationList, AttestListAdapter adapter, Context context, List<User> UserList, UsersFragment.userinterface onClickListener, Button btDel, Spinner spin) {
        this.mUserList = UserList;
        this.mOnClickListener = onClickListener;
        this.mAttestationList = attestationList;
        this.mAdapter = adapter;
        this.mcontext = context;
        this.mPopupWindow = popupWindow;
        this.mBtDel = btDel;
        this.mSpinMotif = spin;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_user_item, parent, false);
        return new UsersViewHolder(view, mPopupWindow, mAttestationList, mAdapter, mcontext, mOnClickListener, mBtDel, mUserList, mSpinMotif, this);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.getTitle().setText(this.mUserList.get(position).getName());
        holder.setUser(this.mUserList.get(position));
        holder.setVisibility();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }



}
