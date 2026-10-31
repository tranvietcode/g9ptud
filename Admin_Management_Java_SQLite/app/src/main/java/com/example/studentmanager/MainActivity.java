package com.example.studentmanager;

import android.content.Intent;
import android.os.Bundle;
import com.example.studentmanager.activities.AdminActivity;
import com.example.studentmanager.activities.LoginActivity;
import com.example.studentmanager.activities.StudentActivity;
import com.example.studentmanager.activities.SubjectActivity;

public class MainActivity extends AdminActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnStudents).setOnClickListener(v -> startActivity(new Intent(this, StudentActivity.class)));
        findViewById(R.id.btnSubjects).setOnClickListener(v -> startActivity(new Intent(this, SubjectActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            getSharedPreferences("login_account", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }
}
