package com.quanlydatvemaybay.dao;

import com.quanlydatvemaybay.config.DatabaseConfig;
import com.quanlydatvemaybay.entity.HoaDon;
import com.quanlydatvemaybay.entity.HoaDonChiTiet;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // ═══════════════════════════════════════════════════════
    // CRUD CƠ BẢN
    // ═══════════════════════════════════════════════════════

    /** Lấy tất cả hóa đơn */
    public List<HoaDon> getAll() {
        String sql = "SELECT h.*, k.Ten AS TenKhachHang, nv.Ten AS TenNhanVien " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.IdKH = k.Id " +
                     "LEFT JOIN Users nv ON h.IdNV = nv.Id " +
                     "ORDER BY h.NgayTao DESC";
        List<HoaDon> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapHoaDon(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tìm kiếm hóa đơn */
    public List<HoaDon> search(String keyword) {
        String sql = "SELECT h.*, k.Ten AS TenKhachHang, nv.Ten AS TenNhanVien " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.IdKH = k.Id " +
                     "LEFT JOIN Users nv ON h.IdNV = nv.Id " +
                     "WHERE h.Ma LIKE ? OR k.Ten LIKE ? OR nv.Ten LIKE ? " +
                     "ORDER BY h.NgayTao DESC";
        List<HoaDon> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lấy hóa đơn theo ID */
    public HoaDon getById(int id) {
        String sql = "SELECT h.*, k.Ten AS TenKhachHang, nv.Ten AS TenNhanVien " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.IdKH = k.Id " +
                     "LEFT JOIN Users nv ON h.IdNV = nv.Id " +
                     "WHERE h.Id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapHoaDon(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Xóa hóa đơn */
    public boolean delete(int id) {
        try {
            // Xóa chi tiết hóa đơn trước
            String sqlDetail = "DELETE FROM HoaDonChiTiet WHERE IdHD = ?";
            try (PreparedStatement ps = getConn().prepareStatement(sqlDetail)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Xóa hóa đơn
            String sql = "DELETE FROM HoaDon WHERE Id = ?";
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Thêm hóa đơn mới (đơn giản) */
    public boolean add(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (IdKH, IdNV, Ma, NgayTao, NgayThanhToan, TinhTrang, GhiChu, TongTien) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setObject(1, hd.getIdKH());
            ps.setObject(2, hd.getIdNV());
            ps.setString(3, hd.getMa());
            ps.setObject(4, hd.getNgayTao());
            ps.setObject(5, hd.getNgayThanhToan());
            ps.setObject(6, hd.getTinhTrang());
            ps.setString(7, hd.getGhiChu());
            ps.setObject(8, hd.getTongTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật hóa đơn */
    public boolean update(HoaDon hd) {
        String sql = "UPDATE HoaDon SET IdKH=?, IdNV=?, Ma=?, NgayTao=?, NgayThanhToan=?, TinhTrang=?, GhiChu=?, TongTien=? WHERE Id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setObject(1, hd.getIdKH());
            ps.setObject(2, hd.getIdNV());
            ps.setString(3, hd.getMa());
            ps.setObject(4, hd.getNgayTao());
            ps.setObject(5, hd.getNgayThanhToan());
            ps.setObject(6, hd.getTinhTrang());
            ps.setString(7, hd.getGhiChu());
            ps.setObject(8, hd.getTongTien());
            ps.setInt(9, hd.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════
    // TÁO HÓA ĐƠN PHỨC TẠP (với chi tiết)
    // ═══════════════════════════════════════════════════════
    public int taoHoaDon(HoaDon hd, List<HoaDonChiTiet> chiTietList) {
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(false);

            // 1. Them hoa don
            String sqlHD = "INSERT INTO HoaDon (IdKH, IdNV, Ma, NgayTao, NgayThanhToan, TinhTrang, GhiChu, TongTien) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int idHD = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlHD, Statement.RETURN_GENERATED_KEYS)) {
                if (hd.getIdKH() != null) ps.setInt(1, hd.getIdKH()); else ps.setNull(1, Types.INTEGER);
                ps.setInt(2, hd.getIdNV());
                ps.setString(3, hd.getMa());
                ps.setTimestamp(4, Timestamp.valueOf(hd.getNgayTao()));
                if (hd.getNgayThanhToan() != null)
                    ps.setDate(5, Date.valueOf(hd.getNgayThanhToan()));
                else ps.setNull(5, Types.DATE);
                ps.setBoolean(6, hd.getTinhTrang() != null && hd.getTinhTrang());
                ps.setString(7, hd.getGhiChu());
                ps.setBigDecimal(8, hd.getTongTien());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idHD = rs.getInt(1);
                }
            }
            if (idHD == -1) throw new SQLException("Khong lay duoc Id hoa don moi");

            // 2. Them chi tiet hoa don
            String sqlCT = "INSERT INTO HoaDonChiTiet (IdHD, IdCTSP, Soluong, Dongia) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlCT)) {
                for (HoaDonChiTiet ct : chiTietList) {
                    ps.setInt(1, idHD);
                    ps.setInt(2, ct.getIdCTSP());
                    ps.setInt(3, ct.getSoLuong());
                    ps.setBigDecimal(4, ct.getDonGia());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Giam so luong ton kho
            String sqlTon = "UPDATE ChitietSP SET SoLuongTon = SoLuongTon - ? WHERE Id = ? AND SoLuongTon >= ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTon)) {
                for (HoaDonChiTiet ct : chiTietList) {
                    ps.setInt(1, ct.getSoLuong());
                    ps.setInt(2, ct.getIdCTSP());
                    ps.setInt(3, ct.getSoLuong());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return idHD;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return -1;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /** Lay danh sach hoa don gan nhat */
    public List<HoaDon> getRecent(int limit) {
        String sql = "SELECT h.*, k.Ten AS TenKH, u.Ten AS TenNV " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.IdKH = k.Id " +
                     "LEFT JOIN Users u ON h.IdNV = u.Id " +
                     "ORDER BY h.NgayTao DESC " +
                     "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        List<HoaDon> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Dem so hoa don hom nay */
    public int demHoaDonHomNay() {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE CAST(NgayTao AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Doanh thu hom nay (cac hoa don da thanh toan) */
    public BigDecimal doanhThuHomNay() {
        String sql = "SELECT ISNULL(SUM(TongTien), 0) " +
                     "FROM HoaDon " +
                     "WHERE CAST(NgayTao AS DATE) = CAST(GETDATE() AS DATE) " +
                     "AND TinhTrang = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    /** Sinh ma hoa don tu dong: HD + timestamp */
    public static String sinhMaHoaDon() {
        return "HD" + System.currentTimeMillis() % 100000000;
    }

    private HoaDon mapRow(ResultSet rs) throws SQLException {
        HoaDon h = new HoaDon();
        h.setId(rs.getInt("Id"));
        int idKH = rs.getInt("IdKH"); if (!rs.wasNull()) h.setIdKH(idKH);
        int idNV = rs.getInt("IdNV"); if (!rs.wasNull()) h.setIdNV(idNV);
        h.setMa(rs.getString("Ma"));
        Timestamp ts = rs.getTimestamp("NgayTao");
        if (ts != null) h.setNgayTao(ts.toLocalDateTime());
        Date d = rs.getDate("NgayThanhToan");
        if (d != null) h.setNgayThanhToan(d.toLocalDate());
        boolean tt = rs.getBoolean("TinhTrang");
        if (!rs.wasNull()) h.setTinhTrang(tt);
        h.setGhiChu(rs.getString("GhiChu"));
        h.setTongTien(rs.getBigDecimal("TongTien"));
        try { h.setTenKhachHang(rs.getString("TenKhachHang")); } catch (Exception ignored) {}
        try { h.setTenNhanVien(rs.getString("TenNhanVien")); } catch (Exception ignored) {}
        return h;
    }

    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        return mapRow(rs);
    }
}