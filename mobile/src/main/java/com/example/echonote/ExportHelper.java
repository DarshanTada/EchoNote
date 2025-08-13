package com.example.echonote;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileOutputStream;

public class ExportHelper {

    private static final String TAG = "ExportHelper";

    /**
     * Export MOM summary to Word document.
     *
     * @param context Android context.
     * @param title   Title of the MOM.
     * @param content Content to export.
     */
    public static void exportToWord(Context context, String title, String content) {
        XWPFDocument document = new XWPFDocument();

        try {
            XWPFParagraph paragraphTitle = document.createParagraph();
            paragraphTitle.createRun().setText(title);
            paragraphTitle.createRun().setBold(true);
            paragraphTitle.createRun().addBreak();

            XWPFParagraph paragraphContent = document.createParagraph();
            paragraphContent.createRun().setText(content);

            // File path in external storage
            File dir = new File(Environment.getExternalStorageDirectory(), "EchoNoteExports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = title.replaceAll("\\W+", "_") + ".docx";
            File file = new File(dir, fileName);

            FileOutputStream out = new FileOutputStream(file);
            document.write(out);
            out.close();
            document.close();

            Toast.makeText(context, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(TAG, "Error exporting to Word", e);
            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}
