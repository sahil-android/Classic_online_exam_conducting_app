package com.example.classroomassingment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class WriteAssignmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private EditText assignmentName;
    private EditText assignmetTopic;
    private EditText assignmentComment;
    private EditText pdfName;
    private TextView startDate;
    private TextView endDate;
    private ImageView pdfImage;
    private Date start;
    private Date end;
    private Button startButton;
    private CardView mycardview;
    private  Button endButton;
    private Button createButton;
    private Button openPDFbutton;
    private Calendar c = Calendar.getInstance(Locale.ENGLISH);
    private Calendar cStart =  Calendar.getInstance(Locale.ENGLISH);
    private Calendar cEnd = Calendar.getInstance(Locale.ENGLISH);
    PDFView pdfView;
    Intent myintent;
    Intent mydata;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    UploadTask uploadTask;
    Uri myUri;
    private CollectionReference assignRef;
    private  int k=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_assignment);

        assignmentName = findViewById(R.id.assign_name);
        assignmetTopic = findViewById(R.id.topic_assign);
        assignmentComment = findViewById(R.id.comment_assign);
        pdfName = findViewById(R.id.pdf_name);
        startDate = findViewById(R.id.start_Date);
        endDate = findViewById(R.id.end_Date);
        pdfImage = findViewById(R.id.pdf_image);
        pdfView = findViewById(R.id.pdf_view);
        mycardview = findViewById(R.id.cardView_ass);
        createButton = findViewById(R.id.createButton);
        openPDFbutton = findViewById(R.id.openpdfButton);
        startButton = findViewById(R.id.mycalendStart);
        endButton = findViewById(R.id.mycalendEnd);
        Intent myintent = getIntent();
        final String classroomID = myintent.getStringExtra("classroomid");

        assignRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments");
    }

    public void buttonStartdate(View view){

        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");

        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");

    }

    public void buttonEnddate(View view){

        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");

        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");

    }

    public void open_pdf(View view){

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
                    pdfView.setVisibility(View.VISIBLE);
                    pdfView.fromUri(data.getData())
                            .load();
                    createButton.setVisibility(View.INVISIBLE);
                    openPDFbutton.setVisibility(View.INVISIBLE);
                    startButton.setVisibility(View.INVISIBLE);
                    endButton.setVisibility(View.INVISIBLE);
                    mycardview.setVisibility(View.INVISIBLE);
                    mydata = data;
                    pdfImage.setImageResource(R.drawable.ic_pdf);
                }
                break;
        }
    }

    public void createAssign(View view){

        final String name = assignmentName.getText().toString();
        final String topic = assignmetTopic.getText().toString();
        final String comment = assignmentComment.getText().toString();
        final String pdfmame = pdfName.getText().toString();
        final Timestamp startStamp = new Timestamp(start);
        final Timestamp endStamp = new Timestamp(end);

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

                    Assignments myassign = new Assignments(name, startStamp, endStamp, comment, topic, myUri.toString(), pdfmame);
                    assignRef.add(myassign);

                    finish();

                    Toast.makeText(WriteAssignmentActivity.this, "Assignment Created", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    public void closePdf(View view){

        pdfView.setVisibility(View.INVISIBLE);
        openPDFbutton.setVisibility(View.VISIBLE);
        createButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        endButton.setVisibility(View.VISIBLE);
        mycardview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH, i1);
        c.set(Calendar.DAY_OF_MONTH, i2);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        c.set(Calendar.HOUR_OF_DAY, i);
        c.set(Calendar.MINUTE, i1);

        if(k%2 == 1){
            cStart = c;

            String formatdate = DateFormat.format("dd-MM-yyyy  h:mm a", cStart).toString();
            startDate.setText(formatdate);
            start = cStart.getTime();

            k++;
            Log.i("cStart", cStart.getTime().toString());
            Log.i("cEnd", cEnd.getTime().toString());
        }
        else if(k%2 ==0){
            cEnd = c;

            String formatdate = DateFormat.format("dd-MM-yyyy  h:mm a", cEnd).toString();
            endDate.setText(formatdate);
            end = cEnd.getTime();
            k++;
            Log.i("cStart1", cStart.getTime().toString());
            Log.i("cEnd1", cEnd.getTime().toString());
        }
    }
}