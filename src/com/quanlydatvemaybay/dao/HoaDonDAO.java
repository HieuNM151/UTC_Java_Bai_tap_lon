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

    /**
     * Tao hoa don moi + luu chi tiet + giam ton kho.
     * Dung transaction de dam bao toan ven du lieu.
     * @return Id hoa don moi tao, -1 neu that bai
     */
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
        try { h.setTenKhachHang(rs.getString("TenKH")); } catch (Exception ignored) {}
        try { h.setTenNhanVien(rs.getString("TenNV")); } catch (Exception ignored) {}
        return h;
    }
}