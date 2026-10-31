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
import com.example.test3.adapters.StudentAdapter;
import com.example.test3.dal.DBHelper;
import com.example.test3.models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity implements StudentAdapter.OnStudentActionListener {

    private EditText etSearchStudent;
    private Button btnAddStudent, btnBack;
    private RecyclerView rvStudents;
    private TextView tvEmptyStudent;

    private DBHelper dbHelper;
    private StudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        dbHelper = new DBHelper(this);

        initViews();
        setupRecyclerView();
        loadStudents();
        setupSearch();

        btnAddStudent.setOnClickListener(v -> showAddStudentDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etSearchStudent = findViewById(R.id.etSearchStudent);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnBack = findViewById(R.id.btnBack);
        rvStudents = findViewById(R.id.rvStudents);
        tvEmptyStudent = findViewById(R.id.tvEmptyStudent);
    }

    private void setupRecyclerView() {
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList, this);
        rvStudents.setAdapter(adapter);
    }

    private void loadStudents() {
        studentList = dbHelper.getAllStudents();
        adapter.updateData(studentList);

        if (studentList.isEmpty()) {
            tvEmptyStudent.setVisibility(View.VISIBLE);
            rvStudents.setVisibility(View.GONE);
        } else {
            tvEmptyStudent.setVisibility(View.GONE);
            rvStudents.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearch() {
        etSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    loadStudents();
                } else {
                    studentList = dbHelper.searchStudents(keyword);
                    adapter.updateData(studentList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Sinh Viên & Tạo Tài Khoản");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        builder.setView(view);

        EditText edtUsername = view.findViewById(R.id.edtUsername);
        EditText edtPassword = view.findViewById(R.id.edtPassword);
        EditText edtStudentCode = view.findViewById(R.id.edtStudentCode);
        EditText edtFullname = view.findViewById(R.id.edtFullname);
        EditText edtEmail = view.findViewById(R.id.edtEmail);
        EditText edtPhone = view.findViewById(R.id.edtPhone);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String studentCode = edtStudentCode.getText().toString().trim();
            String fullname = edtFullname.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || studentCode.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đủ Tên đăng nhập, Mật khẩu, Mã SV và Họ tên!", Toast.LENGTH_LONG).show();
                return;
            }

            long result = dbHelper.addStudentWithAccount(username, password, studentCode, fullname, email, phone);
            if (result > 0) {
                Toast.makeText(this, "Thêm sinh viên và tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                loadStudents();
            } else {
                Toast.makeText(this, "Thêm thất bại! Tên đăng nhập hoặc Mã SV đã tồn tại.", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onEdit(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa Sinh Viên: " + student.getStudentCode());

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        builder.setView(view);

        EditText edtUsername = view.findViewById(R.id.edtUsername);
        EditText edtPassword = view.findViewById(R.id.edtPassword);
        EditText edtStudentCode = view.findViewById(R.id.edtStudentCode);
        EditText edtFullname = view.findViewById(R.id.edtFullname);
        EditText edtEmail = view.findViewById(R.id.edtEmail);
        EditText edtPhone = view.findViewById(R.id.edtPhone);

        // Hide account username and password fields during student edit
        edtUsername.setVisibility(View.GONE);
        edtPassword.setVisibility(View.GONE);

        edtStudentCode.setText(student.getStudentCode());
        edtFullname.setText(student.getFullname());
        edtEmail.setText(student.getEmail());
        edtPhone.setText(student.getPhone());

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String studentCode = edtStudentCode.getText().toString().trim();
            String fullname = edtFullname.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (studentCode.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "Mã SV và Họ tên không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            student.setStudentCode(studentCode);
            student.setFullname(fullname);
            student.setEmail(email);
            student.setPhone(phone);

            boolean success = dbHelper.updateStudent(student);
            if (success) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                loadStudents();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onDelete(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa Sinh Viên");
        builder.setMessage("Bạn có chắc chắn muốn xóa sinh viên " + student.getFullname() + " (" + student.getStudentCode() + ")?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = dbHelper.deleteStudent(student.getId());
            if (success) {
                Toast.makeText(this, "Đã xóa sinh viên và tài khoản liên kết!", Toast.LENGTH_SHORT).show();
                loadStudents();
            } else {
                Toast.makeText(this, "Không thể xóa sinh viên do đang có lịch học!", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }
}
