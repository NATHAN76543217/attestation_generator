package com.example.attestation_generator.pdf;

import android.content.Intent;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import com.example.attestation_generator.R;

import java.io.File;

public class displayPdf extends AppCompatActivity {

    PDFView pdfView;
    File myPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pdf);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pdf")) {
            myPdf = (File) intent.getExtras().get("pdf");
            if (myPdf != null)
                Log.i("My TAG", String.format("LOAD ACTIVITY: IN--pdf_file = %s", myPdf.getName()));
            else
                Log.e("My TAG", "LOAD ACTIVITY: IN--pdf_file = NULL");
        }
        Log.i("My TAG", String.format("filename = %s", myPdf.getName()));
        Log.i("My TAG", String.format("pathname = %s", myPdf.getPath()));
        pdfView = findViewById(R.id.pdfViewer);
        pdfView.fromFile(myPdf)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(0)
                .load();
        Log.i("My TAG", "DISPLAY PDF");
    }
}