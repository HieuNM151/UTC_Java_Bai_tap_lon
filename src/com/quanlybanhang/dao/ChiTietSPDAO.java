package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseConfig;
import com.quanlybanhang.entity.ChitietSP;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietSPDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    /** Lay tat ca san pham con hang (SoLuongTon > 0) */
    public List<ChitietSP> getAll() {
        return search("");
    }

    /** Tim kiem theo ten, ma, mau sac, kich co */
    public List<ChitietSP> search(String keyword) {
        String sql = "SELECT c.*, ms.Ten AS TenMauSac, kc.Ten AS TenKichCo, th.Ten AS TenThuongHieu " +
                     "FROM ChitietSP c " +
                     "LEFT JOIN MauSac ms ON c.IdMauSac = ms.Id " +
                     "LEFT JOIN KichCo kc ON c.IdKC = kc.Id " +
                     "LEFT JOIN ThuongHieu th ON c.IdTH = th.Id " +
                     "WHERE c.SoLuongTon > 0 " +
                     "AND (c.Ten LIKE ? OR c.Ma LIKE ? OR ms.Ten LIKE ? OR kc.Ten LIKE ?) " +
                     "ORDER BY c.Ten";
        List<ChitietSP> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lay san pham theo Id */
    public ChitietSP getById(int id) {
        String sql = "SELECT c.*, ms.Ten AS TenMauSac, kc.Ten AS TenKichCo, th.Ten AS TenThuongHieu " +
                     "FROM ChitietSP c " +
                     "LEFT JOIN MauSac ms ON c.IdMauSac = ms.Id " +
                     "LEFT JOIN KichCo kc ON c.IdKC = kc.Id " +
                     "LEFT JOIN ThuongHieu th ON c.IdTH = th.Id " +
                     "WHERE c.Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Giam so luong ton kho sau khi ban */
    public boolean giamSoLuong(int idCTSP, int soLuong) {
        String sql = "UPDATE ChitietSP SET SoLuongTon = SoLuongTon - ? WHERE Id = ? AND SoLuongTon >= ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, idCTSP);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Hoan tra so luong khi huy hoa don */
    public boolean hoanTraSoLuong(int idCTSP, int soLuong) {
        String sql = "UPDATE ChitietSP SET SoLuongTon = SoLuongTon + ? WHERE Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, idCTSP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ChitietSP mapRow(ResultSet rs) throws SQLException {
        ChitietSP c = new ChitietSP();
        c.setId(rs.getInt("Id"));
        c.setMa(rs.getString("Ma"));
        c.setTen(rs.getString("Ten"));
        c.setMoTa(rs.getString("MoTa"));
        c.setSoLuongTon(rs.getInt("SoLuongTon"));
        c.setGiaBan(rs.getBigDecimal("GiaBan"));
        c.setGiaNhap(rs.getBigDecimal("GiaNhap"));
        int idMS = rs.getInt("IdMauSac"); if (!rs.wasNull()) c.setIdMauSac(idMS);
        int idKC = rs.getInt("IdKC");     if (!rs.wasNull()) c.setIdKC(idKC);
        int idTH = rs.getInt("IdTH");     if (!rs.wasNull()) c.setIdTH(idTH);
        int idKM = rs.getInt("IdKM");     if (!rs.wasNull()) c.setIdKM(idKM);
        int idDM = rs.getInt("IdDMuc");   if (!rs.wasNull()) c.setIdDMuc(idDM);
        int idCL = rs.getInt("IdCL");     if (!rs.wasNull()) c.setIdCL(idCL);
        int idNsx = rs.getInt("IdNsx");   if (!rs.wasNull()) c.setIdNsx(idNsx);
        c.setTenMauSac(rs.getString("TenMauSac"));
        c.setTenKichCo(rs.getString("TenKichCo"));
        c.setTenThuongHieu(rs.getString("TenThuongHieu"));
        return c;
    }
}