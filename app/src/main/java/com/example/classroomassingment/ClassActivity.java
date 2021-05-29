package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClassActivity extends AppCompatActivity {

    private DocumentReference classRef;
    private String classroomID;
    private TextView subjectTextview;
    private TextView teacherTextview;
    private TextView codeTextview;
    private String profesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        subjectTextview = findViewById(R.id.subjectText);
        teacherTextview = findViewById(R.id.teacherText);
        codeTextview = findViewById(R.id.class_roomcode);


        Intent myintent = getIntent();
        classroomID = myintent.getStringExtra("id");
        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("classroomid",classroomID);
        editor.apply();

        classRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID);

        classRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String subject = documentSnapshot.getString("subject");
                        String teacher = documentSnapshot.getString("teacher");

                        subjectTextview.setText(subject);
                        teacherTextview.setText(teacher);
                        codeTextview.setText( "Class Code- "   + documentSnapshot.getString("classroomCode"));
                    }
                });

    }

    public void messageButton(View view){

        Intent newIntent = new Intent(ClassActivity.this, MessageActivity.class);
        newIntent.putExtra("classroomID", classroomID);
        startActivity(newIntent);

    }
    public void assignmentButton(View view){

        Intent newIntent = new Intent(ClassActivity.this, TeacherAssignmentActivity.class);
        newIntent.putExtra("classroomID", classroomID);
        startActivity(newIntent);
    }

}