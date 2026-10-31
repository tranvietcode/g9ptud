package com.example.test3.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test3.MainActivity;
import com.example.test3.R;
import com.example.test3.dal.DBHelper;
import com.example.test3.models.Account;
import com.example.test3.models.Student;
import com.example.test3.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);

        // Check active session
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        int savedAccountId = prefs.getInt(Constants.KEY_ACCOUNT_ID, -1);
        if (savedAccountId != -1) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        Account account = dbHelper.checkLogin(username, password);
        if (account == null) {
            Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save session (WITHOUT storing password)
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.KEY_ACCOUNT_ID, account.getId());
        editor.putString(Constants.KEY_USERNAME, account.getUsername());
        editor.putString(Constants.KEY_ROLE, account.getRole());

        if (Constants.ROLE_STUDENT.equalsIgnoreCase(account.getRole())) {
            Student student = dbHelper.getStudentByAccountId(account.getId());
            if (student != null) {
                editor.putInt(Constants.KEY_STUDENT_ID, student.getId());
            } else {
                editor.putInt(Constants.KEY_STUDENT_ID, -1);
            }
        } else {
            editor.putInt(Constants.KEY_STUDENT_ID, -1);
        }

        editor.apply();

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
