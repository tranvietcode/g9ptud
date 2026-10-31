package com.example.test3.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionListener {

    private CalendarView calendarView;
    private TextView tvSelectedDate;
    private RecyclerView rvDateSchedules;
    private TextView tvNoSchedule;
    private Button btnBack;

    private DBHelper dbHelper;
    private ScheduleAdapter adapter;
    private List<StudySchedule> dateScheduleList = new ArrayList<>();
    private int loggedInStudentId = -1;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        loggedInStudentId = pref.getInt(Constants.KEY_STUDENT_ID, -1);

        dbHelper = new DBHelper(this);

        initViews();
        setupRecyclerView();

        // Default: selected date is today
        long todayMillis = calendarView.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date(todayMillis));
        tvSelectedDate.setText("Lịch học ngày: " + selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvSelectedDate.setText("Lịch học ngày: " + selectedDate);
            loadSchedulesForDate(selectedDate);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rvDateSchedules = findViewById(R.id.rvDateSchedules);
        tvNoSchedule = findViewById(R.id.tvNoSchedule);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        rvDateSchedules.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(dateScheduleList, this);
        rvDateSchedules.setAdapter(adapter);
    }

    private void loadSchedulesForDate(String date) {
        if (loggedInStudentId <= 0) {
            tvNoSchedule.setVisibility(View.VISIBLE);
            rvDateSchedules.setVisibility(View.GONE);
            return;
        }

        dateScheduleList = dbHelper.getSchedulesByStudentAndDate(loggedInStudentId, date);
        adapter.updateData(dateScheduleList);

        if (dateScheduleList.isEmpty()) {
            tvNoSchedule.setVisibility(View.VISIBLE);
            rvDateSchedules.setVisibility(View.GONE);
        } else {
            tvNoSchedule.setVisibility(View.GONE);
            rvDateSchedules.setVisibility(View.VISIBLE);
        }
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
                loadSchedulesForDate(selectedDate);

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
                loadSchedulesForDate(selectedDate);
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
        if (!selectedDate.isEmpty()) {
            loadSchedulesForDate(selectedDate);
        }
    }
}
