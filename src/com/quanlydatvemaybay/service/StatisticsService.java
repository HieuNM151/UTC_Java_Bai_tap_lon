package com.quanlydatvemaybay.service;

import com.quanlydatvemaybay.dao.ChiTietSPDAO;
import com.quanlydatvemaybay.dao.HoaDonDAO;
import com.quanlydatvemaybay.dao.HoaDonChiTietDAO;
import com.quanlydatvemaybay.entity.ChitietSP;
import com.quanlydatvemaybay.entity.HoaDon;
import com.quanlydatvemaybay.entity.HoaDonChiTiet;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class StatisticsService {

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private HoaDonChiTietDAO hoaDonChiTietDAO = new HoaDonChiTietDAO();
    private ChiTietSPDAO chiTietSPDAO = new ChiTietSPDAO();

    /**
     * Thống kê doanh thu theo ngày
     */
    public Map<String, BigDecimal> getRevenueByDay(LocalDate date) {
        String sql = "SELECT CONVERT(DATE, NgayTao) AS Ngay, SUM(TongTien) AS DoanhThu " +
                     "FROM HoaDon " +
                     "WHERE CONVERT(DATE, NgayTao) = ? AND TinhTrang = 1 " +
                     "GROUP BY CONVERT(DATE, NgayTao)";
        Map<String, BigDecimal> result = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal revenue = rs.getBigDecimal("DoanhThu");
                    result.put("doanhThu", revenue != null ? revenue : BigDecimal.ZERO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thống kê doanh thu theo tháng
     */
    public Map<String, BigDecimal> getRevenueByMonth(YearMonth yearMonth) {
        String sql = "SELECT SUM(TongTien) AS DoanhThu " +
                     "FROM HoaDon " +
                     "WHERE YEAR(NgayTao) = ? AND MONTH(NgayTao) = ? AND TinhTrang = 1";
        Map<String, BigDecimal> result = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, yearMonth.getYear());
            ps.setInt(2, yearMonth.getMonthValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal revenue = rs.getBigDecimal("DoanhThu");
                    result.put("doanhThu", revenue != null ? revenue : BigDecimal.ZERO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thống kê doanh thu theo năm
     */
    public Map<String, BigDecimal> getRevenueByYear(int year) {
        String sql = "SELECT SUM(TongTien) AS DoanhThu " +
                     "FROM HoaDon " +
                     "WHERE YEAR(NgayTao) = ? AND TinhTrang = 1";
        Map<String, BigDecimal> result = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal revenue = rs.getBigDecimal("DoanhThu");
                    result.put("doanhThu", revenue != null ? revenue : BigDecimal.ZERO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thống kê doanh thu theo khoảng ngày
     */
    public Map<String, BigDecimal> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT SUM(TongTien) AS DoanhThu " +
                     "FROM HoaDon " +
                     "WHERE CONVERT(DATE, NgayTao) BETWEEN ? AND ? AND TinhTrang = 1";
        Map<String, BigDecimal> result = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal revenue = rs.getBigDecimal("DoanhThu");
                    result.put("doanhThu", revenue != null ? revenue : BigDecimal.ZERO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy số lượng hóa đơn theo khoảng thời gian
     */
    public long getInvoiceCountByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) AS SoLuong " +
                     "FROM HoaDon " +
                     "WHERE CONVERT(DATE, NgayTao) BETWEEN ? AND ? AND TinhTrang = 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("SoLuong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số lượng hóa đơn theo ngày
     */
    public long getInvoiceCountByDay(LocalDate date) {
        String sql = "SELECT COUNT(*) AS SoLuong " +
                     "FROM HoaDon " +
                     "WHERE CONVERT(DATE, NgayTao) = ? AND TinhTrang = 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("SoLuong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tính lãi suất từ doanh thu và chi phí
     * Lãi = (Doanh thu - Chi phí) / Doanh thu * 100
     */
    public BigDecimal getProfitMarginByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT " +
                     "  SUM(h.TongTien) AS DoanhThu, " +
                     "  SUM(hct.Soluong * ct.GiaNhap) AS ChiPhi " +
                     "FROM HoaDon h " +
                     "LEFT JOIN HoaDonChiTiet hct ON h.Id = hct.IdHD " +
                     "LEFT JOIN ChitietSP ct ON hct.IdCTSP = ct.Id " +
                     "WHERE CONVERT(DATE, h.NgayTao) BETWEEN ? AND ? AND h.TinhTrang = 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal revenue = rs.getBigDecimal("DoanhThu");
                    BigDecimal cost = rs.getBigDecimal("ChiPhi");

                    if (revenue != null && cost != null && revenue.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal profit = revenue.subtract(cost);
                        BigDecimal margin = profit.divide(revenue, 4, java.math.RoundingMode.HALF_UP)
                                                  .multiply(new BigDecimal("100"));
                        return margin;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Sản phẩm bán chạy nhất
     */
    public List<Map<String, Object>> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        String sql = "SELECT TOP " + limit + " " +
                     "  ct.Id, ct.Ten, ct.Ma, ct.GiaNhap, ct.GiaBan, " +
                     "  SUM(hct.Soluong) AS SoLuongBan, " +
                     "  SUM(hct.Soluong * hct.Dongia) AS DoanhThu " +
                     "FROM HoaDonChiTiet hct " +
                     "LEFT JOIN ChitietSP ct ON hct.IdCTSP = ct.Id " +
                     "LEFT JOIN HoaDon h ON hct.IdHD = h.Id " +
                     "WHERE CONVERT(DATE, h.NgayTao) BETWEEN ? AND ? AND h.TinhTrang = 1 " +
                     "GROUP BY ct.Id, ct.Ten, ct.Ma, ct.GiaNhap, ct.GiaBan " +
                     "ORDER BY SUM(hct.Soluong) DESC";

        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("Id"));
                    item.put("ten", rs.getString("Ten"));
                    item.put("ma", rs.getString("Ma"));
                    item.put("giaNhap", rs.getBigDecimal("GiaNhap"));
                    item.put("giaBan", rs.getBigDecimal("GiaBan"));
                    item.put("soLuongBan", rs.getInt("SoLuongBan"));
                    item.put("doanhThu", rs.getBigDecimal("DoanhThu"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Sản phẩm tồn kho
     */
    public List<Map<String, Object>> getProductsInStock(int limit) {
        String sql = "SELECT TOP " + limit + " " +
                     "  Id, Ten, Ma, SoLuongTon, GiaNhap, GiaBan " +
                     "FROM ChitietSP " +
                     "WHERE SoLuongTon > 0 " +
                     "ORDER BY SoLuongTon DESC";

        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("Id"));
                    item.put("ten", rs.getString("Ten"));
                    item.put("ma", rs.getString("Ma"));
                    item.put("soLuongTon", rs.getInt("SoLuongTon"));
                    item.put("giaNhap", rs.getBigDecimal("GiaNhap"));
                    item.put("giaBan", rs.getBigDecimal("GiaBan"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy kết nối cơ sở dữ liệu
     */
    private Connection getConnection() throws SQLException {
        return com.quanlydatvemaybay.config.DatabaseConfig.getInstance().getConnection();
    }
}
