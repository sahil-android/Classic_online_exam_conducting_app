package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class NewAssignmentActivity extends AppCompatActivity {

    private NewAssignmetAdapter adapter;

    private CollectionReference studentAssignRef;

    private TextView submittedAssignName;
    private TextView submittedAssignTopic;
    private TextView submittedAssignComment;
    private TextView submittedAssignStartDate;
    private TextView submittedAssignSubmittedDate;
    private String assignID;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_assignment);

        submittedAssignName = findViewById(R.id.submitted_assignName);
        submittedAssignTopic = findViewById(R.id.submitted_assignTopic);
        submittedAssignComment = findViewById(R.id.submitted_comment);
        submittedAssignStartDate = findViewById(R.id.submitted_startdate);
        submittedAssignSubmittedDate = findViewById(R.id.submitted_enddate);
        pdfView = findViewById(R.id.submitted_pdfView);

        Intent myintent = getIntent();
        assignID = myintent.getStringExtra("assignmentID");

        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
        String classroomID = sharedPreferences.getString("classroomid", "");

        DocumentReference assignDocRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments").document(assignID);

        studentAssignRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments").document(assignID).collection("TakeAssignment");

        assignDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Assignments myassign = documentSnapshot.toObject(Assignments.class);
                        submittedAssignName.setText(myassign.getAssignmentName());
                        submittedAssignTopic.setText(myassign.getAssignmentTopic());
                        submittedAssignComment.setText(myassign.getCommentAssign());
                        submittedAssignStartDate.setText(getDate(myassign.getAssignmentDate()));
                        submittedAssignSubmittedDate.setText(getDate(myassign.getSubmissionDate()));
                    }
                });

        setUpRecyclerView();
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

    private void setUpRecyclerView(){
        Query query = studentAssignRef.orderBy("studentName", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<StudentAssignment> options = new FirestoreRecyclerOptions.Builder<StudentAssignment>()
                .setQuery(query, StudentAssignment.class)
                .build();
        adapter = new NewAssignmetAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_submittedpdf);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NewAssignmetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                StudentAssignment mycode = documentSnapshot.toObject(StudentAssignment.class);
                String id = documentSnapshot.getId();
                String downloadURL = mycode.getStudentDownloadurl();
                String studentpdfName = mycode.getPdfName();
                String dirpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"+ studentpdfName +".pdf";
                File myfile = new File(dirpath);

                if (myfile.exists()){
                    openPDF(studentpdfName);
                }else{
                    downloadPDF(downloadURL, studentpdfName);
                }

            }
        });
    }

    public  void downloadPDF(String downloadURL, final String studentpdfName) {

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

        int downloadId = PRDownloader.download(downloadURL, dirpath,  studentpdfName + ".pdf")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Log.i("download", "complete");

                        openPDF(studentpdfName);

                    }
                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    public void openPDF(String studentpdfName) {

        Storage storage = new Storage(getApplicationContext());
        String dirpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"+ studentpdfName +".pdf"; //"/storage/emulated/0/Android/data/com.example.classroomassingment/files/Download/yourfile.pdf";
        Log.i("my path ", dirpath);
        byte[] bytes = storage.readFile(dirpath);
        pdfView.setVisibility(View.VISIBLE);
        pdfView.fromBytes(bytes)
                .load();
    }

    public void closesubmittedPDF(View view){
        pdfView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}