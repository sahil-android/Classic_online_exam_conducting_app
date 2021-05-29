package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class WriteClassroomActivity extends AppCompatActivity {

    private EditText subject;
    private EditText teacher;
    private EditText batch;
    private EditText course;
    private EditText classCode;
    private  CollectionReference classCodeRef;
    private CollectionReference clasroomRef = FirebaseFirestore.getInstance()
            .collection("Classroom");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_classroom);

        subject = findViewById(R.id.classSubject);
        teacher = findViewById(R.id.classTeacher);
        batch = findViewById(R.id.classBatch);
        course = findViewById(R.id.classCourse);
        classCode = findViewById(R.id.classCode);

        Intent myintent = getIntent();
        String userIndetity = myintent.getStringExtra("userID");

        //SharedPreferences myshare = getSharedPreferences("SHARED PRE", MODE_PRIVATE);
        //final String userIndetity =  myshare.getString("userID", "bas");

        Log.i("Myuser", userIndetity);

        classCodeRef = FirebaseFirestore.getInstance().collection("Users")
                .document(userIndetity).collection("classCode");
    }

    public void createClass(View view){

        final String mySubject = subject.getText().toString();
        final String myteacher = teacher.getText().toString();
        final String myBatch = batch.getText().toString();
        final String myCourse = course.getText().toString();
        final String myCode = classCode.getText().toString();

        Classroom myclassroom = new Classroom(mySubject, myteacher, myBatch, myCourse, myCode);
        clasroomRef.add(myclassroom);

        clasroomRef.whereEqualTo("classroomCode", myCode).limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    String myID;
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            myID = documentSnapshot.getId();
                        }

                        ClassCode myclasscode = new ClassCode(myID, myCode, mySubject,myteacher,myBatch,myCourse);
                        classCodeRef.add(myclasscode);
                    }
                });

        finish();
    }
}