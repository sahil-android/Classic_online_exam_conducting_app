package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AddClassroomActivity extends AppCompatActivity {

    private  String userID;
    private EditText classCode;
    private CollectionReference classroomRef = FirebaseFirestore.getInstance().collection("Classroom");
    private CollectionReference classcodeRef;
    private String classroomID;
    private Classroom helloclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classroom);

        Intent myintent = getIntent();
        userID = myintent.getStringExtra("userID");

        classCode = findViewById(R.id.mycode_class);

        classcodeRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("classCode");

    }

    public void checkCode(View view){

        String code = classCode.getText().toString();

        classroomRef.whereEqualTo("classroomCode", code)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            helloclass = documentSnapshot.toObject(Classroom.class);
                            classroomID = documentSnapshot.getId();

                        }

                        ClassCode myclass = new ClassCode(classroomID,helloclass.getClassroomCode(),
                                helloclass.getSubject(), helloclass.getTeacher(), helloclass.getBatch(), helloclass.getCourse());

                        classcodeRef.add(myclass);

                        finish();

                    }
                });

    }

}