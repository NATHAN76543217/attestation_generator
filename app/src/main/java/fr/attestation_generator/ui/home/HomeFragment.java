package fr.attestation_generator.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.attestation_generator.R;
import fr.attestation_generator.ui.attestations.Attestation;
import fr.attestation_generator.ui.attestations.AttestationFactory;
import fr.attestation_generator.ui.users.User;
import fr.attestation_generator.ui.users.UsersFragment;
import fr.attestation_generator.ui.users.UsersListAdapter;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements OnLoadCompleteListener, OnPageChangeListener, OnPageErrorListener, AdapterView.OnItemSelectedListener {

    private RecyclerView mRecyclerView;
    private List<Attestation> mAttestationList;
    private AttestListAdapter adapter;
    private OnFIL mclickListener;
    private UsersFragment.userinterface mUserListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_attestations, container, false);
        mRecyclerView = root.findViewById(R.id.my_recycler_view);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("My TAG", "FAB clicked");
                choosePopup(view);
            }
        });

        this.configureRecyclerView();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        return root;
    }

    private void configureRecyclerView() {
        this.mAttestationList = new ArrayList<>();
        Log.i("My TAG", "Ask Write permission");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // request the permission
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        else {
            // has the permission.
            Log.i("My TAG", "Write Permission granted");
            File folder = AttestationFactory.getPdfFolder(getContext());
            File[] files = folder.listFiles();
            Context ctx = getContext();
            Log.i("My TAG", String.format("Files: Found %d files", files.length));
            for (File file : files) {
                this.mAttestationList.add(new Attestation(ctx, file));
            }
            //sort mAttestation de la plus recente a la plus ancienne
            Collections.sort(mAttestationList, new Comparator<Attestation>() {
                @Override
                public int compare(Attestation attestation, Attestation t1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        return t1.getFileAttributes().creationTime().compareTo(attestation.getFileAttributes().creationTime());
                    }
                    return 0;
                }
            });
        }
        this.adapter = new AttestListAdapter(this.mAttestationList, mclickListener);
        this.mRecyclerView.setAdapter(this.adapter);
    }

    public void choosePopup(final View anchorView)
    {
        View chooseView = getLayoutInflater().inflate(R.layout.pop_up_choice_layout, null);
        final View popupNewView = getLayoutInflater().inflate(R.layout.pop_up_new_layout, null);

        final PopupWindow popupWindow = new PopupWindow(chooseView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Button fromUser = chooseView.findViewById(R.id.popUpChoiceFrom);
        Button fromNew = chooseView.findViewById(R.id.popUpChoiceNew);
        fromNew.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupWindow.dismiss();
                                            popupWindow.setContentView(popupNewView);
                                            popUpNew(anchorView, popupWindow, popupNewView);
                                            popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
                                        }
                                    });
        fromUser.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupWindow.dismiss();
                                            popUpUser(anchorView, popupWindow);
                                            popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
                                        }
                                    });
        //Parameters
        popupWindow.setFocusable(true);
        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(13);
        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER,0, 0);
    }

    public void popUpUser(final View anchorView , final PopupWindow popupWindow)
    {
        View userView = getLayoutInflater().inflate(R.layout.pop_up_from_user_layout, null);
        popupWindow.setContentView(userView);

        final Spinner spin = userView.findViewById(R.id.popUpUserSpin);
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.popUp_motifs));
        spin.setAdapter(aa);

        RecyclerView userRecycler = userView.findViewById(R.id.popUpUserList);
        userRecycler.setLayoutManager(new LinearLayoutManager(userView.getContext()));

        List<User> userList = new ArrayList<>();
        UsersFragment.fillUsersList(getContext(), userList);
        UsersListAdapter UsersAdapter = new UsersListAdapter(popupWindow, mAttestationList, adapter, getContext(), userList, mUserListener, null, spin);
        userRecycler.setAdapter(UsersAdapter);

        Button back  = userView.findViewById(R.id.back_bt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                choosePopup(anchorView);
            }
        });
    }

    public void popUpNew(final View anchorView , final PopupWindow popupWindow, final View popupView) {
        //link items
        final Spinner spin = popupView.findViewById(R.id.popUpSpinner);
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.popUp_motifs));
        spin.setAdapter(aa);
        final EditText EName = popupView.findViewById(R.id.popUpGetName);
        final EditText ECity = popupView.findViewById(R.id.popUpGetCity);
        final EditText EAdresse = popupView.findViewById(R.id.popUpGetAdresse);
        final EditText EBirthplace = popupView.findViewById(R.id.popUpGetBirthplace);
        final DatePicker datepicker= popupView.findViewById(R.id.popUpGetBirthday);
        Button BT = popupView.findViewById(R.id.popUpButton);
        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View popUpView) {
                //click sur valider
                if (EName.getText().toString().length() == 0 || ECity.getText().toString().length() == 0 || EAdresse.getText().toString().length() == 0 || EBirthplace.getText().toString().length() == 0)
                {
                    Toast.makeText(anchorView.getContext(),getString(R.string.empty_fields), Toast.LENGTH_LONG).show();
                    return;
                }
                Hashtable<String,Object> dic = new Hashtable<>();
                dic.put("Motif", String.valueOf(spin.getSelectedItemId()));
                dic.put("Name", EName.getText().toString());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());
                dic.put("Birthday", sdf.format(datepicker.getCalendarView().getDate()));
                dic.put("Birthplace", EBirthplace.getText().toString());
                dic.put("Adresse", EAdresse.getText().toString());
                dic.put("City", ECity.getText().toString().substring(0, 1).toUpperCase() + ECity.getText().toString().substring(1));
                Date now = new Date();
                dic.put("Date", new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(now));
                dic.put("Time", new SimpleDateFormat("HH mm", Locale.getDefault()).format(now).replace(" ", " h "));
                addNewPdf(mAttestationList, adapter, getContext(), dic);
                popupWindow.dismiss();
            }
        });
        Button back  = popupView.findViewById(R.id.back_bt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                choosePopup(anchorView);
            }
        });
    }

    public static void addNewPdf(List<Attestation> AttestationList, AttestListAdapter adapter, Context context, Hashtable<String, Object> dic)
    {
        AttestationList.add(AttestationFactory.newAttestation(context, dic));
        //trie mAttestation de la plus recente a la plus ancienne
        Collections.sort(AttestationList, new Comparator<Attestation>() {
            @Override
            public int compare(Attestation attestation, Attestation t1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    return t1.getFileAttributes().creationTime().compareTo(attestation.getFileAttributes().creationTime());
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(AttestationList.size() - 1);
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    // Gestion click

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFIL) {
            //Ici, on affecte l'instance OnTonFragmentInteractionListener à notre objet mListener
            mclickListener = (OnFIL) context;
            mUserListener = (UsersFragment.userinterface) context;
        }

        else if (context instanceof UsersFragment.userinterface)
        {
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnTonFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mclickListener = null;
        mUserListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public interface OnFIL{
        //Par ce cette méthode le fragment va communiquer avec l'activity (lui demander dans notre cas de lancer une nouvelle activity tout en servant des données en paramètre de la méthode pour un traitement spécifique
        void onFragInteract(File data);
    }
}