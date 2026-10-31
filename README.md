# Phần người 2 — Chức năng Admin

Java + SQLite, package `com.example.studentmanager`. Bản demo độc lập để chạy và bàn giao hai module Admin; không phải toàn bộ ứng dụng đặt lịch của nhóm.

## 1. Mở và chạy

1. Giải nén và mở thư mục `Admin_Management_Java_SQLite` bằng Android Studio (Open).
2. Chọn Gradle JDK 17 hoặc JDK đi kèm tương thích AGP 8.7.3; cài Android SDK Platform 35 khi được yêu cầu. Gradle wrapper 8.9 đã kèm.
3. Sync Gradle, chạy trên máy ảo hoặc điện thoại Android 8.0/API 26 trở lên.
4. Đăng nhập: `admin` / `Admin@123`.
5. Chọn Quản lý sinh viên hoặc Quản lý môn học. Bấm một dòng để chọn Xem chi tiết, Sửa, Xóa.
6. Ô tìm kiếm: nhập từ khóa rồi bấm Tìm kiếm; để trống rồi tìm để xem toàn bộ.

Cần Internet lần đầu để Android Studio tải dependency. Không có APK kèm theo: môi trường tạo mã chưa có Android SDK để build/chạy Android.

## 2. Phạm vi đã làm

- Sinh viên: danh sách, chi tiết, thêm, sửa, xóa, tìm theo mã/họ tên/email/điện thoại.
- Khi thêm sinh viên: tạo Account vai trò Student và Student liên kết bằng accountId trong một transaction; lỗi ở một bước rollback cả hai.
- Khi sửa: đổi được username; mật khẩu để trống giữ nguyên, nhập mới phải ít nhất 6 ký tự.
- Chặn trùng mã sinh viên, mã môn và username không phân biệt hoa thường bằng ràng buộc SQLite.
- Kiểm tra email, điện thoại, dữ liệu bắt buộc; tín chỉ là số nguyên 1–30 (quy tắc demo, nhóm có thể điều chỉnh).
- Môn học: danh sách, chi tiết, thêm, sửa, xóa, tìm theo mã/tên/mô tả.
- Môn đã được StudySchedule tham chiếu không được xóa. Xóa sinh viên sẽ xóa tài khoản và các lịch liên quan, có hộp thoại cảnh báo.
- Xác minh Admin từ Account trong SQLite; không chỉ tin trường role trong SharedPreferences.
- Ghi dữ liệu và băm mật khẩu chạy trên luồng nền; truy vấn có tham số.

## 3. Tệp bạn phụ trách

`app/src/main/java/com/example/studentmanager/activities/StudentActivity.java`

`app/src/main/java/com/example/studentmanager/activities/SubjectActivity.java`

`app/src/main/res/layout/activity_student.xml`

`app/src/main/res/layout/dialog_student.xml`

`app/src/main/res/layout/activity_subject.xml`

`app/src/main/res/layout/dialog_subject.xml`

`AdminActivity.java` là lớp dùng chung cho hai màn hình. DBHelper, model, MainActivity và LoginActivity là mã tham chiếu giúp chạy thử và thống nhất cách ghép với trưởng nhóm.

Đọc `docs/GHEP_CODE.md` trước khi đưa vào project của nhóm; không chép đè DBHelper đang có.
