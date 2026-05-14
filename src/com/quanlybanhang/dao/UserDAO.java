package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseConfig;
import com.quanlybanhang.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // ── LẤY TẤT CẢ NHÂN VIÊN ────────────────────────────────────────
    /** Lấy tất cả nhân viên */
    public List<User> getAll() {
        String sql = "SELECT u.*, cv.Ten AS TenChucVu FROM Users u " +
                "LEFT JOIN ChucVu cv ON u.IdCV = cv.Id " +
                "WHERE u.TrangThai = 1 ORDER BY u.Ten";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── ĐĂNG NHẬP ────────────────────────────────────────────────
    /**
     * Đăng nhập bằng TaiKhoan hoặc Email + MatKhau.
     * Chỉ cho phép tài khoản đang hoạt động (TrangThai = 1).
     */
    public User dangNhap(String taiKhoanOrEmail, String matKhau) {
        String sql = "SELECT u.*, cv.Ten AS TenChucVu FROM Users u " +
                "LEFT JOIN ChucVu cv ON u.IdCV = cv.Id " +
                "WHERE (u.TaiKhoan = ? OR u.Email = ?) " +
                "  AND u.MatKhau = ? " +
                "  AND u.TrangThai = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, taiKhoanOrEmail);
            ps.setString(2, taiKhoanOrEmail);
            ps.setString(3, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── ĐĂNG KÝ ──────────────────────────────────────────────────
    /** Tạo tài khoản mới, trả về true nếu thành công */
    public boolean dangKy(User u) {
        String sql = "INSERT INTO Users (Ten, TenDem, Ho, Gioitinh, Sdt, IdCV, " +
                "TaiKhoan, MatKhau, Email, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getTen());
            ps.setString(2, u.getTenDem());
            ps.setString(3, u.getHo());
            if (u.getGioitinh() != null) ps.setBoolean(4, u.getGioitinh());
            else ps.setNull(4, Types.BIT);
            ps.setString(5, u.getSdt());
            if (u.getIdCV() != null) ps.setInt(6, u.getIdCV());
            else ps.setNull(6, Types.INTEGER);
            ps.setString(7, u.getTaiKhoan());
            ps.setString(8, u.getMatKhau());
            ps.setString(9, u.getEmail());
            ps.setBoolean(10, u.getTrangThai() != null && u.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── KIỂM TRA TỒN TẠI ─────────────────────────────────────────
    /** Kiểm tra tài khoản / email có tồn tại không (kể cả bị khoá) */
    public boolean existsTaiKhoan(String taiKhoanOrEmail) {
        String sql = "SELECT 1 FROM Users WHERE TaiKhoan = ? OR Email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, taiKhoanOrEmail);
            ps.setString(2, taiKhoanOrEmail);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Kiểm tra email đã được dùng chưa */
    public boolean existsEmail(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Đếm tài khoản đang hoạt động có quyền quản trị (theo logic {@link User#isAdmin()}). */
    public long countActiveAdmins() {
        return getAll().stream().filter(User::isAdmin).count();
    }

    public static final class ChucVuOption {
        public final int id;
        public final String ten;
        public ChucVuOption(int id, String ten) {
            this.id = id;
            this.ten = ten;
        }
        public boolean isAdminRole() {
            if (ten == null) return id == 1;
            String t = ten.trim().toLowerCase(java.util.Locale.ROOT);
            return id == 1 || t.contains("admin") || t.contains("quản trị") || t.contains("quan tri");
        }
        @Override public String toString() { return ten != null ? ten : ""; }
    }

    public List<ChucVuOption> listChucVu() {
        String sql = "SELECT Id, Ten FROM ChucVu ORDER BY Id";
        List<ChucVuOption> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new ChucVuOption(rs.getInt("Id"), rs.getString("Ten")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public User getById(int id) {
        String sql = "SELECT u.*, cv.Ten AS TenChucVu FROM Users u " +
                "LEFT JOIN ChucVu cv ON u.IdCV = cv.Id WHERE u.Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Cập nhật người dùng. {@code newPassword} null hoặc rỗng = giữ mật khẩu cũ. */
    public boolean capNhat(User u, String newPassword) {
        boolean changePwd = newPassword != null && !newPassword.isBlank();
        String sql = changePwd
                ? "UPDATE Users SET Ten=?, TenDem=?, Ho=?, Gioitinh=?, Sdt=?, IdCV=?, TaiKhoan=?, Email=?, MatKhau=? WHERE Id=?"
                : "UPDATE Users SET Ten=?, TenDem=?, Ho=?, Gioitinh=?, Sdt=?, IdCV=?, TaiKhoan=?, Email=? WHERE Id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, u.getTen());
            ps.setString(i++, u.getTenDem());
            ps.setString(i++, u.getHo());
            if (u.getGioitinh() != null) ps.setBoolean(i++, u.getGioitinh());
            else ps.setNull(i++, Types.BIT);
            ps.setString(i++, u.getSdt());
            if (u.getIdCV() != null) ps.setInt(i++, u.getIdCV());
            else ps.setNull(i++, Types.INTEGER);
            ps.setString(i++, u.getTaiKhoan());
            ps.setString(i++, u.getEmail());
            if (changePwd) ps.setString(i++, newPassword);
            ps.setInt(i, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Đặt TrangThai = 0 (không xóa bản ghi). */
    public boolean voHieuHoa(int userId) {
        String sql = "UPDATE Users SET TrangThai = 0 WHERE Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsTaiKhoanForOther(String taiKhoan, int excludeUserId) {
        String sql = "SELECT 1 FROM Users WHERE TaiKhoan = ? AND Id <> ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existsEmailForOther(String email, int excludeUserId) {
        String sql = "SELECT 1 FROM Users WHERE Email = ? AND Id <> ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── MAP ROW ───────────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("Id"));
        u.setTen(rs.getString("Ten"));
        u.setTenDem(rs.getString("TenDem"));
        u.setHo(rs.getString("Ho"));
        Date ngaySinh = rs.getDate("NgaySinh");
        if (ngaySinh != null) u.setNgaySinh(ngaySinh.toLocalDate());
        boolean gt = rs.getBoolean("Gioitinh");
        if (!rs.wasNull()) u.setGioitinh(gt);
        u.setSdt(rs.getString("Sdt"));
        int idCV = rs.getInt("IdCV");
        if (!rs.wasNull()) u.setIdCV(idCV);
        u.setTaiKhoan(rs.getString("TaiKhoan"));
        u.setMatKhau(rs.getString("MatKhau"));
        u.setEmail(rs.getString("Email"));
        boolean tt = rs.getBoolean("TrangThai");
        if (!rs.wasNull()) u.setTrangThai(tt);
        String cv = getStringColumnIgnoreCase(rs, "TenChucVu");
        if (cv != null) u.setTenChucVu(cv);
        return u;
    }

    /** Oracle có thể trả nhãn cột dạng TENCHUCVU — đọc theo meta không phân biệt hoa thường. */
    private static String getStringColumnIgnoreCase(ResultSet rs, String logicalName) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            if (logicalName.equalsIgnoreCase(md.getColumnLabel(i)))
                return rs.getString(i);
        }
        return null;
    }
}