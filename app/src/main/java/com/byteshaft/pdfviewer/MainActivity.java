package com.byteshaft.pdfviewer;

import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.pdfviewer.utils.Helpers;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;


public class MainActivity extends AppCompatActivity implements OnDrawListener, OnLoadCompleteListener, OnPageChangeListener {

    private PDFView pdfView;
    private final int RESULT_CODE = 100;
    private TextView pagesDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pdfView = (PDFView) findViewById(R.id.pdfview);
        pagesDetailsTextView = (TextView) findViewById(R.id.page_details);
        if (Helpers.getPreviousSavedFile().equals("")) {
            showFileChooser();
        } else {
            loadPdfFile(Helpers.getPreviousSavedFile());
        }

    }

    private void loadPdfFile(String path) {
        pdfView.fromFile(new File(path))
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(this)
                .onLoad(this)
                .onPageChange(this)
                .load();
        pagesDetailsTextView.setText(pdfView.getCurrentPage()+"/"+pdfView.getPageCount());
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

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pagesDetailsTextView.setText(page+"/"+pageCount);

    }
}

