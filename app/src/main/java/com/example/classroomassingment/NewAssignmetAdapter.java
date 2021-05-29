package com.example.classroomassingment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class NewAssignmetAdapter extends FirestoreRecyclerAdapter<StudentAssignment, NewAssignmetAdapter.NewAssignmentHolder> {

    private NewAssignmetAdapter.OnItemClickListener listener;

    public NewAssignmetAdapter(@NonNull FirestoreRecyclerOptions<StudentAssignment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NewAssignmetAdapter.NewAssignmentHolder holder, int position, @NonNull StudentAssignment model) {

        holder.studentName.setText(model.getStudentName());
        holder.pdfnamesubmitted.setText(model.getPdfName());

    }

    @NonNull
    @Override
    public NewAssignmetAdapter.NewAssignmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.studentpdfview, parent, false);
        return new NewAssignmetAdapter.NewAssignmentHolder(v);
    }

    class NewAssignmentHolder extends RecyclerView.ViewHolder{

        TextView studentName;
        TextView pdfnamesubmitted;
        Button openpdfbutton;

        public NewAssignmentHolder(@NonNull View itemView) {
            super(itemView);

            studentName = itemView.findViewById(R.id.student_name);
            pdfnamesubmitted = itemView.findViewById(R.id.submitted_pdfname);
            openpdfbutton = itemView.findViewById(R.id.opensubmitpdf);


            openpdfbutton.setOnClickListener(new View.OnClickListener() {
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
    public void setOnItemClickListener(NewAssignmetAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

}
