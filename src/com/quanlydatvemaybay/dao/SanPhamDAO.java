package com.quanlydatvemaybay.dao;

import com.quanlydatvemaybay.config.DatabaseConfig;
import com.quanlydatvemaybay.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // ═══════════════════════════════════════════════════════
    // QUẢN LÝ CHI TIẾT SẢN PHẨM
    // ═══════════════════════════════════════════════════════

    /** Lấy tất cả sản phẩm */
    public List<ChitietSP> getAll() {
        String sql = "SELECT c.*, ms.Ten AS TenMauSac, kc.Ten AS TenKichCo, " +
                     "th.Ten AS TenThuongHieu, dm.Ten AS TenDanhMuc " +
                     "FROM ChitietSP c " +
                     "LEFT JOIN MauSac ms ON c.IdMauSac = ms.Id " +
                     "LEFT JOIN KichCo kc ON c.IdKC = kc.Id " +
                     "LEFT JOIN ThuongHieu th ON c.IdTH = th.Id " +
                     "LEFT JOIN DanhMucSP dm ON c.IdDMuc = dm.Id " +
                     "ORDER BY c.Ten";
        List<ChitietSP> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapChitietSP(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tìm kiếm sản phẩm theo tên, mã, danh mục */
    public List<ChitietSP> search(String keyword) {
        String sql = "SELECT c.*, ms.Ten AS TenMauSac, kc.Ten AS TenKichCo, " +
                     "th.Ten AS TenThuongHieu, dm.Ten AS TenDanhMuc " +
                     "FROM ChitietSP c " +
                     "LEFT JOIN MauSac ms ON c.IdMauSac = ms.Id " +
                     "LEFT JOIN KichCo kc ON c.IdKC = kc.Id " +
                     "LEFT JOIN ThuongHieu th ON c.IdTH = th.Id " +
                     "LEFT JOIN DanhMucSP dm ON c.IdDMuc = dm.Id " +
                     "WHERE c.Ten LIKE ? OR c.Ma LIKE ? OR ms.Ten LIKE ? OR dm.Ten LIKE ? " +
                     "ORDER BY c.Ten";
        List<ChitietSP> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapChitietSP(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lấy sản phẩm theo ID */
    public ChitietSP getById(int id) {
        String sql = "SELECT c.*, ms.Ten AS TenMauSac, kc.Ten AS TenKichCo, " +
                     "th.Ten AS TenThuongHieu, dm.Ten AS TenDanhMuc " +
                     "FROM ChitietSP c " +
                     "LEFT JOIN MauSac ms ON c.IdMauSac = ms.Id " +
                     "LEFT JOIN KichCo kc ON c.IdKC = kc.Id " +
                     "LEFT JOIN ThuongHieu th ON c.IdTH = th.Id " +
                     "LEFT JOIN DanhMucSP dm ON c.IdDMuc = dm.Id " +
                     "WHERE c.Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapChitietSP(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Thêm sản phẩm mới */
    public boolean add(ChitietSP sp) {
        String sql = "INSERT INTO ChitietSP (Ma, Ten, IdNsx, IdMauSac, IdDMuc, IdKC, IdCL, IdTH, IdKM, MoTa, SoLuongTon, GiaNhap, GiaBan, QrCode) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, sp.getMa());
            ps.setString(2, sp.getTen());
            ps.setObject(3, sp.getIdNsx());
            ps.setObject(4, sp.getIdMauSac());
            ps.setObject(5, sp.getIdDMuc());
            ps.setObject(6, sp.getIdKC());
            ps.setObject(7, sp.getIdCL());
            ps.setObject(8, sp.getIdTH());
            ps.setObject(9, sp.getIdKM());
            ps.setString(10, sp.getMoTa());
            ps.setObject(11, sp.getSoLuongTon());
            ps.setObject(12, sp.getGiaNhap());
            ps.setObject(13, sp.getGiaBan());
            ps.setString(14, sp.getQrCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật sản phẩm */
    public boolean update(ChitietSP sp) {
        String sql = "UPDATE ChitietSP SET Ma=?, Ten=?, IdNsx=?, IdMauSac=?, IdDMuc=?, IdKC=?, IdCL=?, IdTH=?, IdKM=?, " +
                     "MoTa=?, SoLuongTon=?, GiaNhap=?, GiaBan=?, QrCode=? WHERE Id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, sp.getMa());
            ps.setString(2, sp.getTen());
            ps.setObject(3, sp.getIdNsx());
            ps.setObject(4, sp.getIdMauSac());
            ps.setObject(5, sp.getIdDMuc());
            ps.setObject(6, sp.getIdKC());
            ps.setObject(7, sp.getIdCL());
            ps.setObject(8, sp.getIdTH());
            ps.setObject(9, sp.getIdKM());
            ps.setString(10, sp.getMoTa());
            ps.setObject(11, sp.getSoLuongTon());
            ps.setObject(12, sp.getGiaNhap());
            ps.setObject(13, sp.getGiaBan());
            ps.setString(14, sp.getQrCode());
            ps.setInt(15, sp.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa sản phẩm */
    public boolean delete(int id) {
        String sql = "DELETE FROM ChitietSP WHERE Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════
    // DANH SÁCH - MẦU SẮC, KÍCH CỠ, DANH MỤC, v.v
    // ═══════════════════════════════════════════════════════

    public List<MauSac> getAllMauSac() {
        String sql = "SELECT * FROM MauSac ORDER BY Ten";
        List<MauSac> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MauSac(rs.getInt("Id"), rs.getString("Ten")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<KichCo> getAllKichCo() {
        String sql = "SELECT * FROM KichCo ORDER BY Ten";
        List<KichCo> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new KichCo(rs.getInt("Id"), rs.getString("Ten")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DanhMucSP> getAllDanhMuc() {
        String sql = "SELECT * FROM DanhMucSP ORDER BY Ten";
        List<DanhMucSP> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DanhMucSP(rs.getInt("Id"), rs.getString("Ten")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NSX> getAllNSX() {
        String sql = "SELECT * FROM NSX ORDER BY Ten";
        List<NSX> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NSX(rs.getInt("Id"), rs.getString("Ten")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ThuongHieu> getAllThuongHieu() {
        String sql = "SELECT * FROM ThuongHieu ORDER BY Ten";
        List<ThuongHieu> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ThuongHieu(rs.getInt("Id"), rs.getString("Ten")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChatLieu> getAllChatLieu() {
        String sql = "SELECT * FROM ChatLieu ORDER BY Ten";
        List<ChatLieu> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ChatLieu(rs.getInt("Id"), rs.getString("Ten")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ═══════════════════════════════════════════════════════
    // HELPER
    // ═══════════════════════════════════════════════════════

    private ChitietSP mapChitietSP(ResultSet rs) throws SQLException {
        ChitietSP c = new ChitietSP();
        c.setId(rs.getInt("Id"));
        c.setMa(rs.getString("Ma"));
        c.setTen(rs.getString("Ten"));
        c.setMoTa(rs.getString("MoTa"));
        c.setSoLuongTon(rs.getInt("SoLuongTon"));
        c.setGiaBan(rs.getBigDecimal("GiaBan"));
        c.setGiaNhap(rs.getBigDecimal("GiaNhap"));
        c.setQrCode(rs.getString("QrCode"));

        int idMS = rs.getInt("IdMauSac");
        if (!rs.wasNull()) c.setIdMauSac(idMS);
        int idKC = rs.getInt("IdKC");
        if (!rs.wasNull()) c.setIdKC(idKC);
        int idTH = rs.getInt("IdTH");
        if (!rs.wasNull()) c.setIdTH(idTH);
        int idKM = rs.getInt("IdKM");
        if (!rs.wasNull()) c.setIdKM(idKM);
        int idDM = rs.getInt("IdDMuc");
        if (!rs.wasNull()) c.setIdDMuc(idDM);
        int idCL = rs.getInt("IdCL");
        if (!rs.wasNull()) c.setIdCL(idCL);
        int idNsx = rs.getInt("IdNsx");
        if (!rs.wasNull()) c.setIdNsx(idNsx);

        c.setTenMauSac(rs.getString("TenMauSac"));
        c.setTenKichCo(rs.getString("TenKichCo"));
        c.setTenThuongHieu(rs.getString("TenThuongHieu"));
        c.setTenDanhMuc(rs.getString("TenDanhMuc"));

        return c;
    }
}
