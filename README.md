# Ứng dụng quản lý và đặt lịch học sinh viên

Bài thực hành nhóm môn Phát triển ứng dụng di động — Nhóm 9.

Ứng dụng Android hỗ trợ quản lý sinh viên, môn học và đặt lịch học cá nhân, phân quyền theo hai vai trò **Admin** và **Student**.

## Công nghệ

- Ngôn ngữ: Java.
- Giao diện: XML.
- Cơ sở dữ liệu: SQLite, sử dụng `SQLiteOpenHelper`.
- Môi trường phát triển: Android Studio.
- Quản lý mã nguồn: Git và GitHub.
- Lưu phiên đăng nhập: SharedPreferences.

## Chức năng theo yêu cầu

### Admin

- Đăng nhập và đăng xuất.
- Xem, thêm, sửa, xóa và tìm kiếm sinh viên.
- Tạo tài khoản liên kết với hồ sơ sinh viên.
- Xem, thêm, sửa, xóa và tìm kiếm môn học.

### Student

- Đăng nhập và đăng xuất.
- Xem và tìm kiếm lịch học cá nhân.
- Đặt lịch bằng cách chọn môn học, ngày, giờ bắt đầu, giờ kết thúc và địa điểm.
- Chỉnh sửa và xóa lịch học.
- Kiểm tra và ngăn đặt lịch trùng giờ.
- Quản lý trạng thái: `Pending`, `Confirmed`, `Cancelled`.
- Xem lịch học theo ngày.
- Nhận thông báo buổi học sắp tới và thay đổi lịch.

## Cơ sở dữ liệu

| Bảng | Các trường |
|---|---|
| Account | id, username, password, role |
| Student | id, studentCode, fullname, email, phone, accountId |
| Subject | id, subjectCode, subjectName, description, credits |
| StudySchedule | id, studentId, subjectId, studyDate, startTime, endTime, location, status |

Dữ liệu được lưu cục bộ trên từng thiết bị bằng SQLite. Các thiết bị không tự đồng bộ dữ liệu với nhau.

SharedPreferences lưu thông tin phiên đăng nhập; dữ liệu nghiệp vụ được lưu trong SQLite.

## Cấu trúc mã nguồn

Package gốc của project nền tảng: `com.example.test3`.

| Thư mục / File | Trách nhiệm |
|---|---|
| `activities/` | Các màn hình đăng nhập, quản lý và đặt lịch |
| `dal/DBHelper.java` | Tạo database và thực hiện truy vấn SQLite |
| `models/Account.java` | Model tài khoản |
| `models/Student.java` | Model sinh viên |
| `
