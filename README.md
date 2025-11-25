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

### 📌 Ý nghĩa tên Paws Hearts

Paws Hearts là sự kết hợp của hai từ mang ý nghĩa sâu sắc và liên quan đến mục đích của dự án:

- **Paws (Bàn chân thú cưng):** Đại diện cho các loài vật nuôi, thú cưng, là đối tượng chính mà ứng dụng hướng đến. Nó gợi lên sự gần gũi, đáng yêu và mong manh của những người bạn bốn chân.
- **Hearts (Trái tim):** Tượng trưng cho tình yêu thương, lòng nhân ái, sự sẻ chia và kết nối giữa những người yêu thú cưng. Nó cũng thể hiện tinh thần cộng đồng, nơi mọi người cùng chung tay chăm sóc và bảo vệ động vật.

Kết hợp lại, **Paws&Hearts** mang ý nghĩa về một nền tảng nơi tình yêu thương và sự quan tâm dành cho thú cưng được lan tỏa, kết nối những trái tim nhân ái để cùng chăm sóc và tạo mái ấm cho những người bạn động vật.

### 🎯 Sơ lược về dự án

Paws Hearts là một ứng dụng di động toàn diện, được thiết kế để kết nối cộng đồng những người yêu thú cưng và hỗ trợ các hoạt động liên quan đến chăm sóc, nhận nuôi và bảo vệ động vật:

- **Cộng đồng sôi nổi:** Người dùng có thể chia sẻ khoảnh khắc, kinh nghiệm chăm sóc, và kết nối với những người có cùng sở thích.
- **Hỗ trợ nhận nuôi:** Nền tảng tạo điều kiện cho việc đăng tin và tìm kiếm thú cưng cần mái ấm mới, giúp động vật cơ nhỡ tìm được gia đình yêu thương.
- **Quản lý thông tin thú cưng:** Cung cấp công cụ để người dùng theo dõi lịch sử tiêm chủng, khám bệnh và các thông tin quan trọng khác của thú cưng.
- **Quyên góp và hỗ trợ:** Tính năng quyên góp giúp các tổ chức và cá nhân có thể kêu gọi hoặc đóng góp cho các hoạt động từ thiện, hỗ trợ động vật gặp khó khăn.
- **Tương tác đa dạng:** Người dùng có thể tham gia vào các hoạt động, sự kiện, gửi tin nhắn và tương tác qua lại để xây dựng một cộng đồng vững mạnh.

## 2. Lý do lựa chọn dự án

   Dự án Paws Hearts được lựa chọn với nhiều lý do thiết thực, mang lại giá trị cho cộng đồng và cơ hội rèn luyện kỹ năng phát triển:

   - **Thiết thực và có tác động xã hội:**
     - Hỗ trợ giải quyết vấn đề thú cưng bị bỏ rơi, giúp chúng tìm được mái ấm mới thông qua tính năng nhận nuôi.
     - Xây dựng một cộng đồng vững mạnh, nơi những người yêu thú cưng có thể chia sẻ kiến thức, kinh nghiệm, và hỗ trợ lẫn nhau trong việc chăm sóc thú cưng.
     - Thúc đẩy các hoạt động từ thiện, quyên góp cho các tổ chức bảo vệ động vật, góp phần nâng cao ý thức cộng đồng về trách nhiệm với thú cưng.
   - **Tập trung vào minh bạch & tin cậy:**
     - Đảm bảo tính minh bạch trong các hoạt động cộng đồng và quyên góp, tạo niềm tin cho người dùng.
     - Cung cấp các công cụ quản lý thông tin thú cưng rõ ràng, giúp chủ nuôi dễ dàng theo dõi sức khỏe và tình trạng của vật nuôi.
   - **Rèn luyện kỹ năng phát triển di động hiện đại:**
     - Sử dụng Kotlin và Jetpack Compose, những công nghệ mới nhất và mạnh mẽ nhất cho phát triển ứng dụng Android.
     - Thực hành xây dựng ứng dụng với kiến trúc MVVM, Room Database, DataStore và tích hợp Firebase mạnh mẽ (Authentication, Firestore, Storage).
     - Nâng cao kỹ năng về quản lý trạng thái, tương tác người dùng, và tối ưu hóa hiệu suất ứng dụng di động.

