package com.example.classroomassingment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Locale;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> {


    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageAdapter.MessageHolder holder, int position, @NonNull Message model) {

        holder.message.setText(model.getMessage());
        holder.name.setText(model.getNameofauthor());
        holder.time.setText(getDate(model.getMessgaedate()));

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

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_messages, parent, false);
        return new MessageAdapter.MessageHolder(v);
    }

    class MessageHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView name;
        TextView time;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message_sent);
            name = itemView.findViewById(R.id.message_name);
            time = itemView.findViewById(R.id.message_time);


        }
    }

}
