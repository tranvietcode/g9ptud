package com.example.studentmanager.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.studentmanager.R;
import com.example.studentmanager.models.Subject;
import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AdminActivity {
    private EditText search;
    private ListView list;
    private List<Subject> items = new ArrayList<>();
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_subject);
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
        List<Subject> loaded = new ArrayList<>();
        runDb(() -> loaded.addAll(db.getSubjects(actor, keyword)), () -> {
            items = loaded;
            List<String> labels = new ArrayList<>();
            for (Subject item : items) labels.add(item.getSubjectCode() + " — " + item.getSubjectName() + "\n" + item.getCredits() + " tín chỉ");
            list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
            ((TextView) findViewById(R.id.txtCount)).setText("Kết quả: " + items.size());
        });
    }
    private void actions(Subject item) {
        new AlertDialog.Builder(this).setTitle("Chọn thao tác")
            .setItems(new String[]{"Xem chi tiết", "Sửa", "Xóa"}, (dialog, which) -> {
                if (which == 0) new AlertDialog.Builder(this).setTitle("Thông tin môn học")
                    .setMessage("Mã môn: " + item.getSubjectCode() + "\nTên môn: " + item.getSubjectName() + "\nMô tả: " + item.getDescription() + "\nSố tín chỉ: " + item.getCredits()).setPositiveButton("Đóng", null).show();
                else if (which == 1) edit(item);
                else new AlertDialog.Builder(this).setTitle("Xác nhận xóa")
                    .setMessage("Bạn có muốn xóa môn học này? Môn đã có lịch học sẽ không thể xóa.").setNegativeButton("Hủy", null)
                    .setPositiveButton("Xóa", (d, w) -> runDb(() -> db.deleteSubject(actor, item.getId()), this::loadData)).show();
            }).show();
    }
    private void edit(Subject item) {
        View form = getLayoutInflater().inflate(R.layout.dialog_subject, null);
        EditText subjectCode = form.findViewById(R.id.subjectCode);
        EditText subjectName = form.findViewById(R.id.subjectName);
        EditText description = form.findViewById(R.id.description);
        EditText credits = form.findViewById(R.id.credits);
        if (item != null) {
            subjectCode.setText(item.getSubjectCode());
            subjectName.setText(item.getSubjectName());
            description.setText(item.getDescription());
            credits.setText(String.valueOf(item.getCredits()));
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(item == null ? "Thêm môn học" : "Sửa môn học")
            .setView(form).setNegativeButton("Hủy", null).setPositiveButton("Lưu", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String[] values = new String[]{subjectCode.getText().toString(), subjectName.getText().toString(), description.getText().toString(), credits.getText().toString()};
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
            dialog.setCancelable(false);
            runDb(() -> db.saveSubject(actor, item == null ? 0 : item.getId(), values[0], values[1], values[2], values[3]), () -> {
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