## 3. Công nghệ sử dụng

   Dự án Paws Hearts được xây dựng trên nền tảng các công nghệ hiện đại và mạnh mẽ để đảm bảo hiệu suất, khả năng mở rộng và trải nghiệm người dùng tốt nhất:

   ### ✨ Phát triển di động (Mobile Development)

   - **Ngôn ngữ lập trình:** Kotlin
   - **Framework UI:** Jetpack Compose
   - **Kiến trúc ứng dụng:** MVVM (Model-View-ViewModel) với Android Architecture Components (Lifecycle, ViewModel, Room)
   - **Cơ sở dữ liệu cục bộ:** Room Persistence Library và Proto DataStore
   - **Quản lý bất đồng bộ:** Kotlin Coroutines và Flow
   - **Tải và hiển thị hình ảnh:** Coil Compose

   ### 🌐 Backend as a Service (BaaS)

   - **Google Firebase:**
     - Authentication: Quản lý người dùng và xác thực.
     - Firestore Database: Cơ sở dữ liệu NoSQL linh hoạt và có khả năng mở rộng để lưu trữ dữ liệu ứng dụng.
     - Cloud Storage: Lưu trữ và quản lý các tệp phương tiện (hình ảnh, video) của người dùng.
     - Analytics: Thu thập dữ liệu phân tích để hiểu hành vi người dùng và cải thiện ứng dụng.

   ### 🔧 Công cụ phát triển & Triển khai (Development Tools & Deployment)

   - **Hệ thống kiểm soát phiên bản:** Git, GitHub
   - **Môi trường phát triển tích hợp (IDE):** Android Studio
   - **Triển khai:** Firebase CLI (dành cho quản lý và triển khai các dịch vụ Firebase)

## 4. Các tính năng chính

   Ứng dụng Paws Hearts cung cấp các tính năng đa dạng nhằm kết nối và hỗ trợ cộng đồng yêu thú cưng:

   ### 🛡️ Xác thực & Quản lý tài khoản

   - **Đăng ký tài khoản:** Người dùng có thể đăng ký tài khoản mới bằng email và mật khẩu hoặc thông qua tài khoản Google.
   - **Đăng nhập:** Đăng nhập an toàn với các phương thức xác thực đã đăng ký.
   - **Quản lý thông tin tài khoản:** Xem và cập nhật thông tin cá nhân, ảnh đại diện, và các thiết lập tài khoản khác.
   - **Quên/Đặt lại mật khẩu:** Hỗ trợ khôi phục mật khẩu qua email khi người dùng quên.
   - **Bảo mật:** Dữ liệu người dùng được bảo vệ thông qua Firebase Authentication và các quy tắc bảo mật của Firestore.

   ### 👥 Quản lý hồ sơ & Thú cưng

   - **Hồ sơ cá nhân:** Tạo và quản lý hồ sơ cá nhân chi tiết, bao gồm thông tin liên hệ và giới thiệu bản thân.
   - **Quản lý thú cưng:** Thêm, chỉnh sửa và xóa thông tin về thú cưng của bạn (tên, loài, giống, ngày sinh, lịch sử y tế, ảnh).

   ### 📝 Bài viết & Tương tác cộng đồng

   - **Tạo bài viết:** Chia sẻ hình ảnh, video và văn bản về thú cưng của bạn hoặc các hoạt động cộng đồng.
   - **Trang chủ:** Hiển thị các bài viết mới nhất và phổ biến từ cộng đồng.
   - **Bình luận & Thích:** Tương tác với các bài viết thông qua bình luận và biểu tượng cảm xúc.
   - **Bài viết của tôi:** Xem và quản lý tất cả các bài viết của bạn.

   ### 🐾 Nhận nuôi thú cưng

   - **Đăng bài nhận nuôi:** Đăng tin về thú cưng cần nhận nuôi, cung cấp thông tin chi tiết và hình ảnh.
   - **Tìm kiếm thú cưng:** Duyệt qua danh sách thú cưng cần nhận nuôi, lọc theo các tiêu chí (loài, giống, địa điểm).
   - **Chi tiết thú cưng:** Xem thông tin chi tiết về từng thú cưng, bao gồm thông tin liên hệ của người đăng.

   ### 🤝 Hoạt động cộng đồng

   - **Tạo hoạt động:** Tổ chức và quản lý các sự kiện, buổi gặp mặt hoặc hoạt động từ thiện liên quan đến thú cưng.
   - **Tham gia hoạt động:** Tìm kiếm và đăng ký tham gia các hoạt động cộng đồng.
   - **Chi tiết hoạt động:** Xem thông tin chi tiết về từng hoạt động, bao gồm thời gian, địa điểm và mô tả.

   ### 💬 Hệ thống tin nhắn

   - **Trò chuyện trực tiếp:** Giao tiếp riêng tư với người dùng khác trong ứng dụng.
   - **Quản lý cuộc trò chuyện:** Xem danh sách các cuộc trò chuyện và lịch sử tin nhắn.
   - **Thông báo tin nhắn mới:** Nhận thông báo khi có tin nhắn mới.

   ### 💰 Quyên góp

   - **Quyên góp cho tổ chức/cá nhân:** Hỗ trợ tài chính cho các hoạt động bảo vệ động vật hoặc các trường hợp thú cưng cần giúp đỡ.
   - **Lịch sử quyên góp:** Theo dõi các khoản quyên góp của bạn.

   ### 🔔 Thông báo

   - **Thông báo trong ứng dụng:** Nhận các thông báo về tin nhắn mới, hoạt động, bình luận và các sự kiện quan trọng khác.
   - **Quản lý thông báo:** Tùy chỉnh cài đặt thông báo.

   ### ⚙️ Yêu cầu khác (phi kỹ thuật)

   - **An toàn & tốc độ:** Đảm bảo quyền riêng tư và bảo mật dữ liệu người dùng, tối ưu hóa hiệu suất ứng dụng để có trải nghiệm mượt mà.
   - **Giao diện người dùng (UI):** Thiết kế thân thiện, dễ sử dụng, với khả năng hỗ trợ chế độ sáng/tối (Dark/Light Mode) và hiển thị hình ảnh rõ nét.
   - **Ngôn ngữ:** Hỗ trợ đa ngôn ngữ (nếu có).


