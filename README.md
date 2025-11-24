# Paws&Hearts - Ứng dụng với tính năng nhận nuôi động vật cơ nhỡ

# UI figma
https://www.figma.com/design/UwuzUCmsJjyzOvyAOjTwII/UI?node-id=0-1&p=f&t=ih4SgHjN8IlcIb1s-0

# Mục lục

- [Tên dự án và chủ đề](#tên-dự-án-và-chủ-đề)
- [Lý do lựa chọn dự án](#lý-do-lựa-chọn-dự-án)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Các tính năng chính](#các-tính-năng-chính)
- [Hướng dẫn sử dụng & Demo](#hướng-dẫn-sử-dụng--demo)
- [Nguyên tắc làm việc](#nguyên-tắc-làm-việc)

## 1. Tên dự án và chủ đề

   **Tên dự án:** Paws Hearts
   **Chủ đề:** Ứng dụng quản lý thú cưng và kết nối cộng đồng yêu thú cưng.

2. Lý do lựa chọn dự án

   Trong xã hội hiện đại, thú cưng ngày càng trở thành một phần quan trọng trong cuộc sống của nhiều người. Tuy nhiên, việc quản lý thông tin thú cưng, kết nối với cộng đồng những người yêu thú cưng, và tìm kiếm các dịch vụ liên quan đôi khi còn gặp nhiều khó khăn. Dự án Paws Hearts ra đời với mong muốn tạo ra một nền tảng toàn diện, giúp người dùng:

   - **Quản lý thông tin thú cưng:** Dễ dàng lưu trữ và theo dõi các thông tin quan trọng về thú cưng như lịch sử tiêm chủng, khám bệnh, chế độ ăn uống, và các ghi chú đặc biệt khác.
   - **Kết nối cộng đồng:** Xây dựng một cộng đồng vững mạnh nơi những người yêu thú cưng có thể chia sẻ kinh nghiệm, tìm kiếm lời khuyên, và hỗ trợ lẫn nhau.
   - **Hỗ trợ và quyên góp:** Cung cấp tính năng quyên góp và các hoạt động hỗ trợ cho các tổ chức bảo vệ động vật hoặc những thú cưng gặp hoàn cảnh khó khăn.
   - **Tìm kiếm dịch vụ:** Dễ dàng tìm kiếm các dịch vụ thú cưng lân cận như phòng khám, cửa hàng thức ăn, hoặc dịch vụ trông giữ.

3. Công nghệ sử dụng

   Dự án Paws Hearts được xây dựng trên nền tảng các công nghệ hiện đại và mạnh mẽ để đảm bảo hiệu suất, khả năng mở rộng và trải nghiệm người dùng tốt nhất:

   - **Ngôn ngữ lập trình:** Kotlin
   - **Framework UI:** Jetpack Compose
   - **Kiến trúc ứng dụng:** MVVM (Model-View-ViewModel) với Android Architecture Components (Lifecycle, ViewModel, Room)
   - **Cơ sở dữ liệu cục bộ:** Room Persistence Library và Proto DataStore
   - **Backend as a Service (BaaS):** Google Firebase (Authentication, Firestore Database, Cloud Storage, Analytics)
   - **Xác thực:** Firebase Authentication, Google Sign-In (thông qua Play Services Auth)
   - **Quản lý bất đồng bộ:** Kotlin Coroutines và Flow
   - **Tải và hiển thị hình ảnh:** Coil Compose

4. Các tính năng chính

   Ứng dụng Paws Hearts cung cấp các tính năng đa dạng nhằm kết nối và hỗ trợ cộng đồng yêu thú cưng:

   - **Xác thực người dùng:** Đăng ký, đăng nhập an toàn với Firebase Authentication và Google Sign-In.
   - **Quản lý hồ sơ cá nhân:** Xem, chỉnh sửa thông tin cá nhân, quản lý thú cưng của người dùng.
   - **Hoạt động cộng đồng:** Tạo và tham gia các hoạt động, sự kiện liên quan đến thú cưng.
   - **Nhận nuôi thú cưng:** Đăng bài về thú cưng cần nhận nuôi và tìm kiếm thú cưng để nhận nuôi.
   - **Bài viết và tương tác:** Tạo bài viết, chia sẻ hình ảnh, video, tương tác qua bình luận và lượt thích.
   - **Hệ thống tin nhắn:** Giao tiếp trực tiếp với người dùng khác thông qua tính năng trò chuyện trong ứng dụng.
   - **Quyên góp:** Hỗ trợ các tổ chức bảo vệ động vật hoặc các trường hợp thú cưng cần giúp đỡ thông qua tính năng quyên góp.
   - **Thông báo:** Nhận các thông báo quan trọng về hoạt động, tin nhắn mới và các sự kiện khác.
   - **Cài đặt ứng dụng:** Tùy chỉnh các thiết lập của ứng dụng theo sở thích cá nhân.

5. Hướng dẫn sử dụng & Demo

   Để cài đặt và chạy ứng dụng Paws Hearts trên môi trường phát triển của bạn, hãy làm theo các bước sau:

   1. **Clone dự án:**
      ```bash
      git clone https://github.com/your-username/Paws-Hearts.git
      cd Paws-Hearts
      ```
   2. **Cài đặt Firebase:**
      - Tạo một dự án Firebase mới tại [Firebase Console](https://console.firebase.google.com/).
      - Thêm ứng dụng Android vào dự án Firebase của bạn và tải xuống tệp `google-services.json`.
      - Đặt tệp `google-services.json` vào thư mục `app/` của dự án.
      - Đảm bảo rằng bạn đã kích hoạt các dịch vụ sau trong Firebase:
        - Authentication (Email/Password, Google Sign-In)
        - Cloud Firestore
        - Cloud Storage
        - Google Analytics (Tùy chọn)
   3. **Cấu hình biến môi trường (nếu có):** Kiểm tra file `local.properties` hoặc các file cấu hình khác để xem có cần thêm biến môi trường nào không. (Hiện tại không có)
   4. **Mở dự án bằng Android Studio:**
      - Mở Android Studio và chọn "Open an existing Android Studio project".
      - Điều hướng đến thư mục gốc của dự án `Paws-Hearts` và mở nó.
   5. **Đồng bộ Gradle:** Android Studio sẽ tự động đồng bộ hóa Gradle. Nếu có lỗi, hãy kiểm tra kết nối internet và đảm bảo các plugin và dependency đã được cấu hình đúng.
   6. **Chạy ứng dụng:**
      - Chọn một thiết bị Android (máy ảo hoặc thiết bị vật lý) đã được kết nối.
      - Nhấp vào nút "Run" (biểu tượng tam giác màu xanh lá cây) trên thanh công cụ của Android Studio để cài đặt và chạy ứng dụng.

   **Demo:**

   Hiện tại, không có video demo trực tiếp. Tuy nhiên, bạn có thể dễ dàng trải nghiệm các tính năng bằng cách chạy ứng dụng trên thiết bị của mình và khám phá từng phần:

   - Đăng ký tài khoản mới hoặc đăng nhập bằng tài khoản Google.
   - Tạo hồ sơ thú cưng và cập nhật thông tin cá nhân.
   - Duyệt qua các hoạt động và bài viết của cộng đồng.
   - Tham gia vào các cuộc trò chuyện và gửi tin nhắn.
   - Thử nghiệm tính năng quyên góp.

6. Nguyên tắc làm việc

   Dự án Paws Hearts tuân thủ các nguyên tắc phát triển phần mềm sau để đảm bảo chất lượng, khả năng bảo trì và hợp tác hiệu quả:

   - **Mã nguồn sạch và dễ đọc:** Ưu tiên viết mã nguồn rõ ràng, dễ hiểu, tuân thủ các quy tắc định dạng và đặt tên chuẩn của Kotlin.
   - **Kiến trúc module hóa:** Chia nhỏ ứng dụng thành các module độc lập để dễ dàng phát triển, kiểm thử và bảo trì.
   - **Tuân thủ nguyên tắc SOLID:** Áp dụng các nguyên tắc SOLID để thiết kế mã nguồn linh hoạt, có thể mở rộng và dễ dàng thay đổi.
   - **Tối ưu hóa hiệu suất:** Đảm bảo ứng dụng hoạt động mượt mà, phản hồi nhanh chóng và sử dụng tài nguyên hiệu quả.
   - **Bảo mật dữ liệu:** Thực hiện các biện pháp bảo mật cần thiết để bảo vệ thông tin người dùng và dữ liệu ứng dụng.
   - **Kiểm thử đầy đủ:** Viết các bài kiểm thử đơn vị và kiểm thử tích hợp để đảm bảo tính ổn định và chính xác của các tính năng.
   - **Quản lý phiên bản:** Sử dụng Git để quản lý mã nguồn, tuân thủ quy trình Git Flow hoặc Trunk-Based Development để hợp tác hiệu quả.
   - **Tài liệu hóa:** Cung cấp tài liệu rõ ràng cho các phần quan trọng của mã nguồn và kiến trúc ứng dụng.
 
