package com.example.attestation_generator;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.example.attestation_generator.pdf.displayPdf;
import com.example.attestation_generator.ui.attestations.Attestation;
import com.example.attestation_generator.ui.home.AttestListAdapter;
import com.example.attestation_generator.ui.home.HomeFragment;
import com.example.attestation_generator.ui.users.User;
import com.example.attestation_generator.ui.users.UsersFragment;
import com.example.attestation_generator.ui.users.UsersListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;

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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
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
            Log.i("My TAG", String.format("template pdf not found at: %s", file.getPath()));
            Log.i("My TAG", "Create it");
            AssetManager assetManager = getAssets();
            InputStream in = null;
            OutputStream out = null;
            try {
                Log.i("My TAG", "here4");
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
            Log.i("My TAG", String.format("template pdf exist at: %s", file.getPath()));
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
        dic.put("Name", user.getName());
        dic.put("Birthday", user.getBirthday());
        dic.put("Birthplace", user.getBirthplace());
        dic.put("Adresse", user.getAdresse());
        dic.put("City", user.getCity());
        Date now = new Date();
        dic.put("Date", new SimpleDateFormat("dd / MM / YYYY").format(now));
        dic.put("Time", new SimpleDateFormat("HH mm").format(now));
        HomeFragment.newPdf(attestationList, adapter, context, dic);
    }
}
