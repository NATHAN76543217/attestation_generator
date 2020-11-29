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

    private String  User;
    private Document    PDF_doc;
    private File    PDF_file;
    private String  filepath;
    private String  fileName;
    private String motif;
    private int     Date;
    private int     id;
    private Context context;
    private BasicFileAttributes mFileAttributes;
    private String creationDate;

    public Attestation(Context context, File pdf)
    {
        this.context = context;
        this.fileName = pdf.getName().substring(0, pdf.getName().length() - 4);
        this.filepath = getPdfFolder() + "/" + this.fileName;
        this.PDF_file = pdf;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.mFileAttributes = Files.readAttributes(this.PDF_file.toPath(), BasicFileAttributes.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.dateFormat), Locale.getDefault());
                creationDate = simpleDateFormat.format(new Date(mFileAttributes.creationTime().toMillis())).toString();
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public Attestation(Context context, Hashtable dic)
    {
        this.context = context;
        this.PDF_doc = (Document) dic.get("Document");
        this.PDF_file = (File) dic.get("PDF");
        this.fileName = (String) dic.get("fileName");
        this.filepath = (String) dic.get("filePath");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.mFileAttributes = Files.readAttributes(this.PDF_file.toPath(), BasicFileAttributes.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.dateFormat), Locale.getDefault());
                creationDate = simpleDateFormat.format(new Date(mFileAttributes.creationTime().toMillis())).toString();
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
    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getFileName()
    {
        return this.fileName;
    }
    public String getFilepath(Boolean extention)
    {
        if (extention)
        {
            return  this.filepath ;
        }
        else
            return  this.filepath.substring(0, filepath.length() - 4);

    }

    public Document getPDF_doc()
    {
        return this.PDF_doc;
    }

    public File getPDF_file() {
        Log.i("My TAG", String.format("GET: Att.PDF_file = %s", PDF_file.getName()));
        return PDF_file;
    }

    public String PdfIsCreate()
    {
        if (this.PDF_file == null) {
            return ("NULL");
        }
        else
            return ("PDF create");
    }

	public void deleteFile() {
	    PDF_file.delete();
    }

}
