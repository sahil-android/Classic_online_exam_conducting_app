package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TeacherAssignmentActivity extends AppCompatActivity {

    private TeacherAssignmentAdapter adapter;
    private String profession;
    private CollectionReference assignmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_assignment);

        Intent myintent = getIntent();
        final String classroomID = myintent.getStringExtra("classroomID");

        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
        profession = sharedPreferences.getString("profession", "");


        assignmentRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Assignments");


        final FloatingActionButton buttonAddQuestion = findViewById(R.id.button_add_note_assign);
        buttonAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profession.toLowerCase().equals("teacher")){
                    Intent newIntent = new Intent(TeacherAssignmentActivity.this, WriteAssignmentActivity.class);
                    newIntent.putExtra("classroomid", classroomID);
                    startActivity(newIntent);
                }
                else{
                    buttonAddQuestion.setVisibility(View.INVISIBLE);
                }
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = assignmentRef.orderBy("assignmentDate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Assignments> options = new FirestoreRecyclerOptions.Builder<Assignments>()
                .setQuery(query, Assignments.class)
                .build();
        adapter = new TeacherAssignmentAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_assign);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TeacherAssignmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Assignments mycode = documentSnapshot.toObject(Assignments.class);
                String id = documentSnapshot.getId();

                if (profession.toLowerCase().equals("teacher")){
                    Intent answerIntent = new Intent(TeacherAssignmentActivity.this, NewAssignmentActivity.class);
                    answerIntent.putExtra("assignmentID", id);
                    startActivity(answerIntent);
                }
                else{
                    Intent answerIntent = new Intent(TeacherAssignmentActivity.this, StudentAssignmentActivity.class);
                    answerIntent.putExtra("assignmentID", id);
                    startActivity(answerIntent);
                }

            }
        });
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