## 5. Cấu trúc dự án

Dự án Paws Hearts được tổ chức theo cấu trúc module rõ ràng để dễ dàng phát triển và bảo trì. Dưới đây là các thư mục và thành phần chính:

```
Paws-Hearts/
├── app/                      # Module ứng dụng chính
│   ├── build/                # Kết quả build của ứng dụng
│   ├── src/                  # Mã nguồn của ứng dụng
│   │   ├── main/             # Mã nguồn chính
│   │   │   ├── AndroidManifest.xml # File manifest của Android
│   │   │   ├── java/         # Mã nguồn Kotlin/Java
│   │   │   │   └── com/example/pawshearts/
│   │   │   │       ├── activities/   # Module quản lý các hoạt động cộng đồng
│   │   │   │       ├── adopt/        # Module liên quan đến tính năng nhận nuôi thú cưng
│   │   │   │       ├── auth/         # Module quản lý xác thực người dùng (đăng ký, đăng nhập)
│   │   │   │       ├── data/         # Lớp dữ liệu (repository, model, local database)
│   │   │   │       ├── donate/       # Module tính năng quyên góp
│   │   │   │       ├── messages/     # Module hệ thống tin nhắn
│   │   │   │       ├── navmodel/     # Định nghĩa điều hướng và cấu trúc ứng dụng
│   │   │   │       ├── notification/ # Module quản lý thông báo
│   │   │   │       ├── post/         # Module quản lý bài viết và tương tác
│   │   │   │       ├── profile/      # Module quản lý hồ sơ người dùng
│   │   │   │       ├── setting/      # Module cài đặt ứng dụng
│   │   │   │       ├── ui/           # Các thành phần UI chung và theme
│   │   │   │       └── ...           # Các tệp Kotlin cấp cao (MainActivity, MyApplication, SplashScreen)
│   │   │   └── res/              # Tài nguyên ứng dụng (layout, drawable, values)
│   ├── build.gradle.kts      # Cấu hình Gradle cho module ứng dụng
│   └── ...                   # Các tệp khác của module ứng dụng
├── build.gradle.kts          # Cấu hình Gradle cấp độ dự án
├── firebase.json             # Cấu hình Firebase Hosting/Functions
├── firestore-debug.log       # Nhật ký debug của Firestore
├── gradle/                   # Tệp wrapper của Gradle
├── gradlew                   # Script Gradle Wrapper cho Linux/macOS
├── gradlew.bat               # Script Gradle Wrapper cho Windows
├── local.properties          # Thuộc tính cục bộ (SDK location, private keys)
├── settings.gradle.kts       # Cấu hình module dự án Gradle
└── README.md                 # Tài liệu dự án
```


## 6. Cài đặt app & Demo

   **Cài đặt app:**
     
   **Video demo app:**


