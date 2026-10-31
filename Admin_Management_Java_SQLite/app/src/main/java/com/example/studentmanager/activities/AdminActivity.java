package com.example.studentmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;
import com.example.studentmanager.dal.DBHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AdminActivity extends Activity {
    protected DBHelper db;
    protected long actor;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private volatile boolean destroyed;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DBHelper(this);
        actor = getSharedPreferences("login_account", MODE_PRIVATE).getLong("accountId", -1);
    }
    @Override protected void onResume() {
        super.onResume();
        runDb(() -> { if (!db.isAdmin(actor)) throw new SecurityException(); }, () -> {});
    }
    protected void runDb(Runnable task, Runnable success) { runDb(task, success, () -> {}); }
    protected void runDb(Runnable task, Runnable success, Runnable failure) {
        worker.execute(() -> {
            try {
                task.run();
                runOnUiThread(() -> { if (!destroyed && !isFinishing()) success.run(); });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (destroyed || isFinishing()) return;
                    if (e instanceof SecurityException) {
                        getSharedPreferences("login_account", MODE_PRIVATE).edit().clear().apply();
                        startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    } else {
                        String message = e instanceof SQLiteConstraintException ? "Mã hoặc tên đăng nhập đã tồn tại, hoặc dữ liệu có liên kết không hợp lệ." :
                            e instanceof IllegalArgumentException ? e.getMessage() : "Không thể thao tác dữ liệu. Vui lòng thử lại.";
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                    failure.run();
                });
            }
        });
    }
    protected void notifySaved() { Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show(); }
    @Override protected void onDestroy() {
        destroyed = true;
        worker.execute(() -> db.close());
        worker.shutdown();
        super.onDestroy();
    }
}
