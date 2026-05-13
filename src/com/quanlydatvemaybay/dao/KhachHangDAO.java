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

    /** Thêm khách hàng mới */
    public boolean add(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (Ho, TenDem, Ten, Gioitinh, NgaySinh, Email, Sdt, Diemthuong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, kh.getHo());
            ps.setString(2, kh.getTenDem());
            ps.setString(3, kh.getTen());
            ps.setObject(4, kh.getGioitinh());
            ps.setObject(5, kh.getNgaySinh());
            ps.setString(6, kh.getEmail());
            ps.setString(7, kh.getSdt());
            ps.setObject(8, kh.getDiemThuong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật khách hàng */
    public boolean update(KhachHang kh) {
        String sql = "UPDATE KhachHang SET Ho=?, TenDem=?, Ten=?, Gioitinh=?, NgaySinh=?, Email=?, Sdt=?, Diemthuong=? WHERE Id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, kh.getHo());
            ps.setString(2, kh.getTenDem());
            ps.setString(3, kh.getTen());
            ps.setObject(4, kh.getGioitinh());
            ps.setObject(5, kh.getNgaySinh());
            ps.setString(6, kh.getEmail());
            ps.setString(7, kh.getSdt());
            ps.setObject(8, kh.getDiemThuong());
            ps.setInt(9, kh.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa khách hàng */
    public boolean delete(int id) {
        String sql = "DELETE FROM KhachHang WHERE Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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