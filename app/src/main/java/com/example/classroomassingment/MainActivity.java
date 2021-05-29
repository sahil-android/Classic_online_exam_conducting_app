package com.example.classroomassingment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private LinearLayout signupLayout;
    private  LinearLayout loginLayout;
    private EditText emailSignup;
    private EditText nameSignup;
    private EditText profSignup;
    private EditText passwordSignup;
    private String userID;
    private String userProf;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");

    /*PDFView pdfView;
    Intent myintent;
    Intent mydata;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    UploadTask uploadTask;
    Uri myUri;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        emailSignup = findViewById(R.id.signupEmail);
        nameSignup = findViewById(R.id.signupName);
        profSignup = findViewById(R.id.signupProf);
        passwordSignup = findViewById(R.id.signupPassword);
        loginLayout = findViewById(R.id.loginLayout);
        signupLayout = findViewById(R.id.signupLayout);

        //PRDownloader.initialize(getApplicationContext());
        //pdfView = findViewById(R.id.pdfView);
    }

    public void login(View view){

        String mymail = email.getText().toString();
        String mypassword = password.getText().toString();

        mAuth.signInWithEmailAndPassword(mymail, mypassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i("User", user.getEmail());
                            userRef.whereEqualTo("emailID", user.getEmail())
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {


                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                userID = documentSnapshot.getId();


                                            }
                                            Log.i("userID",userID);
                                            Toast.makeText(MainActivity.this, userID, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this, ClassroomActivity.class);
                                            intent.putExtra("userID",userID);
                                            startActivity(intent);
                                        }

                                    });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    public void signupPage(View view){
        loginLayout.setVisibility(View.INVISIBLE);
        signupLayout.setVisibility(View.VISIBLE);
    }

    public void signup(View view){

        final String mymail = emailSignup.getText().toString();
        String mypassword = passwordSignup.getText().toString();
        final String myName = nameSignup.getText().toString();
        final String myProf = profSignup.getText().toString();

        mAuth.createUserWithEmailAndPassword(mymail, mypassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Signup", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i("User", user.getEmail());
                            User newUser = new User(myName,myProf,mymail);
                            userRef.add(newUser);

                            userRef.whereEqualTo("emailID", user.getEmail())
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                        String userID;
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                userID = documentSnapshot.getId();
                                                userProf = documentSnapshot.getString("profession");
                                            }
                                            Log.i("userID",userID);
                                            Intent intent = new Intent(MainActivity.this, ClassroomActivity.class);
                                            intent.putExtra("userID",userID);
                                            startActivity(intent);
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Signup", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /*public void load(View view){
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

    }*/

}