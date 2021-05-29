package com.example.classroomassingment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.UUID;

public class SecondActivity extends AppCompatActivity {

    PDFView pdfView;
    Intent myintent;
    Intent mydata;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    UploadTask uploadTask;
    Uri myUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        PRDownloader.initialize(getApplicationContext());
        pdfView = findViewById(R.id.pdfView);

    }

    public void load(View view){
        myintent = new Intent(Intent.ACTION_GET_CONTENT);
        myintent.setType("application/pdf");
        startActivityForResult(myintent, 10);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode==RESULT_OK){
                    assert data != null;
                    pdfView.fromUri(data.getData())
                            .load();
                    mydata = data;

                }
                break;
        }
    }

    public void uploadtask(View view){

        String path = "firememes/" + UUID.randomUUID() + ".pdf";
        final StorageReference firememeRef = storage.getReference(path);

        uploadTask = firememeRef.putFile((mydata.getData()));

        Log.i("path", mydata.getData().getPath());

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //contains metadata
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("exception", e.toString());
            }
        });


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return firememeRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    myUri = downloadUri;
                    Log.i("download url", downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
    public  void download(View view) {

        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

// Setting timeout globally for the download network requests:
        Log.i("download", myUri.toString());

        File file = new File(Environment.getExternalStorageDirectory(),
                "myfileme.pdf");



        String dirpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

        Log.i("path of Download", dirpath);

        int downloadId = PRDownloader.download(myUri.toString(), dirpath,"yourfile.pdf")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Log.i("download", "complete");

                    }
                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    public void openfile(View view) {

        Storage storage = new Storage(getApplicationContext());
        String dirpath = "/storage/emulated/0/Android/data/com.example.classroomassingment/files/Download/yourfile.pdf";//getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/yourfile.pdf";
        Log.i("my path ", dirpath);
        byte[] bytes = storage.readFile(dirpath);

        pdfView.fromBytes(bytes)
                .load();

    }

}