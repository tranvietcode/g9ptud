package com.example.test3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test3.activities.CalendarActivity;
import com.example.test3.activities.LoginActivity;
import com.example.test3.activities.ScheduleFormActivity;
import com.example.test3.activities.StudentActivity;
import com.example.test3.activities.StudyScheduleActivity;
import com.example.test3.activities.SubjectActivity;
import com.example.test3.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvRoleInfo;
    private LinearLayout layoutAdminOptions, layoutStudentOptions;
    private Button btnManageStudents, btnManageSubjects;
    private Button btnAddSchedule, btnViewSchedules, btnViewCalendar;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session check
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        int accountId = prefs.getInt(Constants.KEY_ACCOUNT_ID, -1);

        if (accountId == -1) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvRoleInfo = findViewById(R.id.tvRoleInfo);
        layoutAdminOptions = findViewById(R.id.layoutAdminOptions);
        layoutStudentOptions = findViewById(R.id.layoutStudentOptions);

        btnManageStudents = findViewById(R.id.btnManageStudents);
        btnManageSubjects = findViewById(R.id.btnManageSubjects);

        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnViewSchedules = findViewById(R.id.btnViewSchedules);
        btnViewCalendar = findViewById(R.id.btnViewCalendar);

        btnLogout = findViewById(R.id.btnLogout);

        String username = prefs.getString(Constants.KEY_USERNAME, "");
        String role = prefs.getString(Constants.KEY_ROLE, "");

        if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {
            tvWelcome.setText("Xin chào Admin: " + username);
            tvRoleInfo.setText("Vai trò: Quản trị viên (ADMIN)");
            layoutAdminOptions.setVisibility(View.VISIBLE);
            layoutStudentOptions.setVisibility(View.GONE);
        } else if (Constants.ROLE_STUDENT.equalsIgnoreCase(role)) {
            tvWelcome.setText("Xin chào Sinh viên: " + username);
            tvRoleInfo.setText("Vai trò: Sinh viên (STUDENT)");
            layoutStudentOptions.setVisibility(View.VISIBLE);
            layoutAdminOptions.setVisibility(View.GONE);
        }

        // ADMIN Listeners
        btnManageStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StudentActivity.class));
            }
        });

        btnManageSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SubjectActivity.class));
            }
        });

        // STUDENT Listeners
        btnAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScheduleFormActivity.class));
            }
        });

        btnViewSchedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StudyScheduleActivity.class));
            }
        });

        btnViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        // LOGOUT Listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
