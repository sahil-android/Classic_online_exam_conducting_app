package com.example.classroomassingment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Locale;

public class TeacherAssignmentAdapter extends FirestoreRecyclerAdapter<Assignments, TeacherAssignmentAdapter.TeacherAssignmentHolder> {

    private TeacherAssignmentAdapter.OnItemClickListener listener;

    public TeacherAssignmentAdapter(@NonNull FirestoreRecyclerOptions<Assignments> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final TeacherAssignmentAdapter.TeacherAssignmentHolder holder, int position, @NonNull Assignments model) {

        holder.assignmentName.setText(model.getAssignmentName());
        holder.assignmentDate.setText(getDate(model.getAssignmentDate()));
        holder.assignmentDueDate.setText(getDate(model.getSubmissionDate()));
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
    public TeacherAssignmentAdapter.TeacherAssignmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_assignment, parent, false);
        return new TeacherAssignmentAdapter.TeacherAssignmentHolder(v);
    }

    class TeacherAssignmentHolder extends RecyclerView.ViewHolder{

        TextView assignmentName;
        TextView assignmentDate;
        TextView assignmentDueDate;

        public TeacherAssignmentHolder(@NonNull View itemView) {
            super(itemView);

            assignmentName = itemView.findViewById(R.id.assignment_Name);
            assignmentDate = itemView.findViewById(R.id.assign_date);
            assignmentDueDate = itemView.findViewById(R.id.assign_dueDate);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }
                }
            });

        }
    }
    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(TeacherAssignmentAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
