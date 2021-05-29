package com.example.classroomassingment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClassroomAdapter extends FirestoreRecyclerAdapter<ClassCode, ClassroomAdapter.ClassroomHolder>{

    private OnItemClickListener listener;

    public ClassroomAdapter(@NonNull FirestoreRecyclerOptions<ClassCode> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ClassroomHolder holder, int position, @NonNull ClassCode model) {

        holder.textViewSubject.setText(model.getClassroomSubject());
        holder.textViewTeacher.setText(model.getClassroomTeacher());

    }

    @NonNull
    @Override
    public ClassroomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_classroom, parent, false);
        return new ClassroomHolder(v);
    }

    class ClassroomHolder extends RecyclerView.ViewHolder{

        TextView textViewSubject;
        TextView textViewTeacher;

        public ClassroomHolder(@NonNull View itemView) {
            super(itemView);

            textViewSubject = itemView.findViewById(R.id.subjectTextView);
            textViewTeacher = itemView.findViewById(R.id.teacherTextView);

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
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
