-- ============================================================
-- SCRIPT THEM DU LIEU TEST CHO CHUC NANG QUAN LY SAN PHAM
-- Database: Oracle (schema quanlybh) — PDB FREEPDB1
--
-- Chay bang:
--   docker cp INSERT_TEST_DATA.sql oracle-db:/tmp/INSERT_TEST_DATA.sql
--   docker exec -i oracle-db sqlplus quanlybh/abc123@//localhost:1521/FREEPDB1 @/tmp/INSERT_TEST_DATA.sql
-- ============================================================

-- 1. ChatLieu
INSERT INTO ChatLieu (Ten) VALUES (N'Cotton');
INSERT INTO ChatLieu (Ten) VALUES (N'Polyester');
INSERT INTO ChatLieu (Ten) VALUES (N'Linen');
INSERT INTO ChatLieu (Ten) VALUES (N'Silk');

-- 2. MauSac
INSERT INTO MauSac (Ten) VALUES (N'Đỏ');
INSERT INTO MauSac (Ten) VALUES (N'Xanh');
INSERT INTO MauSac (Ten) VALUES (N'Vàng');
INSERT INTO MauSac (Ten) VALUES (N'Trắng');
INSERT INTO MauSac (Ten) VALUES (N'Đen');
INSERT INTO MauSac (Ten) VALUES (N'Hồng');

-- 3. KichCo
INSERT INTO KichCo (Ten) VALUES ('XS');
INSERT INTO KichCo (Ten) VALUES ('S');
INSERT INTO KichCo (Ten) VALUES ('M');
INSERT INTO KichCo (Ten) VALUES ('L');
INSERT INTO KichCo (Ten) VALUES ('XL');
INSERT INTO KichCo (Ten) VALUES ('XXL');

-- 4. NSX
INSERT INTO NSX (Ten) VALUES (N'Việt Nam');
INSERT INTO NSX (Ten) VALUES (N'Trung Quốc');
INSERT INTO NSX (Ten) VALUES (N'Hàn Quốc');
INSERT INTO NSX (Ten) VALUES (N'Thái Lan');
INSERT INTO NSX (Ten) VALUES (N'Nhật Bản');

-- 5. ThuongHieu
INSERT INTO ThuongHieu (Ten) VALUES (N'Nike');
INSERT INTO ThuongHieu (Ten) VALUES (N'Adidas');
INSERT INTO ThuongHieu (Ten) VALUES (N'Puma');
INSERT INTO ThuongHieu (Ten) VALUES (N'Zara');
INSERT INTO ThuongHieu (Ten) VALUES (N'H&M');
INSERT INTO ThuongHieu (Ten) VALUES (N'Gucci');

-- 6. DanhMucSP
INSERT INTO DanhMucSP (Ten) VALUES (N'Áo');
INSERT INTO DanhMucSP (Ten) VALUES (N'Quần');
INSERT INTO DanhMucSP (Ten) VALUES (N'Váy');
INSERT INTO DanhMucSP (Ten) VALUES (N'Giày');
INSERT INTO DanhMucSP (Ten) VALUES (N'Phụ kiện');

-- 7. KhuyenMai
INSERT INTO KhuyenMai (Ten, Ngaybatdau, Ngayketthuc, HinhthucKM, Giatrigiam, Trangthai)
VALUES (N'Khuyến mãi mùa hè', DATE '2026-04-01', DATE '2026-06-30', N'Giảm %', 10, 1);
INSERT INTO KhuyenMai (Ten, Ngaybatdau, Ngayketthuc, HinhthucKM, Giatrigiam, Trangthai)
VALUES (N'Khuyến mãi mùa đông', DATE '2025-12-01', DATE '2026-02-28', N'Giảm %', 15, 0);

-- 8. ChucVu + Users (tài khoản admin để đăng nhập)
INSERT INTO ChucVu (Ten) VALUES (N'Admin');
INSERT INTO ChucVu (Ten) VALUES (N'Nhân viên');
INSERT INTO Users (Ten, Ho, Email, TaiKhoan, MatKhau, IdCV, TrangThai)
VALUES (N'Khanh', N'Nguyễn', 'admin@local', 'admin', '123', 1, 1);

-- 9. ChitietSP (10 sản phẩm mẫu)
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP001', N'Áo thun Nam Nike', 5, 5, 1, 3, 1, 1, 1, N'Áo thun thể thao', 50, 150000, 299000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP002', N'Quần Jean Nam Adidas', 3, 5, 2, 4, 2, 2, NULL, N'Quần Jean cao cấp', 30, 200000, 449000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP003', N'Áo Phông Nữ Zara', 1, 1, 1, 2, 1, 4, NULL, N'Áo phông màu đỏ', 60, 120000, 249000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP004', N'Váy Hoa Nữ H&M', 2, 2, 3, 3, 1, 5, NULL, N'Váy hoa xinh xắn', 25, 180000, 399000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP005', N'Giày Thể Thao Nike', 5, 1, 4, 3, 2, 1, 1, N'Giày chạy bộ', 15, 500000, 899000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP006', N'Áo Polo Nam Puma', 4, 4, 1, 4, 1, 3, NULL, N'Áo polo trắng sang trọng', 40, 160000, 349000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP007', N'Áo Khoác Jean Nữ', 2, 5, 1, 3, 2, 4, NULL, N'Áo khoác jean xanh', 20, 250000, 549000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP008', N'Quần Legging Nữ', 1, 6, 2, 2, 1, 5, NULL, N'Quần thể thao thoải mái', 80, 80000, 189000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP009', N'Phụ Kiện Đồng Hồ', 5, 1, 5, NULL, NULL, 6, NULL, N'Đồng hồ thời trang', 35, 400000, 899000, NULL);
INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode)
VALUES ('SP010', N'Mũ Snapback Nam', 3, 2, 5, NULL, 1, 2, NULL, N'Mũ snap back xanh', 70, 50000, 129000, NULL);

COMMIT;

-- 10. Kiểm tra
SET LINESIZE 200
COLUMN Ma     FORMAT A8
COLUMN Ten    FORMAT A30
COLUMN GiaBan FORMAT 999,999,999
SELECT Id, Ma, Ten, SoLuongTon, GiaBan FROM ChitietSP ORDER BY Id;
SELECT * FROM MauSac;
SELECT * FROM KichCo;
SELECT * FROM DanhMucSP;
EXIT;
