package com.example.studentmanager.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.studentmanager.R;
import com.example.studentmanager.models.Student;
import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AdminActivity {
    private EditText search;
    private ListView list;
    private List<Student> items = new ArrayList<>();
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_student);
        search = findViewById(R.id.edtSearch);
        list = findViewById(R.id.listData);
        list.setEmptyView(findViewById(R.id.txtEmpty));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSearch).setOnClickListener(v -> loadData());
        search.setOnEditorActionListener((v, action, event) -> { loadData(); return true; });
        findViewById(R.id.btnAdd).setOnClickListener(v -> edit(null));
        list.setOnItemClickListener((parent, view, position, id) -> actions(items.get(position)));
    }
    @Override protected void onResume() { super.onResume(); loadData(); }
    private void loadData() {
        String keyword = search.getText().toString();
        List<Student> loaded = new ArrayList<>();
        runDb(() -> loaded.addAll(db.getStudents(actor, keyword)), () -> {
            items = loaded;
            List<String> labels = new ArrayList<>();
            for (Student item : items) labels.add(item.getStudentCode() + " — " + item.getFullname() + "\n" + item.getEmail() + " | " + item.getPhone());
            list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
            ((TextView) findViewById(R.id.txtCount)).setText("Kết quả: " + items.size());
        });
    }
    private void actions(Student item) {
        new AlertDialog.Builder(this).setTitle("Chọn thao tác")
            .setItems(new String[]{"Xem chi tiết", "Sửa", "Xóa"}, (dialog, which) -> {
                if (which == 0) new AlertDialog.Builder(this).setTitle("Thông tin sinh viên")
                    .setMessage("Mã SV: " + item.getStudentCode() + "\nHọ tên: " + item.getFullname() + "\nEmail: " + item.getEmail() + "\nĐiện thoại: " + item.getPhone() + "\nTài khoản: " + item.getUsername()).setPositiveButton("Đóng", null).show();
                else if (which == 1) edit(item);
                else new AlertDialog.Builder(this).setTitle("Xác nhận xóa")
                    .setMessage("Xóa sinh viên sẽ xóa cả tài khoản và toàn bộ lịch học của sinh viên này. Bạn có muốn tiếp tục?").setNegativeButton("Hủy", null)
                    .setPositiveButton("Xóa", (d, w) -> runDb(() -> db.deleteStudent(actor, item.getId()), this::loadData)).show();
            }).show();
    }
    private void edit(Student item) {
        View form = getLayoutInflater().inflate(R.layout.dialog_student, null);
        EditText studentCode = form.findViewById(R.id.studentCode);
        EditText fullname = form.findViewById(R.id.fullname);
        EditText email = form.findViewById(R.id.email);
        EditText phone = form.findViewById(R.id.phone);
        EditText username = form.findViewById(R.id.username);
        EditText password = form.findViewById(R.id.password);
        if (item != null) {
            studentCode.setText(item.getStudentCode());
            fullname.setText(item.getFullname());
            email.setText(item.getEmail());
            phone.setText(item.getPhone());
            username.setText(item.getUsername());
            password.setHint("Mật khẩu mới (để trống để giữ mật khẩu cũ)");
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(item == null ? "Thêm sinh viên" : "Sửa sinh viên")
            .setView(form).setNegativeButton("Hủy", null).setPositiveButton("Lưu", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String[] values = new String[]{studentCode.getText().toString(), fullname.getText().toString(), email.getText().toString(), phone.getText().toString(), username.getText().toString(), password.getText().toString()};
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
            dialog.setCancelable(false);
            runDb(() -> db.saveStudent(actor, item == null ? 0 : item.getId(), values[0], values[1], values[2], values[3], values[4], values[5]), () -> {
                dialog.dismiss(); notifySaved(); loadData();
            }, () -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
                dialog.setCancelable(true);
            });
        }));
        dialog.show();
    }
}
