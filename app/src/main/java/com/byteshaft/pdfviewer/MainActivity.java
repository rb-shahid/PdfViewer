package com.byteshaft.pdfviewer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byteshaft.pdfviewer.utils.Helpers;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;


public class MainActivity extends AppCompatActivity implements OnDrawListener, OnLoadCompleteListener, OnPageChangeListener, View.OnClickListener {

    private PDFView mPdfView;
    private final int RESULT_CODE = 100;
    private static boolean isFileChooserShown = false;
    private Button pagesDetailsTextView;
    private static File currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPdfView = (PDFView) findViewById(R.id.pdfview);
        pagesDetailsTextView = (Button) findViewById(R.id.page_details);
        pagesDetailsTextView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helpers.getPreviousSavedFile().equals("") && !isFileChooserShown) {
            showFileChooser();
            isFileChooserShown = true;
        } else {
            loadPdfFile(Helpers.getPreviousSavedFile());
        }

    }

    private void loadPdfFile(String path) {
        File file = new File(path);
        currentFile = file;
        mPdfView.fromFile(file)
                .defaultPage(1)
                .showMinimap(true)
                .enableSwipe(true)
                .onDraw(this)
                .onLoad(this)
                .onPageChange(this)
                .load();
        pagesDetailsTextView.setText(mPdfView.getCurrentPage() + "/" + mPdfView.getPageCount());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Helpers.saveCurrentPage(currentFile.getName(), mPdfView.getCurrentPage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Helpers.saveCurrentPage(currentFile.getName(), mPdfView.getCurrentPage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE && data != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            loadPdfFile(path);
            Helpers.savePreviousOpenedFile(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.open_file_chooser)   {
            showFileChooser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    RESULT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {
        System.out.println(Helpers.getLastLoadedPage(currentFile.getName()));
        if (Helpers.getLastLoadedPage(currentFile.getName()) != 0) {
            mPdfView.jumpTo(Helpers.getLastLoadedPage(currentFile.getName())+1);
        }

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pagesDetailsTextView.setText(page+"/"+pageCount);

    }

    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.page_details:
                 showAlertDialog(MainActivity.this);
                 break;
         }
    }

    public void showAlertDialog(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Move to page");
        alertDialog.setMessage("Enter page number");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
//        alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int num = 0;
                        String text = input.getText().toString();
                        try {
                            num = Integer.parseInt(text);
                            Log.i("", num + " is a number");
                        } catch (NumberFormatException e) {
                            Log.i("",text+"is not a number");
                            Toast.makeText(AppGlobals.getContext(), "please enter a valid number", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        if (num <= mPdfView.getPageCount()) {
                            mPdfView.jumpTo(num);
                        } else {
                            Toast.makeText(getApplicationContext(), "page limit exceeded", Toast.LENGTH_SHORT).show();

                        }
                        dialog.dismiss();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}

