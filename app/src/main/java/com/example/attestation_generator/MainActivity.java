package com.example.attestation_generator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.example.attestation_generator.pdf.displayPdf;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.attestations.AttestationFactory;
import com.example.attestation_generator.ui.home.AttestListAdapter;
import com.example.attestation_generator.ui.home.HomeFragment;
import com.example.attestation_generator.ui.parameters.Param;
import com.example.attestation_generator.ui.parameters.parameters;
import com.example.attestation_generator.ui.users.User;
import com.example.attestation_generator.ui.users.UsersFragment;
import com.example.attestation_generator.ui.users.UsersListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFIL, UsersFragment.userinterface {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //ADD template.pdf
        File file = new File(getExternalFilesDir("") + java.io.File.separator + "template.pdf");
        if (!file.exists()) {
            Log.i("My TAG", "Create pdf template file in storage");
            AssetManager assetManager = getAssets();
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(getString(R.string.pdfTemplateName));
                Log.i("My TAG", String.format("Copy: %s", in.toString()));
                out = new FileOutputStream(file);
                Log.i("My TAG", String.format("to: %s", file.getPath()));
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("My TAG", "Failed to copy asset file: " + in.toString(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
        else
            Log.i("My TAG", "template pdf already exist in storage");
        //uncomment to start with empty SharedPreferences
        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        auto_create_attestations();
    }

    private void auto_create_attestations() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //récupere parametre auto_creare
        Boolean auto_create = (Boolean) Param.loadParam(preferences, getString(R.string.auto_create), Param.BOOLEAN);
        if (auto_create == false)
            return ;

        //récupere list user
        List<User> userList = new ArrayList<>();
        UsersFragment.fillUsersList(this, userList);

        //recupere parametre list user
        String users_str = (String) Param.loadParam(preferences, getString(R.string.create_for_users), Param.STRING);
        String[] to_create = users_str.split(";");
        for(String str : to_create) {
            Log.i("My TAG", "--" + str);
            String value[] = str.split(":");
            Log.i("My TAG", "0--" + value[0]);
            Log.i("My TAG", "1--" + value[1]);

            // crée pour chaque user
            for (User user : userList) {
                if (user.getName().equals(value[0])) {
                    Log.i("My TAG", "create auto for user " + user);
                    user.setDefaultMotif(value[1]);
                    AttestationFactory.newAttestation(this, user.getDic(true));
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.optParametres:
                openParameters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @Override
    public void onFragInteract(File pdf) {
        Log.i("My TAG", "Lunch new Activity");
        Intent intent = new Intent(this, displayPdf.class);
        intent.putExtra("pdf", pdf);
        if (pdf != null)
            Log.i("My TAG", String.format("LOAD ACTIVITY: OUT--pdf_file = %s", pdf.getName()));
        else
            Log.e("My TAG", "LOAD ACTIVITY: OUT--pdf_file = NULL");
        startActivity(intent);
    }

    @Override
    public void onUserInteraction(List<Attestation> attestationList, AttestListAdapter adapter, Context context, User user) {
        Hashtable dic = new Hashtable();
        dic.put("Motif", user.getDefaultMotif());
        dic.put("Name", user.getName());
        dic.put("Birthday", user.getBirthday());
        dic.put("Birthplace", user.getBirthplace());
        dic.put("Adresse", user.getAdresse());
        dic.put("City", user.getCity());
        Date now = new Date();
        dic.put("Date", new SimpleDateFormat("dd / MM / YYYY").format(now));
        dic.put("Time", new SimpleDateFormat("HH mm").format(now));
        HomeFragment.addNewPdf(attestationList, adapter, context, dic);
    }
    private void openParameters() {
        Intent intent = new Intent(this, parameters.class);
        startActivity(intent);
    }
}
