# HUỚNG DẪN BÀN GIAO DỰ ÁN (TEAM HANDOFF)

---

## 1. CẤU TRÚC PHÂN CHIA NHÓM VÀ TỆP TIN

- **Package gốc:** `com.example.test3`
- **Thành viên 1 (Phụ trách Nền tảng & Đặt lịch - Đã hoàn thành):**
  - Database & Data Layer: `dal/DBHelper.java`
  - Models: `models/Account.java`, `models/Student.java`, `models/Subject.java`, `models/StudySchedule.java`
  - Constants: `utils/Constants.java`
  - Screens: `activities/LoginActivity.java`, `MainActivity.java`, `activities/ScheduleFormActivity.java`
- **Thành viên 2 (Phụ trách Sinh viên & Môn học):**
  - Screens: `activities/StudentActivity.java`, `activities/SubjectActivity.java`
- **Thành viên 3 (Phụ trách Xem lịch, Lịch theo ngày & Thông báo):**
  - Screens: `activities/StudyScheduleActivity.java`, `activities/CalendarActivity.java`

---

## 2. TÀI KHOẢN VÀ DỮ LIỆU MẪU MẶC ĐỊNH

| Loại tài khoản | Username | Password | Thông tin bổ sung |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | Quản trị viên hệ thống |
| **STUDENT** | `student01` | `student123` | Sinh viên: Nguyễn Văn A (Mã: SV001) |

*Môn học mẫu khởi tạo:*
- `SUB001`: Lập trình Android (3 tín chỉ)
- `SUB002`: Cơ sở dữ liệu (3 tín chỉ)
- `SUB003`: Mạng máy tính (2 tín chỉ)

---

## 3. CHỮ KÝ HÀM VÀ NGUYÊN TẮC DÙNG `DBHelper`

### A. Xác thực và Phiên làm việc
```java
// Đăng nhập: trả về Account nếu đúng, null nếu sai
public Account checkLogin(String username, String password)

// Lấy hồ sơ sinh viên theo accountId
public Student getStudentByAccountId(int accountId)

// Kiểm tra username đã tồn tại chưa
public boolean isUsernameExists(String username)
```

### B. Dành cho Người 2 (StudentActivity & SubjectActivity)
```java
// Thêm Sinh viên kèm Tạo Account (Chạy Transaction, rollback nếu lỗi)
public long addStudentWithAccount(String username, String password, String studentCode, String fullname, String email, String phone)

public List<Student> getAllStudents()
public Student getStudentById(int id)
public boolean updateStudent(Student student)
public List<Student> searchStudents(String keyword)

// Xóa sinh viên: Tự động chặn xóa nếu đã có lịch học (tránh bản ghi mồ côi)
public boolean deleteStudent(int studentId)

// Quản lý Môn học
public long addSubject(Subject subject)
public List<Subject> getAllSubjects()
public Subject getSubjectById(int id)
public boolean updateSubject(Subject subject)
public List<Subject> searchSubjects(String keyword)

// Xóa môn học: Tự động chặn xóa nếu đã có lịch học liên kết
public boolean deleteSubject(int subjectId)
```

### C. Dành cho Người 3 (StudyScheduleActivity & CalendarActivity)
```java
// Lấy danh sách lịch học của sinh viên (sắp xếp ngày và giờ tăng dần)
public List<StudySchedule> getSchedulesByStudentId(int studentId)

// Lọc lịch học theo sinh viên và ngày chọn (yyyy-MM-dd)
public List<StudySchedule> getSchedulesByStudentAndDate(int studentId, String studyDate)

// Tìm kiếm lịch học theo tên môn hoặc địa điểm
public List<StudySchedule> searchSchedules(int studentId, String keyword)

// Lấy 1 lịch học theo ID (kèm thông tin tên môn học)
public StudySchedule getScheduleById(int scheduleId)

// Cập nhật trạng thái lịch (Pending, Confirmed, Cancelled). Tự động kiểm tra trùng giờ nếu đổi từ Cancelled sang Pending/Confirmed.
public boolean updateScheduleStatus(int scheduleId, int studentId, String newStatus)

// Xóa lịch học (kiểm tra đúng sinh viên sở hữu)
public boolean deleteSchedule(int scheduleId, int studentId)
```

---

## 4. HẰNG SỐ, SHARED PREFERENCES VÀ INTENT EXTRAS

Tất cả hằng số dùng chung lưu tại `com.example.test3.utils.Constants`:

```java
Constants.PREF_NAME           = "UserSession";
Constants.KEY_ACCOUNT_ID      = "account_id";
Constants.KEY_USERNAME        = "username";
Constants.KEY_ROLE            = "role";            // "ADMIN" hoặc "STUDENT"
Constants.KEY_STUDENT_ID     = "student_id";      // ID sinh viên (nếu role là STUDENT)

Constants.EXTRA_SCHEDULE_ID   = "extra_schedule_id";
Constants.EXTRA_STUDENT_ID    = "extra_student_id";
Constants.EXTRA_SUBJECT_ID    = "extra_subject_id";
```

### Cách lấy thông tin phiên đăng nhập:
```java
SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
int studentId = prefs.getInt(Constants.KEY_STUDENT_ID, -1);
String role = prefs.getString(Constants.KEY_ROLE, "");
```

---

## 5. HƯỚNG DẪN MỞ FORM THÊM / SỬA LỊCH HỌC

### Thêm lịch mới (Mode Add):
```java
Intent intent = new Intent(context, ScheduleFormActivity.class);
startActivity(intent);
```

### Chỉnh sửa lịch (Mode Edit):
```java
Intent intent = new Intent(context, ScheduleFormActivity.class);
intent.putExtra(Constants.EXTRA_SCHEDULE_ID, schedule.getId());
startActivity(intent);
```

---

## 6. ĐIỂM NỐI TÍNH NĂNG THÔNG BÁO (DÀNH CHO NGƯỜI 3)

Khi thực hiện Thêm, Sửa hoặc Đổi trạng thái lịch học, Người 3 có thể đặt lịch/hủy thông báo (`AlarmManager` / `Notification`) tại:
1. **Sau khi thêm thành công:** Đặt thông báo nhắc lịch học trước giờ học.
2. **Sau khi chỉnh sửa/hủy lịch (Cancelled):** Cập nhật hoặc hủy thông báo tương ứng.
