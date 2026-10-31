package com.example.test3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.R;
import com.example.test3.models.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    public interface OnSubjectActionListener {
        void onEdit(Subject subject);
        void onDelete(Subject subject);
    }

    private List<Subject> list;
    private final OnSubjectActionListener listener;

    public SubjectAdapter(List<Subject> list, OnSubjectActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Subject> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject item = list.get(position);

        holder.tvSubjectName.setText(item.getSubjectName());
        holder.tvSubjectCode.setText(item.getSubjectCode());
        holder.tvCredits.setText("🎖️ Số tín chỉ: " + item.getCredits());
        holder.tvDescription.setText("📝 Mô tả: " + (item.getDescription() != null ? item.getDescription() : "Không có mô tả"));

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
        TextView tvSubjectName, tvSubjectCode, tvCredits, tvDescription;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
