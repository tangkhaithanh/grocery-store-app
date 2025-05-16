🛒 Grocery Store App
📌 Giới thiệu chung
Grocery Store App là một ứng dụng mua sắm trực tuyến được phát triển như một dự án cuối kỳ cho môn lập trình di động Android. Ứng dụng cho phép người dùng duyệt danh mục sản phẩm, thêm hàng vào giỏ, sử dụng voucher giảm giá, đặt hàng và theo dõi đơn hàng trực tuyến một cách tiện lợi và nhanh chóng.

Với mục tiêu mô phỏng một quy trình mua sắm thực tế, ứng dụng không chỉ tập trung vào trải nghiệm người dùng (UI/UX) mà còn xây dựng một kiến trúc phần mềm hiện đại, dễ mở rộng và bảo trì.

🧰 Công nghệ sử dụng
🔧 Backend
Java + Spring Boot

Áp dụng Mô hình MVC để tổ chức các tầng: Controller, Service, Repository

Kết nối cơ sở dữ liệu (ví dụ: MySQL/PostgreSQL)

Cung cấp các API RESTful phục vụ ứng dụng Android

📱 Mobile App
Kotlin sử dụng Jetpack Compose cho giao diện người dùng hiện đại và mượt mà

Áp dụng Mô hình MVVM để phân tách logic và UI

Các thành phần chính: ViewModel, State Management, Navigation, Repository Pattern

Kết nối API qua Retrofit và xử lý bất đồng bộ bằng Coroutine

📚 Các chức năng chính (Use Case)
Ứng dụng Grocery Store App hỗ trợ đầy đủ các tính năng cần thiết cho một quy trình mua sắm trực tuyến hiện đại, chia thành các nhóm chức năng như sau:

👤 Quản lý người dùng
Đăng ký tài khoản

Đăng nhập

Lấy lại mật khẩu

🛒 Quản lý giỏ hàng
Thêm sản phẩm vào giỏ hàng

Xóa sản phẩm khỏi giỏ hàng

Chỉnh sửa số lượng sản phẩm trong giỏ

❤️ Danh sách yêu thích
Thêm sản phẩm vào danh sách yêu thích

Xóa sản phẩm khỏi danh sách yêu thích

📦 Quản lý đơn hàng
Tạo đơn hàng

Hủy đơn hàng

Theo dõi trạng thái đơn hàng

Thanh toán COD (trả tiền khi nhận hàng)

Thanh toán qua VNPay

Áp dụng mã giảm giá (Voucher)

🗺️ Quản lý địa chỉ
Thêm địa chỉ giao hàng

Xóa địa chỉ

Sửa địa chỉ

🔍 Tìm kiếm và đánh giá
Tìm kiếm sản phẩm

Xem đánh giá sản phẩm

Thêm đánh giá

⚡ Hệ thống và quản trị (Dành cho quản trị viên)
Kích hoạt chương trình Flash Sale
