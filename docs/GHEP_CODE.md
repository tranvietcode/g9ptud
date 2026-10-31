# Ghép vào project của nhóm

## 1. Thống nhất với trưởng nhóm

Chỉ trưởng nhóm sửa DBHelper và model theo phân công. Bản này cung cấp bản tham chiếu để thống nhất API. Giữ database/schema/migration đang có của nhóm; không thay bằng database demo. Database demo tên `student_manager_admin_demo.db`, không đọc hay nâng cấp database project cũ.

Package: `com.example.studentmanager`.

SharedPreferences: file `login_account`; khóa `accountId` kiểu long, `username` và `role` kiểu String. Account.role dùng chính xác `Admin` hoặc `Student`. Nếu nhóm dùng tên khác, trưởng nhóm cần điều chỉnh thống nhất LoginActivity, AdminActivity và các hàm phân quyền.

## 2. API DBHelper hai màn hình cần

```java
public DBHelper(Context context);
public boolean isAdmin(long actor);
public List<Student> getStudents(long actor, String keyword);
public void saveStudent(long actor, long id, String code, String name,
    String email, String phone, String username, String password);
public void deleteStudent(long actor, long id);
public List<Subject> getSubjects(long actor, String keyword);
public void saveSubject(long actor, long id, String code, String name,
    String description, String credits);
public void deleteSubject(long actor, long id);
```

`actor` là Account.id của người đang đăng nhập, không phải Student.id. `id=0` nghĩa là thêm mới; `id>0` là sửa. Đầu ra getStudents có username từ JOIN Account, không lấy mật khẩu. Thuộc tính username trong Student model chỉ phục vụ giao diện, không thêm cột username vào bảng Student.

Model Student cần các getter getId, getStudentCode, getFullname, getEmail, getPhone, getAccountId, getUsername. Subject cần getId, getSubjectCode, getSubjectName, getDescription, getCredits. Có thể thêm getter vào model nhóm thay vì thay thế model.

DBHelper phải kiểm tra Admin trong các thao tác quản trị, bật foreign_keys trong onConfigure, và transaction khi ghi Student + Account. Thống nhất chính sách xóa với nhóm: demo cascade lịch khi xóa sinh viên; chặn xóa môn đã có lịch. Mọi phiên bản ứng dụng phải dùng cùng cách băm/kiểm tra mật khẩu; demo dùng dal/Passwords.java với PBKDF2-HMAC-SHA256. Không thay cách lưu mật khẩu của database cũ nếu chưa có migration tương ứng.

## 3. Các file ghép

1. Tạo nhánh `admin-management`.
2. Chép StudentActivity.java, SubjectActivity.java, AdminActivity.java vào activities.
3. Chép 4 layout activity_student, dialog_student, activity_subject, dialog_subject vào res/layout. Các ID của hai màn hình có thể trùng nhau vì được tìm từ đúng layout/form.
4. Trưởng nhóm ghép các API trên vào DBHelper và đối chiếu getter của model.
5. Thêm vào bên trong application của AndroidManifest.xml, chỉ khi chưa có:

```xml
<activity android:name=".activities.StudentActivity" android:exported="false" />
<activity android:name=".activities.SubjectActivity" android:exported="false" />
```

6. Trong nhánh Admin của MainActivity, nối nút vào hai màn hình:

```java
startActivity(new Intent(this, StudentActivity.class));
startActivity(new Intent(this, SubjectActivity.class));
```

Mỗi lệnh đặt trong listener của nút tương ứng. Thêm import các Activity và android.content.Intent. Không thay MainActivity/LoginActivity chung bằng bản demo nếu nhóm đã triển khai.

7. Nếu dùng layout XML được cung cấp, không cần thư viện ngoài Android SDK. Demo dùng minSdk 26; nhóm dùng thấp hơn phải kiểm tra API của mã hỗ trợ trước khi ghép.
8. Với database có sẵn: trưởng nhóm viết migration giữ dữ liệu, không xóa bảng để khắc phục lỗi schema. onUpgrade của bản demo chủ động báo lỗi nếu tăng version mà chưa có migration.

## 4. Trả lời khi cô hỏi

- Màn hình ở activities/StudentActivity.java và SubjectActivity.java; giao diện ở res/layout.
- SQL và kiểm tra dữ liệu nằm trong dal/DBHelper.java.
- Student và Subject model biểu diễn dữ liệu, không tự lưu database.
- Thêm sinh viên tạo Account trước, lấy id để lưu Student.accountId; hai thao tác cùng transaction.
- Dữ liệu nằm trong SQLite riêng của ứng dụng trên từng thiết bị; GitHub không đồng bộ dữ liệu SQLite.
