package com.example.classroomassingment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {

    private CollectionReference messageRef;
    private MessageAdapter adapter;
    private EditText messagewritten;
    private LinearLayout mylinear;
    private  String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent myintent = getIntent();
        final String classroomID = myintent.getStringExtra("classroomID");

        messagewritten = findViewById(R.id.message_written);
        mylinear = findViewById(R.id.mylinearlayout);

        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
        username = sharedPreferences.getString("username","");

        messageRef = FirebaseFirestore.getInstance().collection("Classroom").document(classroomID)
                .collection("Messages");

        final FloatingActionButton buttonAddQuestion = findViewById(R.id.button_add_note_message);
        buttonAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mylinear.setVisibility(View.VISIBLE);
            }
        });

        setUpRecyclerView();
    }


    private void setUpRecyclerView(){
        Query query = messageRef.orderBy("messgaedate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        adapter = new MessageAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    public void sendmessage(View view){

        Calendar c = Calendar.getInstance();
        Date mydate = c.getTime();
        Timestamp mytime = new Timestamp(mydate);

        String fewMessage = messagewritten.getText().toString();

        Message mymessage = new Message(fewMessage,username,mytime);

        messageRef.add(mymessage);
        mylinear.setVisibility(View.INVISIBLE);

        messagewritten.setText("");
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

    public void closewritetext(View view){
        mylinear.setVisibility(View.INVISIBLE);
    }
}