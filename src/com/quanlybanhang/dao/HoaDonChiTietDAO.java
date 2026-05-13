package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseConfig;
import com.quanlybanhang.entity.HoaDonChiTiet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonChiTietDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    /** Lấy chi tiết hóa đơn theo ID hóa đơn */
    public List<HoaDonChiTiet> getByHoaDonId(int idHoaDon) {
        String sql = "SELECT ct.IdHD, ct.IdCTSP, ct.Soluong, ct.Dongia, " +
                     "sp.Ma AS MaSP, sp.Ten AS TenSP, ms.Ten AS TenMauSac, kc.Ten AS TenKichCo " +
                     "FROM HoaDonChiTiet ct " +
                     "INNER JOIN ChitietSP sp ON ct.IdCTSP = sp.Id " +
                     "LEFT JOIN MauSac ms ON sp.IdMauSac = ms.Id " +
                     "LEFT JOIN KichCo kc ON sp.IdKC = kc.Id " +
                     "WHERE ct.IdHD = ? " +
                     "ORDER BY ct.IdCTSP ASC";

        List<HoaDonChiTiet> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHoaDonChiTiet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private HoaDonChiTiet mapHoaDonChiTiet(ResultSet rs) throws SQLException {
        HoaDonChiTiet ct = new HoaDonChiTiet();
        ct.setIdHD(rs.getInt("IdHD"));
        ct.setIdCTSP(rs.getInt("IdCTSP"));
        ct.setSoLuong(rs.getInt("Soluong"));
        ct.setDonGia(rs.getBigDecimal("Dongia"));
        ct.setMaSP(rs.getString("MaSP"));
        ct.setTenSP(rs.getString("TenSP"));
        ct.setTenMauSac(rs.getString("TenMauSac"));
        ct.setTenKichCo(rs.getString("TenKichCo"));
        return ct;
    }
}
