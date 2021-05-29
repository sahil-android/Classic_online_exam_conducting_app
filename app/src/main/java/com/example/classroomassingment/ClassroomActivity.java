package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ClassroomActivity extends AppCompatActivity {

    private CollectionReference classrooomRef = FirebaseFirestore.getInstance()
            .collection("Classroom");

    private CollectionReference classCodeRef;

    private ClassroomAdapter adapter;

    private DocumentReference oneUserRef;

    private String userName;

    private String profession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Intent myintent = getIntent();
        final String userIndetity = myintent.getStringExtra("userID");

        oneUserRef = FirebaseFirestore.getInstance().collection("Users").document(userIndetity);

        oneUserRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User newuser = documentSnapshot.toObject(User.class);
                        userName = newuser.getName();
                        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserIdentity",userIndetity);
                        editor.putString("profession", newuser.getProfession());
                        editor.putString("username", userName);
                        editor.apply();

                        profession = newuser.getProfession();
                    }
                });



        Log.i("useridentity", userIndetity);

        classCodeRef = FirebaseFirestore.getInstance().collection("Users").document(userIndetity).collection("classCode");

        FloatingActionButton buttonAddQuestion = findViewById(R.id.button_add_note);
        buttonAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profession.toLowerCase().equals("teacher")){
                    Intent newIntent = new Intent(ClassroomActivity.this, WriteClassroomActivity.class);
                    newIntent.putExtra("userID", userIndetity);
                    startActivity(newIntent);
                }
                else{
                    Intent newIntent = new Intent(ClassroomActivity.this, AddClassroomActivity.class);
                    newIntent.putExtra("userID", userIndetity);
                    startActivity(newIntent);
                }

            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = classCodeRef.orderBy("classroomTeacher", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ClassCode> options = new FirestoreRecyclerOptions.Builder<ClassCode>()
                .setQuery(query, ClassCode.class)
                .build();
        adapter = new ClassroomAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new ClassroomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ClassCode mycode = documentSnapshot.toObject(ClassCode.class);
                String id = mycode.getClassroomID();
                SharedPreferences sharedPreferences = getSharedPreferences("SHARED PRE", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("classId",id);
                editor.apply();
                Intent answerIntent = new Intent(ClassroomActivity.this, ClassActivity.class);
                answerIntent.putExtra("id", id);
                startActivity(answerIntent);
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