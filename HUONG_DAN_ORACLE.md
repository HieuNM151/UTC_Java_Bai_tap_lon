# Hướng dẫn chạy Oracle (Docker) và nạp dữ liệu từ đầu đến cuối

Tài liệu này giúp bạn (hoặc máy khác) cài Oracle bằng Docker, tạo user/schema, chạy DDL, chèn dữ liệu mẫu và chạy ứng dụng Java.

---

## 0. Yêu cầu trước khi bắt đầu

| Thành phần | Ghi chú |
|-------------|---------|
| **Docker Desktop** (Windows/macOS) hoặc Docker Engine (Linux) | Bật dịch vụ Docker |
| **JDK 11+** (project dùng Maven compiler target 14; JDK 17/20 đều được) | `java -version` |
| **IntelliJ IDEA** (tuỳ chọn) | Mở project Maven |
| **Git** | Clone repo về máy |

---

## 1. Clone project

```bash
git clone <URL-repo-cua-ban> UTC_Java_Bai_tap_lon
cd UTC_Java_Bai_tap_lon
```

Trong thư mục project cần có ít nhất:

- `db_oracle.sql` — tạo bảng, khóa, ràng buộc
- `INSERT_TEST_DATA.sql` — dữ liệu mẫu + tài khoản đăng nhập
- `src/com/quanlybanhang/...` — mã nguồn
- `pom.xml` — dependency `ojdbc8`

---

## 2. Chạy Oracle bằng Docker (image khuyến nghị: `gvenzl/oracle-free`)

Image này nhẹ, không cần đăng ký Oracle, PDB mặc định là **`FREEPDB1`**, biến môi trường mật khẩu SYS là **`ORACLE_PASSWORD`**.

```bash
docker run -d \
  --name oracle-db \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=abc123 \
  gvenzl/oracle-free:latest
```

Chờ database sẵn sàng (lần đầu có thể vài phút):

```bash
docker logs -f oracle-db
```

Khi thấy dòng kiểu **"DATABASE IS READY TO USE!"** (hoặc tương đương) thì nhấn `Ctrl+C` để thoát theo dõi log (container vẫn chạy).

### Nếu dùng image khác

| Image | Service / PDB thường dùng | Biến mật khẩu SYS |
|--------|---------------------------|------------------|
| `gvenzl/oracle-free` | **FREEPDB1** | `ORACLE_PASSWORD` |
| Oracle XE 21c (`container-registry.oracle.com/database/express:21.3.0-xe`) | **XEPDB1** | `ORACLE_PWD` |
| Oracle Free chính thức (`container-registry.oracle.com/database/free:latest`) | **FREEPDB1** | `ORACLE_PWD` |

Chuỗi JDBC phải khớp **đúng service name** của image bạn chạy:

```text
jdbc:oracle:thin:@//<HOST>:1521/<SERVICE_NAME>
```

Ví dụ máy local: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`

---

## 3. Tạo user ứng dụng (schema) trên PDB

Kết nối bằng **SYS** vào đúng PDB (với `gvenzl/oracle-free` dùng **FREEPDB1**):

```bash
docker exec -i oracle-db sqlplus -S sys/abc123@//localhost:1521/FREEPDB1 as sysdba <<'SQL'
CREATE USER quanlybh IDENTIFIED BY abc123
  DEFAULT TABLESPACE USERS
  QUOTA UNLIMITED ON USERS;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE,
      CREATE TRIGGER, CREATE VIEW TO quanlybh;

