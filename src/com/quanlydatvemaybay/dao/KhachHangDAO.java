package com.quanlydatvemaybay.dao;

import com.quanlydatvemaybay.config.DatabaseConfig;
import com.quanlydatvemaybay.entity.KhachHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    /** Lấy tất cả khách hàng */
    public List<KhachHang> getAll() {
        String sql = "SELECT * FROM KhachHang ORDER BY Ten";
        List<KhachHang> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Tim kiem khach hang theo ten, sdt, email */
    public List<KhachHang> search(String keyword) {
        String sql = "SELECT * FROM KhachHang " +
                     "WHERE Ten LIKE ? OR Sdt LIKE ? OR Email LIKE ? OR Ho LIKE ? " +
                     "ORDER BY Ten";
        List<KhachHang> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw);
            ps.setString(3, kw); ps.setString(4, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public KhachHang getById(int id) {
        String sql = "SELECT * FROM KhachHang WHERE Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int demKhachHang() {
        String sql = "SELECT COUNT(*) FROM KhachHang";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private KhachHang mapRow(ResultSet rs) throws SQLException {
        KhachHang k = new KhachHang();
        k.setId(rs.getInt("Id"));
        k.setTen(rs.getString("Ten"));
        k.setTenDem(rs.getString("TenDem"));
        k.setHo(rs.getString("Ho"));
        boolean gt = rs.getBoolean("Gioitinh"); if (!rs.wasNull()) k.setGioitinh(gt);
        Date ns = rs.getDate("NgaySinh"); if (ns != null) k.setNgaySinh(ns.toLocalDate());
        k.setEmail(rs.getString("Email"));
        k.setSdt(rs.getString("Sdt"));
        int dt = rs.getInt("Diemthuong"); if (!rs.wasNull()) k.setDiemThuong(dt);
        return k;
    }
}