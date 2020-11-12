package com.example.attestation_generator.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attestation_generator.R;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.users.User;
import com.example.attestation_generator.ui.users.UsersFragment;
import com.example.attestation_generator.ui.users.UsersListAdapter;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;


public class HomeFragment extends Fragment implements OnLoadCompleteListener, OnPageChangeListener, OnPageErrorListener {

    private RecyclerView mRecyclerView;
    private List<Attestation> mAttestationList;
    private AttestListAdapter adapter;
    private OnFIL mclickListener;
    private UsersFragment.userinterface mUserListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.attestation_frame_layout, container, false);
        mRecyclerView = root.findViewById(R.id.my_recycler_view);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("My TAG", "FAB clicked");
                choosePopup(view);
            }
        });

        try {
            this.configureRecyclerView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        return root;
    }

    private void configureRecyclerView() throws FileNotFoundException, DocumentException {
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
            File folder = getPdfFolder(getContext());
            File files[] = folder.listFiles();
            Context ctx = getContext();
            Log.i("My TAG", "Files: Get all files in pdf directory");
            Log.i("My TAG", String.format("Files: Found %d files", files.length));
            for (int i = 0; i < files.length; i++) {
                this.mAttestationList.add(new Attestation(ctx, files[i]));
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
            this.adapter = new AttestListAdapter(this.mAttestationList, mclickListener);
            this.mRecyclerView.setAdapter(this.adapter);

        }

    }

    static private Hashtable createPDF(Context context, Hashtable dic) throws FileNotFoundException, DocumentException {

        File pdfFolder = getPdfFolder(context);

        //Choose new file name
        Date date = new Date();
        String timehour = new SimpleDateFormat("HH").format(date);
        String timemin = new SimpleDateFormat("mm").format(date);
        String strName = dic.get("Name") + "_" + timehour + "h" + timemin + ".pdf";
        String pathname = pdfFolder + "/" + strName;
        Log.i("My TAG", String.format("Files: Create PDF: %s", strName));

        File NewPDF = new File(pathname);
        //Step 2 nouvelle attestation
        Document document = new Document();
        OutputStream output = new FileOutputStream(NewPDF);
        //writer == dest

        dic.put("Document", document);
        dic.put("Output", output);
        dic.put("PDF", NewPDF);
        dic.put("fileName", strName);
        return fillPDF(context, dic);
    }

    private static Hashtable fillPDF(Context context, Hashtable dic) throws DocumentException {
        //copy template pdf
        Document document = (Document )dic.get("Document");
        OutputStream output = (OutputStream) dic.get("Output");
        PdfWriter writer = PdfWriter.getInstance(document, output);
        document.open();
        PdfReader reader = null;
        try {
            reader = new PdfReader(context.getExternalFilesDir("").getPath() + File.separator + context.getString(R.string.pdfTemplateName));
        } catch (IOException e) {
            Log.e("My TAG", "template pdf not found");
            e.printStackTrace();
        }
        // Copy all the template page's
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            document.newPage();
            PdfImportedPage page = writer.getImportedPage(reader, i);
            writer.getDirectContent().addTemplate(page, 0, 0);
        }

        //Texts position in pdf
        float name_x = document.left() + 85 + 5; // 36 + x
        float name_y = document.top() - 112; //806 - y

        float birthday_x = document.left() + 85 + 5; // 36 + x
        float birthday_y = document.top() - 132;

        float birthplace_x = document.left() + 265;
        float birthplace_y = document.top() - 132;

        float adresse_x = document.left() + 100;
        float adresse_y = document.top() - 155;

        float city_x = document.left() + 82;
        float city_y = document.bottom() + 138;

        float date_x = document.left() + 82;
        float date_y = document.bottom() + 114;

        float time_x = document.left() + 223;
        float time_y = document.bottom() + 114;

        //TODO verifier position de adresse
        PdfContentByte content = writer.getDirectContent();
        printOnPdf(content, (String) dic.get("Name"), new Rectangle(name_x, name_y, name_x + 200, name_y + 20));
        printOnPdf(content, (String) dic.get("Birthday"), new Rectangle(birthday_x, birthday_y, birthday_x + 80, birthday_y + 20));
        printOnPdf(content, (String) dic.get("Birthplace"), new Rectangle(birthplace_x, birthplace_y, birthplace_x + 80, birthplace_y + 20));
        printOnPdf(content, (String) dic.get("Adresse"), new Rectangle(adresse_x, adresse_y, adresse_x + 380, adresse_y + 20));
        printOnPdf(content, (String) dic.get("City"), new Rectangle(city_x, city_y, city_x + 80, city_y + 20));
        printOnPdf(content, (String) dic.get("Date"), new Rectangle(date_x, date_y, date_x + 80, date_y + 20));
        printOnPdf(content, (String) dic.get("Time"), new Rectangle(time_x, time_y, time_x + 80, time_y + 20));

        //Step 5: Close the document
        if (document != null)
            document.close();
        if (reader != null)
            reader.close();
        return dic;
    }

    private static void printOnPdf(PdfContentByte content, String text, Rectangle rect) throws DocumentException {
        ColumnText ct = new ColumnText(content);
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph(text));
        ct.go();
    }

    public void choosePopup(final View anchorView)
    {
        View chooseView = getLayoutInflater().inflate(R.layout.pop_up_choice_layout, null);
        final View popupNewView = getLayoutInflater().inflate(R.layout.pop_up_new_layout, null);

        final int location[] = new int[2];
        location[0] = 0;
        location[1] = 0;

        final PopupWindow popupWindow = new PopupWindow(chooseView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Button fromUser = (Button) chooseView.findViewById(R.id.popUpChoiceFrom);
        Button fromNew = (Button) chooseView.findViewById(R.id.popUpChoiceNew);
        fromNew.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupWindow.dismiss();
                                            popupWindow.setContentView(popupNewView);
                                            popUpNew(anchorView, popupWindow, popupNewView);
                                            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());
                                        }
                                    });
        fromUser.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupWindow.dismiss();
                                            popUpUser(anchorView, popupWindow);
                                            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());
                                        }
                                    });
        //Parameters
        popupWindow.setFocusable(true);
        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());
    }

    public void popUpUser(final View anchorView , final PopupWindow popupWindow)
    {
        View userView = getLayoutInflater().inflate(R.layout.pop_up_user_layout, null);
        popupWindow.setContentView(userView);

        RecyclerView userRecycler = userView.findViewById(R.id.popUpUserList);
        userRecycler.setLayoutManager(new LinearLayoutManager(userView.getContext()));

        List<User> userList = new ArrayList<>();
        UsersFragment.fillUsersList(getContext(), userList);
        Log.i("My TAG", "user list size = " + userList.size());
        UsersListAdapter UsersAdapter = new UsersListAdapter(popupWindow, mAttestationList, adapter, getContext(), userList, mUserListener);
        userRecycler.setAdapter(UsersAdapter);

    }

    public void popUpNew(final View anchorView , final PopupWindow popupWindow, final View popupView) {

        Log.i("Debug", "set");

        final TextView Ttitle = (TextView) popupView.findViewById(R.id.popUpTitle);
        Ttitle.setText(R.string.popUpTitle);
        final EditText EName = (EditText) popupView.findViewById(R.id.popUpGetName);
        final EditText ECity = (EditText) popupView.findViewById(R.id.popUpGetCity);
        final EditText EAdresse = (EditText) popupView.findViewById(R.id.popUpGetAdresse);
        final EditText EBirthplace = (EditText) popupView.findViewById(R.id.popUpGetBirthplace);
        final DatePicker datepicker=(DatePicker)popupView.findViewById(R.id.popUpGetBirthday);
        Button BT = (Button) popupView.findViewById(R.id.popUpButton);
        BT.setText(R.string.validation);
        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View popUpView) {
                //click sur valider
                if (EName.getText().toString().length() == 0 || ECity.getText().toString().length() == 0 || EAdresse.getText().toString().length() == 0 || EBirthplace.getText().toString().length() == 0)
                {
                    Toast.makeText(anchorView.getContext(),"Certain champs sont vide", Toast.LENGTH_LONG).show();
                    return;
                }
                Hashtable dic = new Hashtable();
                dic.put("Name", EName.getText().toString());
                dic.put("Birthday", datepicker.getDayOfMonth() + " / " + (datepicker.getMonth() + 1) + " / " + datepicker.getYear());
                dic.put("Birthplace", EBirthplace.getText().toString());
                dic.put("Adresse", EAdresse.getText().toString());
                dic.put("City", ECity.getText().toString().substring(0, 1).toUpperCase() + ECity.getText().toString().substring(1));
                Date now = new Date();
                dic.put("Date", new SimpleDateFormat("dd / MM / YYYY").format(now));
                dic.put("Time", new SimpleDateFormat("HH mm").format(now));
                newPdf(mAttestationList, adapter, getContext(), dic);
                popupWindow.dismiss();
            }
        });
    }

    public static void newPdf(List<Attestation> AttestationList, AttestListAdapter adapter, Context context, Hashtable dic)
    {
        try {
            AttestationList.add(new Attestation(context, createPDF(context, dic)));
            Log.i("My TAG", "Files: new file with popUp.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
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
    public static File getPdfFolder(Context context)
    {
        File pdfFolder = new File(context.getExternalFilesDir(""), "mes attestations/");
        if (!pdfFolder.exists()) {
            if (pdfFolder.mkdir())
                Log.i("My TAG", String.format("Pdf Directory <%s> created", pdfFolder.getName()));
            else
            {
                Log.e("My TAG", String.format("Pdf Directory <%s> creation failed", pdfFolder.getName()));
                return null;
            }
        }
        return pdfFolder;
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
    public void onAttach(Context context) {
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

    public interface OnFIL{
        //Par ce cette méthode le fragment va communiquer avec l'activity (lui demander dans notre cas de lancer une nouvelle activity tout en servant des données en paramètre de la méthode pour un traitement spécifique
        void onFragInteract(File data);
    }
}