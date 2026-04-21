package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseConfig;
import com.quanlybanhang.entity.User;

import java.sql.*;

public class UserDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // ── ĐĂNG NHẬP ────────────────────────────────────────────────
    /**
     * Đăng nhập bằng TaiKhoan hoặc Email + MatKhau.
     * Chỉ cho phép tài khoản đang hoạt động (TrangThai = 1).
     */
    public User dangNhap(String taiKhoanOrEmail, String matKhau) {
        String sql = "SELECT * FROM Users " +
                "WHERE (TaiKhoan = ? OR Email = ?) " +
                "  AND MatKhau = ? " +
                "  AND TrangThai = 1";
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
        return u;
    }
}
