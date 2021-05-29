package com.example.classroomassingment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class StudentAssignmentActivity extends AppCompatActivity {

    private CollectionReference assignmentRef;
    private String assignID;
    private DocumentReference assignDocRef;
    private CollectionReference studentAssignRef;

    private TextView assignmentName;
    private TextView startDate;
    private TextView endDate;
    private TextView comment;
    private TextView topicassign;
    private String teacherFileName;
    private  TextView pdftext;
    private String downloadURL;
    private String studentDownloadurl;
    private  String studentName;
    private EditText studPDFname;
    private Button buttonsubmit;
    private Button downloadButton;
    private ImageView pdfImage;
    private Button openButton;
    private PDFView pdfView;
    private TextView giventime;
    Intent myintent;
    Intent mydata;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    UploadTask uploadTask;
    private File myfile;
    private  StudentAssignment mystudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment);

        PRDownloader.initialize(getApplicationContext());

        assignmentName = findViewById(R.id.stud_assignName);
        startDate = findViewById(R.id.stud_startdate);
        topicassign = findViewById(R.id.stud_assignTopic);
        endDate = findViewById(R.id.stud_enddate);
        comment = findViewById(R.id.stud_comment);
        pdftext = findViewById(R.id.stud_pdfText);
        pdfView = findViewById(R.id.stud_pdfView);
        studPDFname = findViewById(R.id.stud_pdf_name);
        buttonsubmit = findViewById(R.id.stud_submit);
        downloadButton = findViewById(R.id.downloadButton);
        openButton = findViewById(R.id.openButton);
        pdfImage = findViewById(R.id.stud_pdf_image);
        giventime = findViewById(R.id.givenTime);

        Intent myintent = getIntent();
        assignID = myintent.getStringExtra("assignmentID");

        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
        String classroomID = sharedPreferences.getString("classroomid", "");
        studentName = sharedPreferences.getString("username","");

        assignDocRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments").document(assignID);

        assignmentRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments").document(assignID).collection("TakeAssignment");

        studentAssignRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments").document(assignID).collection(studentName);

        studentAssignRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            mystudent = documentSnapshot.toObject(StudentAssignment.class);
                        }

                        if (mystudent != null)
                        if (mystudent.getCompleted()){
                            buttonsubmit.setText("completed");
                            buttonsubmit.setBackgroundColor(4);
                            buttonsubmit.setEnabled(false);
                            studPDFname.setEnabled(false);
                        }
                    }
                });

        assignDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Assignments myassign = documentSnapshot.toObject(Assignments.class);
                        assignmentName.setText(myassign.getAssignmentName());
                        topicassign.setText(myassign.getAssignmentTopic());
                        startDate.setText(getDate(myassign.getAssignmentDate()));
                        endDate.setText(getDate(myassign.getSubmissionDate()));
                        comment.setText(myassign.getCommentAssign());
                        pdftext.setText(myassign.getPdfName());
                        teacherFileName = myassign.getPdfName();
                        downloadURL = myassign.getDownloadURL();

                        Timestamp setTime = myassign.getSubmissionDate();
                        String setyourtime = setTime.toString();
                        String setseconds = setyourtime.substring(setyourtime.indexOf("=") + 1, setyourtime.indexOf(","));
                        long setlongtime = Long.parseLong(setseconds);

                        Timestamp getTime = myassign.getAssignmentDate();
                        String getyourtime = getTime.toString();
                        String getseconds = getyourtime.substring(getyourtime.indexOf("=") + 1, getyourtime.indexOf(","));
                        long getlongtime = Long.parseLong(getseconds);

                        Calendar cu = Calendar.getInstance(Locale.ENGLISH);
                        Date currentdate = cu.getTime();
                        Timestamp mytime = new Timestamp(currentdate);
                        String yourtime = mytime.toString();
                        String seconds = yourtime.substring(yourtime.indexOf("=") + 1, yourtime.indexOf(","));
                        long time = Long.parseLong(seconds);

                        if (time < getlongtime){
                            buttonsubmit.setEnabled(false);
                            downloadButton.setEnabled(false);
                            openButton.setEnabled(false);
                        }

                        if (time > setlongtime){
                            giventime.setVisibility(View.VISIBLE);
                            buttonsubmit.setEnabled(false);
                        }

                        myfile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"+ teacherFileName +".pdf");

                        if (myfile.exists()){
                            downloadButton.setVisibility(View.INVISIBLE);
                            openButton.setVisibility(View.VISIBLE);

                        }
                    }
                });
    }

    public  String getDate(Timestamp infotime){
        if(infotime != null) {
            String mytime = infotime.toString();
            String seconds = mytime.substring(mytime.indexOf("=") + 1, mytime.indexOf(","));
            long time = Long.parseLong(seconds);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time * 1000);
            return DateFormat.format("dd-MM-yyyy  h:mm a", cal).toString();

        }
        return  null;
    }

    public  void downloadPDF(View view) {

        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

// Setting timeout globally for the download network requests:
        Log.i("download", downloadURL);

        String dirpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

        Log.i("path of Download", dirpath);

        int downloadId = PRDownloader.download(downloadURL, dirpath,  teacherFileName + ".pdf")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Log.i("download", "complete");
                        downloadButton.setVisibility(View.INVISIBLE);
                        openButton.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    public void openPDF(View view) {

        Storage storage = new Storage(getApplicationContext());
        String dirpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"+ teacherFileName +".pdf"; //"/storage/emulated/0/Android/data/com.example.classroomassingment/files/Download/yourfile.pdf";
        Log.i("my path ", dirpath);
        byte[] bytes = storage.readFile(dirpath);
        pdfView.setVisibility(View.VISIBLE);
        pdfView.fromBytes(bytes)
                .load();
    }

    public void studclosepdf(View view){
        pdfView.setVisibility(View.INVISIBLE);
    }

    public void studopenpdf(View view){
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
                    pdfImage.setImageResource(R.drawable.ic_pdf);
                }
                break;
        }
    }

    public void submit(View view){

        final String studentpdf = studPDFname.getText().toString();

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
                    studentDownloadurl = downloadUri.toString();
                    Log.i("download url", downloadUri.toString());

                    Calendar c = Calendar.getInstance(Locale.ENGLISH);
                    Date date = c.getTime();
                    Timestamp mytime = new Timestamp(date);
                    StudentAssignment student = new StudentAssignment(studentDownloadurl,studentName,studentpdf,true, mytime);
                    assignmentRef.add(student);
                    studentAssignRef.add(student);

                    buttonsubmit.setText("completed");
                    buttonsubmit.setBackgroundColor(4);

                    finish();

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

}