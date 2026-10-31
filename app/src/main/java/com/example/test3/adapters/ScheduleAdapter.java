package com.example.test3.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.R;
import com.example.test3.models.StudySchedule;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    public interface OnScheduleActionListener {
        void onChangeStatus(StudySchedule schedule);
        void onDelete(StudySchedule schedule);
    }

    private List<StudySchedule> list;
    private final OnScheduleActionListener listener;

    public ScheduleAdapter(List<StudySchedule> list, OnScheduleActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<StudySchedule> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudySchedule item = list.get(position);

        holder.tvSubjectName.setText(item.getSubjectName() != null ? item.getSubjectName() : "Môn học #" + item.getSubjectId());
        holder.tvSubjectCode.setText("Mã môn: " + (item.getSubjectCode() != null ? item.getSubjectCode() : "N/A"));
        holder.tvDate.setText("📅 Ngày: " + item.getStudyDate());
        holder.tvTime.setText("⏰ " + item.getStartTime() + " - " + item.getEndTime());
        holder.tvLocation.setText("📍 Địa điểm: " + item.getLocation());
        holder.tvStatus.setText(item.getStatus());

        // Định dạng màu trạng thái
        if ("Confirmed".equalsIgnoreCase(item.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else if ("Pending".equalsIgnoreCase(item.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#E65100"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
        } else { // Cancelled
            holder.tvStatus.setTextColor(Color.parseColor("#C62828"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }

        holder.btnChangeStatus.setOnClickListener(v -> {
            if (listener != null) listener.onChangeStatus(item);
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
        TextView tvSubjectName, tvSubjectCode, tvDate, tvTime, tvLocation, tvStatus;
        Button btnChangeStatus, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnChangeStatus = itemView.findViewById(R.id.btnChangeStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
