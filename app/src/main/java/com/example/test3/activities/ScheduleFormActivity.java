package com.example.test3.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test3.R;
import com.example.test3.dal.DBHelper;
import com.example.test3.models.StudySchedule;
import com.example.test3.models.Subject;
import com.example.test3.utils.Constants;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleFormActivity extends AppCompatActivity {

    private TextView tvFormTitle;
    private Spinner spnSubject, spnStatus;
    private EditText edtStudyDate, edtStartTime, edtEndTime, edtLocation;
    private Button btnSave, btnCancel;

    private DBHelper dbHelper;
    private int loggedInStudentId = -1;
    private int editingScheduleId = -1;
    private StudySchedule currentSchedule = null;
    private List<Subject> subjectList;

    private final String[] statusArray = new String[]{
            Constants.STATUS_PENDING,
            Constants.STATUS_CONFIRMED,
            Constants.STATUS_CANCELLED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_form);

        dbHelper = new DBHelper(this);

        // Get logged-in student ID
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        loggedInStudentId = prefs.getInt(Constants.KEY_STUDENT_ID, -1);

        if (loggedInStudentId <= 0) {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên của tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvFormTitle = findViewById(R.id.tvFormTitle);
        spnSubject = findViewById(R.id.spnSubject);
        spnStatus = findViewById(R.id.spnStatus);
        edtStudyDate = findViewById(R.id.edtStudyDate);
        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
        edtLocation = findViewById(R.id.edtLocation);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Populate Status Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);

        // Populate Subject Spinner
        loadSubjects();

        // Date and Time Pickers
        setupDateAndTimePickers();

        // Check if Editing mode
        editingScheduleId = getIntent().getIntExtra(Constants.EXTRA_SCHEDULE_ID, -1);
        if (editingScheduleId > 0) {
            setupEditMode();
        } else {
            setupAddMode();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchedule();
            }
        });
    }

    private void loadSubjects() {
        subjectList = dbHelper.getAllSubjects();
        if (subjectList == null || subjectList.isEmpty()) {
            Toast.makeText(this, "Chưa có môn học nào trong hệ thống!", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter<Subject> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubject.setAdapter(subjectAdapter);
    }

    private void setupDateAndTimePickers() {
        edtStudyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        edtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(edtStartTime);
            }
        });

        edtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(edtEndTime);
            }
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                edtStudyDate.setText(formattedDate);
            }
        }, year, month, day);

        dialog.show();
    }

    private void showTimePicker(final EditText targetEditText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                targetEditText.setText(formattedTime);
            }
        }, hour, minute, true);

        dialog.show();
    }

    private void setupAddMode() {
        tvFormTitle.setText("THÊM LỊCH HỌC MỚI");
        // Default date: today
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        edtStudyDate.setText(today);
        spnStatus.setSelection(0); // Pending
    }

    private void setupEditMode() {
        tvFormTitle.setText("CHỈNH SỬA LỊCH HỌC");
        currentSchedule = dbHelper.getScheduleById(editingScheduleId);

        if (currentSchedule == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu lịch học!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check ownership: do not allow editing other student's schedule
        if (currentSchedule.getStudentId() != loggedInStudentId) {
            Toast.makeText(this, "Bạn không có quyền chỉnh sửa lịch của sinh viên khác!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Fill form fields
        edtStudyDate.setText(currentSchedule.getStudyDate());
        edtStartTime.setText(currentSchedule.getStartTime());
        edtEndTime.setText(currentSchedule.getEndTime());
        edtLocation.setText(currentSchedule.getLocation());

        // Select subject in Spinner
        if (subjectList != null) {
            for (int i = 0; i < subjectList.size(); i++) {
                if (subjectList.get(i).getId() == currentSchedule.getSubjectId()) {
                    spnSubject.setSelection(i);
                    break;
                }
            }
        }

        // Select status in Spinner
        for (int i = 0; i < statusArray.length; i++) {
            if (statusArray[i].equalsIgnoreCase(currentSchedule.getStatus())) {
                spnStatus.setSelection(i);
                break;
            }
        }
    }

    private void saveSchedule() {
        if (spnSubject.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show();
            return;
        }

        Subject selectedSubject = (Subject) spnSubject.getSelectedItem();
        String studyDate = edtStudyDate.getText().toString().trim();
        String startTime = edtStartTime.getText().toString().trim();
        String endTime = edtEndTime.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        // 1. Data presence validation
        if (studyDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin lịch học!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Time ordering validation: startTime < endTime
        if (startTime.compareTo(endTime) >= 0) {
            Toast.makeText(this, "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc!", Toast.LENGTH_LONG).show();
            return;
        }

        if (editingScheduleId > 0 && currentSchedule != null) {
            // Edit schedule
            currentSchedule.setSubjectId(selectedSubject.getId());
            currentSchedule.setStudyDate(studyDate);
            currentSchedule.setStartTime(startTime);
            currentSchedule.setEndTime(endTime);
            currentSchedule.setLocation(location);
            currentSchedule.setStatus(status);

            int result = dbHelper.updateSchedule(currentSchedule);
            if (result == -2) {
                Toast.makeText(this, "Lịch học bị trùng thời gian với lịch khác đã có!", Toast.LENGTH_LONG).show();
            } else if (result > 0) {
                Toast.makeText(this, "Cập nhật lịch học thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật lịch học!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add schedule
            StudySchedule newSchedule = new StudySchedule(
                    loggedInStudentId,
                    selectedSubject.getId(),
                    studyDate,
                    startTime,
                    endTime,
                    location,
                    status
            );

            long result = dbHelper.addSchedule(newSchedule);
            if (result == -2) {
                Toast.makeText(this, "Lịch học bị trùng thời gian với lịch khác đã có!", Toast.LENGTH_LONG).show();
            } else if (result > 0) {
                Toast.makeText(this, "Thêm lịch học thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm lịch học!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
