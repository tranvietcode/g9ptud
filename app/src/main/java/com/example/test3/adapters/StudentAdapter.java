package com.example.test3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.R;
import com.example.test3.models.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    public interface OnStudentActionListener {
        void onEdit(Student student);
        void onDelete(Student student);
    }

    private List<Student> list;
    private final OnStudentActionListener listener;

    public StudentAdapter(List<Student> list, OnStudentActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Student> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student item = list.get(position);

        holder.tvFullname.setText(item.getFullname());
        holder.tvStudentCode.setText(item.getStudentCode());
        holder.tvEmail.setText("✉️ Email: " + (item.getEmail() != null ? item.getEmail() : "N/A"));
        holder.tvPhone.setText("📞 SĐT: " + (item.getPhone() != null ? item.getPhone() : "N/A"));

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullname, tvStudentCode, tvEmail, tvPhone;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullname = itemView.findViewById(R.id.tvFullname);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
