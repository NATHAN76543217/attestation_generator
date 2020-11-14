package com.example.attestation_generator.ui.attestations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.attestation_generator.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class AttestationFactory {

    //de 0 à 8
    static float y_box[] = {230f,275f, 333f,375f, 415f,455f, 515f,555f, 600f};

    static public Attestation newAttestation(Context context, Hashtable dic)
    {
        try {
            return new Attestation(context, createAttestation(context, dic));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    //crée un nouveau pdf puis le passe a fillAttestation
    static private Hashtable createAttestation(Context context, Hashtable dic) throws FileNotFoundException, DocumentException {

        File pdfFolder = getPdfFolder(context);

        //Choose new file name
        Date date = new Date();
        String strName = dic.get("Name") + " " + String.valueOf(dic.get("Time")).substring(0, 2) + "h" + String.valueOf(dic.get("Time")).substring(3);
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
        return fillAttestation(context, dic);
    }

    //remplie un pdf avec les information utilisateur
    private static Hashtable fillAttestation(Context context, Hashtable dic) throws DocumentException {
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

        float img_x = 77f;
        int box = Integer.parseInt((String) dic.get("Motif"));
        Log.i("My TAG", "BOX: " + box);
        float img_y = document.top() - y_box[box];
        //TODO ajouter User.mDefaultMotif dans créa auto + dans popUpUser edit
        //TODO ajouter signature
        try {
            InputStream ims = context.getAssets().open("checkmark.png");
            Log.i("My TAG", ims.toString());
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image mark = Image.getInstance(stream.toByteArray());
            mark.setAbsolutePosition(img_x, img_y);
            mark.scalePercent(5f);
            writer.getDirectContent().addImage(mark);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO mettre delais de 30 minutes dans auto_create
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

    //imprime un rectangle avec une string dans un pdf
    private static void printOnPdf(PdfContentByte content, String text, Rectangle rect) throws DocumentException {
        ColumnText ct = new ColumnText(content);
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph(text));
        ct.go();
    }

    //return a File on the directory that contain all attestations
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

}