EXIT;
SQL
```

- Đổi `abc123` sau `sys/` thành đúng giá trị bạn đặt ở `ORACLE_PASSWORD`.
- User/schema ứng dụng: **`quanlybh`**, mật khẩu: **`abc123`** (đổi nếu muốn, nhớ sửa trong `DatabaseConfig.java`).

### Lỗi `ORA-01920: user name ... conflicts`

User đã tồn tại. Hoặc **đổi mật khẩu** để khớp với app:

```bash
docker exec -i oracle-db sqlplus -S sys/abc123@//localhost:1521/FREEPDB1 as sysdba <<'SQL'
ALTER USER quanlybh IDENTIFIED BY abc123 ACCOUNT UNLOCK;
EXIT;
SQL
```

Hoặc **xóa user cũ** rồi tạo lại (mất toàn bộ bảng/dữ liệu của user đó):

```bash
docker exec -i oracle-db sqlplus -S sys/abc123@//localhost:1521/FREEPDB1 as sysdba <<'SQL'
BEGIN
  FOR s IN (SELECT sid, serial# FROM v$session WHERE username = 'QUANLYBH') LOOP
    EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION ''' || s.sid || ',' || s.serial# || ''' IMMEDIATE';
  END LOOP;
END;
/
DROP USER quanlybh CASCADE;
CREATE USER quanlybh IDENTIFIED BY abc123 DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER, CREATE VIEW TO quanlybh;
EXIT;
SQL
```

---

## 4. Chạy DDL — tạo bảng (`db_oracle.sql`)

Từ máy có file project (đường dẫn tùy bạn):

```bash
cd /đường/dẫn/UTC_Java_Bai_tap_lon

docker cp db_oracle.sql oracle-db:/tmp/db_oracle.sql

docker exec -i oracle-db sqlplus quanlybh/abc123@//localhost:1521/FREEPDB1 @/tmp/db_oracle.sql
```

Kỳ vọng: nhiều dòng `Table created.` và cuối có `Commit complete.`

### Kiểm tra đã có 12 bảng

```bash
docker exec -i oracle-db sqlplus -S quanlybh/abc123@//localhost:1521/FREEPDB1 <<'SQL'
SELECT table_name FROM user_tables ORDER BY table_name;
EXIT;
SQL
```

Danh sách gồm: `CHATLIEU`, `CHITIETSP`, `CHUCVU`, `DANHMUCSP`, `HOADON`, `HOADONCHITIET`, `KHACHHANG`, `KHUYENMAI`, `KICHCO`, `MAUSAC`, `NSX`, `THUONGHIEU`, `USERS`.

---

## 5. Chèn dữ liệu mẫu (`INSERT_TEST_DATA.sql`)

```bash
docker cp INSERT_TEST_DATA.sql oracle-db:/tmp/INSERT_TEST_DATA.sql

docker exec -i oracle-db sqlplus quanlybh/abc123@//localhost:1521/FREEPDB1 @/tmp/INSERT_TEST_DATA.sql
```

Script này thêm danh mục, màu, size, NSX, thương hiệu, khuyến mãi, **Chức vụ**, **User đăng nhập**, và khoảng 10 dòng `ChitietSP`.

### Đăng nhập ứng dụng (mặc định trong script)

- **Tài khoản:** `admin`  
- **Mật khẩu:** `123`  

(Nếu bạn đổi mật khẩu trong SQL, cập nhật tương ứng khi đăng nhập.)

---

## 6. Cấu hình ứng dụng Java

Mở `src/com/quanlybanhang/config/DatabaseConfig.java` và chỉnh cho khớp máy bạn:

| Hằng số | Ý nghĩa | Ví dụ |
|---------|---------|--------|
| `HOST` | Máy chạy Oracle | `localhost` hoặc IP server |
| `PORT` | Cổng listener | `1521` |
| `SERVICE_NAME` | Tên service PDB | `FREEPDB1` (gvenzl/free) hoặc `XEPDB1` (XE) |
| `USERNAME` / `PASSWORD` | User schema | `quanlybh` / `abc123` |

URL JDBC có dạng:

```java
"jdbc:oracle:thin:@//" + HOST + ":" + PORT + "/" + SERVICE_NAME
```

---

## 7. Chạy project (IntelliJ hoặc Maven)

### IntelliJ

1. **File → Open** → chọn thư mục `UTC_Java_Bai_tap_lon`.
2. Khi có banner Maven → **Load Maven Project** (hoặc chuột phải `pom.xml` → **Maven → Reload Project**).
3. Đợi tải dependency `ojdbc8`.
4. Mở `src/com/quanlybanhang/Main.java` → nút **Run** ▶.

### Maven (terminal)

```bash
cd /đường/dẫn/UTC_Java_Bai_tap_lon
mvn -q compile exec:java -Dexec.mainClass=com.quanlybanhang.Main
```

---

## 8. Lệnh Docker hữu ích

```bash
docker ps                      # container đang chạy
docker stop oracle-db          # tắt
docker start oracle-db         # bật lại (giữ volume nếu có mount)
docker logs --tail 50 oracle-db
docker rm -f oracle-db         # xóa container (mất data trong container nếu không mount volume)
```

---

## 9. Lỗi thường gặp

| Mã / triệu chứng | Nguyên nhân | Cách xử lý |
|------------------|-------------|------------|
| **ORA-12541** TNS:no listener | Container chưa sẵn sàng hoặc chưa map port | Đợi log READY; kiểm tra `-p 1521:1521` |
| **ORA-12514** service … not known | Sai `SERVICE_NAME` | Đổi `FREEPDB1` / `XEPDB1` cho đúng image |
| **ORA-01017** invalid username/password | Sai user/pass hoặc user chưa tạo trên đúng PDB | Làm lại mục 3; đồng bộ `DatabaseConfig` |
| **ORA-01940** cannot drop user currently connected | App/IDE vẫn mở kết nối `quanlybh` | Tắt app IntelliJ; hoặc dùng block `KILL SESSION` ở mục 3 |
| **ORA-00932** inconsistent datatypes: CLOB | Schema cũ dùng CLOB cho cột so sánh `=` | Dùng `db_oracle.sql` mới nhất (đã dùng `VARCHAR2` cho Email/TaiKhoan/MatKhau); drop user + tạo lại nếu cần |
| **`exec: " sqlplus"` not found** | Có khoảng trắng sau `\` khi nối dòng shell | Viết **một dòng** `docker exec ... sqlplus ...` hoặc `\` sát sạt với ký tự tiếp theo |

---

## 10. Checklist nhanh (copy khi sang máy mới)

1. [ ] Cài Docker, clone repo  
2. [ ] `docker run ... gvenzl/oracle-free` + chờ READY  
3. [ ] `CREATE USER quanlybh` + `GRANT` (SYS vào FREEPDB1)  
4. [ ] `@/tmp/db_oracle.sql`  
5. [ ] `@/tmp/INSERT_TEST_DATA.sql`  
6. [ ] Sửa `DatabaseConfig` (host/port/service/user/pass)  
7. [ ] IntelliJ: Reload Maven → Run `Main`  
8. [ ] Đăng nhập `admin` / `123`  

---

*Tài liệu phản ánh cấu hình mặc định trong project: Oracle Free Docker, PDB **FREEPDB1**, user **quanlybh**.*
