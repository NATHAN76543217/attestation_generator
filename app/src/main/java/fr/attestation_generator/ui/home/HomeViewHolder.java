package fr.attestation_generator.ui.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.attestation_generator.R;

import java.io.File;

public class HomeViewHolder extends RecyclerView.ViewHolder{

    private TextView mTextView;
    private TextView mcontent;
    private ImageButton mImageButton;
    private HomeFragment.OnFIL mClickListener;
    private File myPdf;

    public HomeViewHolder(@NonNull final View itemView, HomeFragment.OnFIL _clickListener) {
        super(itemView);

        mTextView = itemView.findViewById(R.id.item_title);
        mcontent = itemView.findViewById(R.id.item_content);
        mImageButton = itemView.findViewById(R.id.item_cross);
        mClickListener = _clickListener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ici on envoie un message à notre activity
                mClickListener.onFragInteract(myPdf); /*on passe la liste à */
                }
            });
    }
    public TextView getTitleView(){
        return mTextView;
    }
    public TextView getContentView()
    {
        return mcontent;
    }
    public void setMyPdf(File pdf_file) {
        myPdf = pdf_file;
    }

    public void setCrossClickListener(View.OnClickListener listener) {
        mImageButton.setOnClickListener(listener);
    }
}
