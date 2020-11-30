package fr.attestation_generator.ui.attestations;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import fr.attestation_generator.R;
import com.itextpdf.text.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class Attestation {

    private final File    PDF_file;
    private final String  fileName;
    private int     id;
    private final Context context;
    private BasicFileAttributes mFileAttributes;
    private String creationDate;

    public Attestation(Context context, File pdf)
    {
        this.context = context;
        this.fileName = pdf.getName().substring(0, pdf.getName().length() - 4);
        this.PDF_file = pdf;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.mFileAttributes = Files.readAttributes(this.PDF_file.toPath(), BasicFileAttributes.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.dateFormat), Locale.getDefault());
                creationDate = simpleDateFormat.format(new Date(mFileAttributes.creationTime().toMillis()));
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public Attestation(Context context, Hashtable<String, Object> dic)
    {
        this.context = context;
        Document PDF_doc = (Document) dic.get("Document");
        this.PDF_file = (File) dic.get("PDF");
        this.fileName = (String) dic.get("fileName");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.mFileAttributes = Files.readAttributes(this.PDF_file.toPath(), BasicFileAttributes.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.dateFormat), Locale.getDefault());
                creationDate = simpleDateFormat.format(new Date(mFileAttributes.creationTime().toMillis()));
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public BasicFileAttributes getFileAttributes() {
        return mFileAttributes;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public File getPdfFolder()
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

    public int getId()
    {
        return this.id;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public File getPDF_file() {
        Log.i("My TAG", String.format("GET: Att.PDF_file = %s", PDF_file.getName()));
        return PDF_file;
    }

    public void deleteFile() {
	    PDF_file.delete();
    }

}
