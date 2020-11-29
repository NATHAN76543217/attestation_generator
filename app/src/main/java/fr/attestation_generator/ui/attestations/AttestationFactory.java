package fr.attestation_generator.ui.attestations;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import fr.attestation_generator.R;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

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

        //Choose new file name
        String strName = dic.get("Name") + " " + String.valueOf(dic.get("Time")).substring(0, 2) + "h" + String.valueOf(dic.get("Time")).substring(3);
        String filepath = getPdfFolder(context) + File.separator + strName + ".pdf";

        dic.put("fileName", strName);
        dic.put("filePath", filepath);
        return fillAttestation(context, dic);
    }

    //remplie un pdf avec les information utilisateur
    private static Hashtable fillAttestation(Context context, Hashtable dic) throws DocumentException {
        //copy template pdf
        String filepath = (String) dic.get("filePath");

        Log.i("My TAG", "CREATE: " + filepath);
        File NewPDF = new File(filepath);
        try {
            PdfReader reader = new PdfReader(context.getExternalFilesDir("").getPath() + File.separator + context.getString(R.string.pdfTemplateName));
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(NewPDF));

            AcroFields form = stamper.getAcroFields();
            Set<String> names = form.getFields().keySet();
            //fill pdf fields
            form.setField("Nom Prénom", (String) dic.get("Name"));
            form.setField("Date de naissance", (String) dic.get("Birthday"));
            form.setField("Lieu de naissance", (String) dic.get("Birthplace"));
            form.setField("Adresse du domicile", (String) dic.get("Adresse"));
            form.setField("Lieu d'établissement du justificatif", (String) dic.get("City"));
            form.setField("Date", (String) dic.get("Date"));
            form.setField("Heure", (String) dic.get("Time"));
            //check motif
            String motif = "distinction Motif " + (Integer.parseInt((String) dic.get("Motif")) + 1);
            String[] states = form.getAppearanceStates(motif);
            form.setField(motif, states[0]);
            //for QRcode
            PdfContentByte content = stamper.getOverContent(1);
            Image QR = createQR("Cree le: " + dic.get("Date") + " a " + dic.get("Time") + ";\n" +
                    "Nom Prénom: " + dic.get("Name") + ";\n" +
                    "Naissance: " + dic.get("Birthday") + " a " + dic.get("Birthplace") + ";\n" +
                    "Adresse: " + dic.get("Adresse") + " " + dic.get("City") + ";\n" +
                    "Sortie: " + dic.get("Date") + " a " + dic.get("Time") + ";\n" +
                    "Motifs: " + context.getResources().getStringArray(R.array.popUp_motifs)[Integer.parseInt((String)dic.get("Motif"))] + ";");
            content.addImage(QR);
            QR.setAbsolutePosition(20, 350);
            QR.scalePercent(800);
            stamper.insertPage(2, PageSize.A4);
            stamper.getOverContent(2).addImage(QR);

            //for signature
            int sign_x = 120;
            int sign_y = 38;
            printOnPdf(content, "digitally signed by: " +  dic.get("Name"), new Rectangle(sign_x, sign_y, sign_x + 250, sign_y + 20));
            //for display
            form.setGenerateAppearances(true);
            stamper.setFormFlattening(true);
            stamper.close();
            dic.put("PDF", NewPDF);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Step 5: Close the document
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
    public static @NonNull Image createQR(String data) {
        Image qrcodeImage = null;
        try {
            BarcodeQRCode qrcode = new BarcodeQRCode(data, 1, 1, null);
            qrcodeImage = qrcode.getImage();
            qrcodeImage.setAbsolutePosition(450, 30);
            qrcodeImage.scalePercent(200);
        } catch (BadElementException e) {
            e.printStackTrace();
        }
        return qrcodeImage;
    }
}
