package com.example.test3.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.example.test3.adapters.SubjectAdapter;
import com.example.test3.dal.DBHelper;
import com.example.test3.models.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectActionListener {

    private EditText etSearchSubject;
    private Button btnAddSubject, btnBack;
    private RecyclerView rvSubjects;
    private TextView tvEmptySubject;

    private DBHelper dbHelper;
    private SubjectAdapter adapter;
    private List<Subject> subjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        dbHelper = new DBHelper(this);

        initViews();
        setupRecyclerView();
        loadSubjects();
        setupSearch();

        btnAddSubject.setOnClickListener(v -> showAddSubjectDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etSearchSubject = findViewById(R.id.etSearchSubject);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        btnBack = findViewById(R.id.btnBack);
        rvSubjects = findViewById(R.id.rvSubjects);
        tvEmptySubject = findViewById(R.id.tvEmptySubject);
    }

    private void setupRecyclerView() {
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(subjectList, this);
        rvSubjects.setAdapter(adapter);
    }

    private void loadSubjects() {
        subjectList = dbHelper.getAllSubjects();
        adapter.updateData(subjectList);

        if (subjectList.isEmpty()) {
            tvEmptySubject.setVisibility(View.VISIBLE);
            rvSubjects.setVisibility(View.GONE);
        } else {
            tvEmptySubject.setVisibility(View.GONE);
            rvSubjects.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearch() {
        etSearchSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    loadSubjects();
                } else {
                    subjectList = dbHelper.searchSubjects(keyword);
                    adapter.updateData(subjectList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("THÊM MÔN HỌC MỚI");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null);
        builder.setView(view);

        EditText edtSubjectCode = view.findViewById(R.id.edtSubjectCode);
        EditText edtSubjectName = view.findViewById(R.id.edtSubjectName);
        EditText edtCredits = view.findViewById(R.id.edtCredits);
        EditText edtDescription = view.findViewById(R.id.edtDescription);

        builder.setPositiveButton("Lưu", null); // Set null to override click listener after show()
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom click listener to keep dialog open on validation error
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String subjectCode = edtSubjectCode.getText().toString().trim();
            String subjectName = edtSubjectName.getText().toString().trim();
            String creditsStr = edtCredits.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (subjectCode.isEmpty()) {
                edtSubjectCode.setError("Vui lòng nhập Mã môn học!");
                edtSubjectCode.requestFocus();
                return;
            }

            if (subjectName.isEmpty()) {
                edtSubjectName.setError("Vui lòng nhập Tên môn học!");
                edtSubjectName.requestFocus();
                return;
            }

            if (creditsStr.isEmpty()) {
                edtCredits.setError("Vui lòng nhập Số tín chỉ!");
                edtCredits.requestFocus();
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
                if (credits <= 0) {
                    edtCredits.setError("Số tín chỉ phải lớn hơn 0!");
                    edtCredits.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                edtCredits.setError("Số tín chỉ phải là một số nguyên!");
                edtCredits.requestFocus();
                return;
            }

            Subject newSubject = new Subject();
            newSubject.setSubjectCode(subjectCode);
            newSubject.setSubjectName(subjectName);
            newSubject.setCredits(credits);
            newSubject.setDescription(description);

            long result = dbHelper.addSubject(newSubject);
            if (result > 0) {
                Toast.makeText(SubjectActivity.this, "Thêm môn học thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadSubjects();
            } else {
                edtSubjectCode.setError("Mã môn học này đã tồn tại trong CSDL!");
                edtSubjectCode.requestFocus();
            }
        });
    }

    @Override
    public void onEdit(Subject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CHỈNH SỬA MÔN HỌC: " + subject.getSubjectCode());

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null);
        builder.setView(view);

        EditText edtSubjectCode = view.findViewById(R.id.edtSubjectCode);
        EditText edtSubjectName = view.findViewById(R.id.edtSubjectName);
        EditText edtCredits = view.findViewById(R.id.edtCredits);
        EditText edtDescription = view.findViewById(R.id.edtDescription);

        edtSubjectCode.setText(subject.getSubjectCode());
        edtSubjectName.setText(subject.getSubjectName());
        edtCredits.setText(String.valueOf(subject.getCredits()));
        edtDescription.setText(subject.getDescription());

        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String subjectCode = edtSubjectCode.getText().toString().trim();
            String subjectName = edtSubjectName.getText().toString().trim();
            String creditsStr = edtCredits.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (subjectCode.isEmpty()) {
                edtSubjectCode.setError("Mã môn học không được để trống!");
                edtSubjectCode.requestFocus();
                return;
            }

            if (subjectName.isEmpty()) {
                edtSubjectName.setError("Tên môn học không được để trống!");
                edtSubjectName.requestFocus();
                return;
            }

            if (creditsStr.isEmpty()) {
                edtCredits.setError("Số tín chỉ không được để trống!");
                edtCredits.requestFocus();
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException e) {
                edtCredits.setError("Số tín chỉ phải là một số hợp lệ!");
                edtCredits.requestFocus();
                return;
            }

            subject.setSubjectCode(subjectCode);
            subject.setSubjectName(subjectName);
            subject.setCredits(credits);
            subject.setDescription(description);

            boolean success = dbHelper.updateSubject(subject);
            if (success) {
                Toast.makeText(SubjectActivity.this, "Cập nhật môn học thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadSubjects();
            } else {
                Toast.makeText(SubjectActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDelete(Subject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa Môn Học");
        builder.setMessage("Bạn có chắc chắn muốn xóa môn học " + subject.getSubjectName() + " (" + subject.getSubjectCode() + ")?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = dbHelper.deleteSubject(subject.getId());
            if (success) {
                Toast.makeText(this, "Đã xóa môn học!", Toast.LENGTH_SHORT).show();
                loadSubjects();
            } else {
                Toast.makeText(this, "Không thể xóa môn học do đang có trong lịch học!", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubjects();
    }
}
