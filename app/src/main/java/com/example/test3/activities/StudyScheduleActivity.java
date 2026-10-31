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
import com.example.test3.utils.Constants;
import com.example.test3.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class StudyScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionListener {

    private EditText etSearch;
    private Button btnAddSchedule, btnOpenCalendar, btnBack;
    private RecyclerView rvSchedules;
    private TextView tvEmpty;

    private DBHelper dbHelper;
    private ScheduleAdapter adapter;
    private List<StudySchedule> scheduleList = new ArrayList<>();
    private int loggedInStudentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule);

        dbHelper = new DBHelper(this);

        // Get studentId from SharedPreferences
        SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        loggedInStudentId = pref.getInt(Constants.KEY_STUDENT_ID, -1);

        initViews();
        setupRecyclerView();
        setupSearch();

        btnAddSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(StudyScheduleActivity.this, ScheduleFormActivity.class);
            startActivity(intent);
        });

        btnOpenCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(StudyScheduleActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnOpenCalendar = findViewById(R.id.btnOpenCalendar);
        btnBack = findViewById(R.id.btnBack);
        rvSchedules = findViewById(R.id.rvSchedules);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(scheduleList, this);
        rvSchedules.setAdapter(adapter);
    }

    private void loadSchedules() {
        if (loggedInStudentId <= 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
            return;
        }

        scheduleList = dbHelper.getSchedulesByStudentId(loggedInStudentId);
        adapter.updateData(scheduleList);

        if (scheduleList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);

            // Schedule upcoming session notifications
            for (StudySchedule schedule : scheduleList) {
                if (Constants.STATUS_CONFIRMED.equalsIgnoreCase(schedule.getStatus())) {
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
                    scheduleList = dbHelper.searchSchedules(loggedInStudentId, keyword);
                    adapter.updateData(scheduleList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onChangeStatus(StudySchedule schedule) {
        String[] statuses = {Constants.STATUS_PENDING, Constants.STATUS_CONFIRMED, Constants.STATUS_CANCELLED};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn trạng thái mới cho lịch học");
        builder.setItems(statuses, (dialog, which) -> {
            String selectedStatus = statuses[which];
            boolean success = dbHelper.updateScheduleStatus(schedule.getId(), loggedInStudentId, selectedStatus);
            if (success) {
                Toast.makeText(this, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                loadSchedules();

                String subjectName = schedule.getSubjectName() != null ? schedule.getSubjectName() : "Môn học";
                NotificationHelper.sendNotification(this,
                        "Thay đổi trạng thái lịch học",
                        "Lịch học " + subjectName + " (" + schedule.getStudyDate() + ") đã chuyển sang " + selectedStatus);
            } else {
                Toast.makeText(this, "Không thể thay đổi trạng thái (có thể do trùng lịch)!", Toast.LENGTH_LONG).show();
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
            boolean success = dbHelper.deleteSchedule(schedule.getId(), loggedInStudentId);
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
