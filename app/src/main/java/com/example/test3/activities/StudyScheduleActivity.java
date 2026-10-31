package com.example.test3.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.R;
import com.example.test3.adapters.ScheduleAdapter;
import com.example.test3.dal.DBHelper;
import com.example.test3.models.StudySchedule;
import com.example.test3.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class StudyScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionListener {

    private EditText etSearch;
    private Button btnOpenCalendar;
    private RecyclerView rvSchedules;
    private TextView tvEmpty;

    private DBHelper dbHelper;
    private ScheduleAdapter adapter;
    private List<StudySchedule> scheduleList = new ArrayList<>();
    private int currentStudentId = 1; // Default hoặc đọc từ SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule);

        // Lấy studentId từ SharedPreferences
        SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentStudentId = pref.getInt("KEY_STUDENT_ID", 1);

        dbHelper = new DBHelper(this);

        initViews();
        setupRecyclerView();
        loadSchedules();
        setupSearch();

        btnOpenCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(StudyScheduleActivity.this, CalendarActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnOpenCalendar = findViewById(R.id.btnOpenCalendar);
        rvSchedules = findViewById(R.id.rvSchedules);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(scheduleList, this);
        rvSchedules.setAdapter(adapter);
    }

    private void loadSchedules() {
        scheduleList = dbHelper.getSchedulesByStudentId(currentStudentId);
        adapter.updateData(scheduleList);

        if (scheduleList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);

            // Đặt lịch thông báo nhắc nhở các buổi học sắp tới
            for (StudySchedule schedule : scheduleList) {
                if ("Confirmed".equalsIgnoreCase(schedule.getStatus())) {
                    NotificationHelper.scheduleUpcomingAlarm(this, schedule);
                }
            }
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    loadSchedules();
                } else {
                    scheduleList = dbHelper.searchSchedules(currentStudentId, keyword);
                    adapter.updateData(scheduleList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onChangeStatus(StudySchedule schedule) {
        String[] statuses = {"Pending", "Confirmed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn trạng thái mới cho lịch học");
        builder.setItems(statuses, (dialog, which) -> {
            String selectedStatus = statuses[which];
            boolean success = dbHelper.updateScheduleStatus(schedule.getId(), selectedStatus);
            if (success) {
                Toast.makeText(this, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                loadSchedules();

                // Bắn thông báo thay đổi lịch
                String subjectName = schedule.getSubjectName() != null ? schedule.getSubjectName() : "Môn học";
                NotificationHelper.sendNotification(this,
                        "Thay đổi trạng thái lịch học",
                        "Lịch học " + subjectName + " (" + schedule.getStudyDate() + ") đã chuyển sang " + selectedStatus);
            } else {
                Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    public void onDelete(StudySchedule schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa lịch học môn này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = dbHelper.deleteSchedule(schedule.getId());
            if (success) {
                Toast.makeText(this, "Đã xóa lịch học!", Toast.LENGTH_SHORT).show();
                loadSchedules();
            } else {
                Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }
}
