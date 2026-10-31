package com.example.studentmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.dal.DBHelper;
import com.example.studentmanager.dal.Row;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends Activity {
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private DBHelper db;
    private volatile boolean destroyed;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DBHelper(this);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.btnLogin);
        login.setOnClickListener(v -> {
            String username = ((EditText) findViewById(R.id.username)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            login.setEnabled(false);
            worker.execute(() -> {
                Row account = null;
                boolean error = false;
                try { account = db.login(username, password); } catch (Exception e) { error = true; }
                Row result = account;
                boolean failed = error;
                runOnUiThread(() -> {
                    if (destroyed || isFinishing()) return;
                    login.setEnabled(true);
                    if (failed) Toast.makeText(this, "Không thể mở dữ liệu", Toast.LENGTH_LONG).show();
                    else if (result == null) Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_LONG).show();
                    else if (!"Admin".equals(result.s("role"))) Toast.makeText(this, "Bản demo này chỉ gồm chức năng Admin. Ghép màn hình Student vào project chính.", Toast.LENGTH_LONG).show();
                    else {
                        getSharedPreferences("login_account", MODE_PRIVATE).edit()
                            .putLong("accountId", result.id("id")).putString("username", result.s("username")).putString("role", result.s("role")).apply();
                        openHome();
                    }
                });
            });
        });
        long accountId = getSharedPreferences("login_account", MODE_PRIVATE).getLong("accountId", -1);
        worker.execute(() -> {
            try { if (db.isAdmin(accountId)) runOnUiThread(() -> { if (!destroyed && !isFinishing()) openHome(); }); }
            catch (Exception ignored) { }
        });
    }
    private void openHome() { startActivity(new Intent(this, MainActivity.class)); finish(); }
    @Override protected void onDestroy() {
        destroyed = true;
        worker.execute(() -> db.close()); worker.shutdown();
        super.onDestroy();
    }
}
