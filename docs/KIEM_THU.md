# Kiểm thử bàn giao

## Kiểm tra đã thực hiện trong môi trường tạo mã

- Kiểm tra XML có cấu trúc hợp lệ và tên layout/ID được Java tham chiếu có khai báo.
- Parse cú pháp cả 12 file Java bằng Java Compiler API; biên dịch 4 model và Passwords, kiểm tra mật khẩu đúng/sai. Chưa kiểm tra kiểu Android vì thiếu SDK.
- Kiểm tra package và cấu trúc thư mục.
- Kiểm tra SQLite bằng schema.sql: liên kết Account–Student, uniqueness, rollback khi thêm sinh viên thất bại, cascade khi xóa, restrict xóa môn có lịch.
- Chưa build APK/chạy emulator vì môi trường không có Android SDK. Kiểm thử SQLite trên Python không thay thế kiểm thử Android.

## Chạy trên Android Studio trước khi bàn giao

1. Đăng nhập admin / Admin@123, mở được hai module. Student không vào được màn hình Admin.
2. Thêm sinh viên với mã mới và username mới. Đóng app, mở lại: dữ liệu vẫn còn.
3. Trong project đầy đủ: đăng nhập bằng tài khoản sinh viên vừa tạo, kiểm tra vai trò Student.
4. Thêm trùng mã SV hoặc username kể cả khác hoa thường: báo lỗi, không có tài khoản mồ côi.
5. Để trống trường bắt buộc; email sai; điện thoại sai; mật khẩu dưới 6 ký tự: báo lỗi và giữ form để sửa.
6. Sửa sinh viên: đổi tên, email, phone, username. Để trống mật khẩu thì tài khoản cũ vẫn đăng nhập được; đặt mật khẩu mới thì dùng mật khẩu mới.
7. Tìm từng trường mã/họ tên/email/phone, để trống để lấy lại toàn bộ, tìm không có kết quả hiển thị danh sách rỗng.
8. Bấm Xóa, chọn Hủy: dữ liệu còn. Xác nhận: sinh viên và Account liên kết bị xóa, lịch liên quan bị cascade theo chính sách demo.
9. Thêm, xem, sửa môn; tìm bằng mã, tên, mô tả; mã trùng bị từ chối.
10. Tín chỉ rỗng, 0, 31, quá lớn: bị từ chối. 1 và 30: chấp nhận.
11. Trong project đầy đủ, tạo lịch tham chiếu môn rồi thử xóa môn: báo không được xóa, môn và lịch còn nguyên.
12. Đăng xuất rồi mở lại màn hình quản trị: yêu cầu đăng nhập; kiểm tra xoay màn hình, nút quay lại và bấm Lưu liên tục.
