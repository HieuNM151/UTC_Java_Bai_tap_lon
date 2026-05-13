-- ============================================================
-- SCRIPT THÊM DỮ LIỆU TEST CHO CHỨC NĂNG QUẢN LÝ SẢN PHẨM
-- Database: DuAn1
-- ============================================================

-- 1. Thêm dữ liệu vào bảng ChatLieu
INSERT INTO [dbo].[ChatLieu] ([Ten]) VALUES (N'Cotton')
INSERT INTO [dbo].[ChatLieu] ([Ten]) VALUES (N'Polyester')
INSERT INTO [dbo].[ChatLieu] ([Ten]) VALUES (N'Linen')
INSERT INTO [dbo].[ChatLieu] ([Ten]) VALUES (N'Silk')
GO

-- 2. Thêm dữ liệu vào bảng MauSac
INSERT INTO [dbo].[MauSac] ([Ten]) VALUES (N'Đỏ')
INSERT INTO [dbo].[MauSac] ([Ten]) VALUES (N'Xanh')
INSERT INTO [dbo].[MauSac] ([Ten]) VALUES (N'Vàng')
INSERT INTO [dbo].[MauSac] ([Ten]) VALUES (N'Trắng')
INSERT INTO [dbo].[MauSac] ([Ten]) VALUES (N'Đen')
INSERT INTO [dbo].[MauSac] ([Ten]) VALUES (N'Hồng')
GO

-- 3. Thêm dữ liệu vào bảng KichCo
INSERT INTO [dbo].[KichCo] ([Ten]) VALUES (N'XS')
INSERT INTO [dbo].[KichCo] ([Ten]) VALUES (N'S')
INSERT INTO [dbo].[KichCo] ([Ten]) VALUES (N'M')
INSERT INTO [dbo].[KichCo] ([Ten]) VALUES (N'L')
INSERT INTO [dbo].[KichCo] ([Ten]) VALUES (N'XL')
INSERT INTO [dbo].[KichCo] ([Ten]) VALUES (N'XXL')
GO

-- 4. Thêm dữ liệu vào bảng NSX
INSERT INTO [dbo].[NSX] ([Ten]) VALUES (N'Việt Nam')
INSERT INTO [dbo].[NSX] ([Ten]) VALUES (N'Trung Quốc')
INSERT INTO [dbo].[NSX] ([Ten]) VALUES (N'Hàn Quốc')
INSERT INTO [dbo].[NSX] ([Ten]) VALUES (N'Thái Lan')
INSERT INTO [dbo].[NSX] ([Ten]) VALUES (N'Nhật Bản')
GO

-- 5. Thêm dữ liệu vào bảng ThuongHieu
INSERT INTO [dbo].[ThuongHieu] ([Ten]) VALUES (N'Nike')
INSERT INTO [dbo].[ThuongHieu] ([Ten]) VALUES (N'Adidas')
INSERT INTO [dbo].[ThuongHieu] ([Ten]) VALUES (N'Puma')
INSERT INTO [dbo].[ThuongHieu] ([Ten]) VALUES (N'Zara')
INSERT INTO [dbo].[ThuongHieu] ([Ten]) VALUES (N'H&M')
INSERT INTO [dbo].[ThuongHieu] ([Ten]) VALUES (N'Gucci')
GO

-- 6. Thêm dữ liệu vào bảng DanhMucSP
INSERT INTO [dbo].[DanhMucSP] ([Ten]) VALUES (N'Áo')
INSERT INTO [dbo].[DanhMucSP] ([Ten]) VALUES (N'Quần')
INSERT INTO [dbo].[DanhMucSP] ([Ten]) VALUES (N'Váy')
INSERT INTO [dbo].[DanhMucSP] ([Ten]) VALUES (N'Giày')
INSERT INTO [dbo].[DanhMucSP] ([Ten]) VALUES (N'Phụ kiện')
GO

-- 7. Thêm dữ liệu vào bảng KhuyenMai
INSERT INTO [dbo].[KhuyenMai] ([Ten], [Ngaybatdau], [Ngayketthuc], [HinhthucKM], [Giatrigiam], [Trangthai])
VALUES (N'Khuyến mãi mùa hè', '2026-04-01', '2026-06-30', N'Giảm %', 10, 1)

INSERT INTO [dbo].[KhuyenMai] ([Ten], [Ngaybatdau], [Ngayketthuc], [HinhthucKM], [Giatrigiam], [Trangthai])
VALUES (N'Khuyến mãi mùa đông', '2025-12-01', '2026-02-28', N'Giảm %', 15, 0)
GO

-- 8. Thêm dữ liệu vào bảng ChitietSP (Sản phẩm test)
INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP001', N'Áo thun Nam Nike', 5, 5, 1, 3, 1, 1, 1, N'Áo thun thể thao', 50, 150000, 299000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP002', N'Quần Jean Nam Adidas', 3, 5, 2, 4, 2, 2, NULL, N'Quần Jean cao cấp', 30, 200000, 449000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP003', N'Áo Phông Nữ Zara', 1, 1, 1, 2, 1, 4, NULL, N'Áo phông màu đỏ', 60, 120000, 249000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP004', N'Váy Hoa Nữ H&M', 2, 2, 3, 3, 1, 5, NULL, N'Váy hoa xinh xắn', 25, 180000, 399000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP005', N'Giày Thể Thao Nike', 5, 1, 4, 3, 2, 1, 1, N'Giày chạy bộ', 15, 500000, 899000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP006', N'Áo Polo Nam Puma', 4, 4, 1, 4, 1, 3, NULL, N'Áo polo trắng sang trọng', 40, 160000, 349000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP007', N'Áo Khoác Jean Nữ', 2, 5, 1, 3, 2, 4, NULL, N'Áo khoác jean xanh', 20, 250000, 549000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP008', N'Quần Legging Nữ', 1, 6, 2, 2, 1, 5, NULL, N'Quần thể thao thoải mái', 80, 80000, 189000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP009', N'Phụ Kiện Đồng Hồ', 5, 1, 5, NULL, NULL, 6, NULL, N'Đồng hồ thời trang', 35, 400000, 899000, NULL)

INSERT INTO [dbo].[ChitietSP]
([Ma], [Ten], [IdNsx], [IdMauSac], [IdDMuc], [IdKC], [IdCL], [IdTH], [IdKM], [MoTa], [SoLuongTon], [GiaNhap], [GiaBan], [QrCode])
VALUES
(N'SP010', N'Mũ Snapback Nam', 3, 2, 5, NULL, 1, 2, NULL, N'Mũ snap back xanh', 70, 50000, 129000, NULL)
GO

-- 9. Kiểm tra dữ liệu đã nhập
SELECT * FROM [dbo].[ChitietSP]
SELECT * FROM [dbo].[MauSac]
SELECT * FROM [dbo].[KichCo]
SELECT * FROM [dbo].[DanhMucSP]
GO

-- ============================================================
-- HƯỚNG DẪN SỬ DỤNG:
-- 1. Mở SQL Server Management Studio
-- 2. Kết nối với database DuAn1
-- 3. Copy toàn bộ script này
-- 4. Paste vào Query Editor
-- 5. Nhấp Execute (hoặc F5)
-- 6. Kiểm tra dữ liệu mới được thêm vào
-- ============================================================
