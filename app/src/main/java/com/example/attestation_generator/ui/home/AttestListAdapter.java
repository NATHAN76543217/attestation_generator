package com.example.attestation_generator.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.attestations.Attestation;

import java.util.List;

public class AttestListAdapter extends RecyclerView.Adapter<HomeViewHolder> {

    //FOR INTERACTION
    private HomeFragment.OnFIL mclickListener;

    // FOR DATA
    private List<Attestation> mAttestationList;

    // CONSTRUCTOR
    public AttestListAdapter(List<Attestation> attestationList, HomeFragment.OnFIL _clickListener) {
        this.mAttestationList = attestationList;
        this.mclickListener = _clickListener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.fragment_attestations;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_attest_item, parent, false);
        return new HomeViewHolder(view, mclickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        holder.getTitleView().setText(this.mAttestationList.get(position).getFileName());
        holder.getContentView().setText(this.mAttestationList.get(position).getCreationDate());
        holder.setMyPdf(this.mAttestationList.get(position).getPDF_file());
        holder.setCrossClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Attestation theRemovedItem = mAttestationList.get(position);
                Log.i("My TAG", "remove :" + theRemovedItem.getFileName());
                theRemovedItem.deleteFile();
                // remove your item from data base
                mAttestationList.remove(position);  // remove the item from list
                notifyItemRemoved(position); // notify the adapter about the removed item
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mAttestationList.size();
    }

}

