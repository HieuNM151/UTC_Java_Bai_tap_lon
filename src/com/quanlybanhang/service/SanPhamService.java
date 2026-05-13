package com.quanlybanhang.service;

import com.quanlybanhang.dao.SanPhamDAO;
import com.quanlybanhang.entity.ChitietSP;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer cho quản lý sản phẩm
 * Xử lý validation và business logic
 */
public class SanPhamService {

    private SanPhamDAO dao = new SanPhamDAO();

    // ═══════════════════════════════════════════════════════
    // VALIDATION
    // ═══════════════════════════════════════════════════════

    /**
     * Kiểm tra sản phẩm có hợp lệ không
     */
    public String validateSanPham(ChitietSP sp) {
        if (sp == null) return "Sản phẩm không được null!";

        if (sp.getMa() == null || sp.getMa().trim().isEmpty()) {
            return "Mã sản phẩm không được để trống!";
        }

        if (sp.getTen() == null || sp.getTen().trim().isEmpty()) {
            return "Tên sản phẩm không được để trống!";
        }

        if (sp.getMa().length() > 10) {
            return "Mã sản phẩm không được quá 10 ký tự!";
        }

        if (sp.getTen().length() > 50) {
            return "Tên sản phẩm không được quá 50 ký tự!";
        }

        if (sp.getGiaNhap() != null && sp.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
            return "Giá nhập không được âm!";
        }

        if (sp.getGiaBan() != null && sp.getGiaBan().compareTo(BigDecimal.ZERO) < 0) {
            return "Giá bán không được âm!";
        }

        if (sp.getSoLuongTon() != null && sp.getSoLuongTon() < 0) {
            return "Số lượng tồn không được âm!";
        }

        return ""; // Hợp lệ
    }

    // ═══════════════════════════════════════════════════════
    // BUSINESS LOGIC
    // ═══════════════════════════════════════════════════════

    /**
     * Thêm sản phẩm với validation
     */
    public String addSanPham(ChitietSP sp) {
        String error = validateSanPham(sp);
        if (!error.isEmpty()) return error;

        if (dao.add(sp)) {
            return "SUCCESS";
        }
        return "Thêm sản phẩm thất bại!";
    }

    /**
     * Cập nhật sản phẩm với validation
     */
    public String updateSanPham(ChitietSP sp) {
        String error = validateSanPham(sp);
        if (!error.isEmpty()) return error;

        if (dao.update(sp)) {
            return "SUCCESS";
        }
        return "Cập nhật sản phẩm thất bại!";
    }

    /**
     * Xóa sản phẩm
     */
    public String deleteSanPham(int id) {
        if (dao.delete(id)) {
            return "SUCCESS";
        }
        return "Xóa sản phẩm thất bại!";
    }

    /**
     * Tính lợi nhuận sản phẩm (Giá bán - Giá nhập)
     */
    public BigDecimal calculateProfit(ChitietSP sp) {
        if (sp.getGiaBan() == null || sp.getGiaNhap() == null) {
            return BigDecimal.ZERO;
        }
        return sp.getGiaBan().subtract(sp.getGiaNhap());
    }

    /**
     * Tính tỷ suất lợi nhuận (%)
     */
    public double calculateProfitRate(ChitietSP sp) {
        if (sp.getGiaBan() == null || sp.getGiaNhap() == null || sp.getGiaNhap().compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal profit = calculateProfit(sp);
        return profit.doubleValue() / sp.getGiaNhap().doubleValue() * 100;
    }

    /**
     * Kiểm tra sản phẩm còn hàng
     */
    public boolean isInStock(ChitietSP sp) {
        return sp.getSoLuongTon() != null && sp.getSoLuongTon() > 0;
    }

    /**
     * Kiểm tra sản phẩm sắp hết hàng (< 10 cái)
     */
    public boolean isLowStock(ChitietSP sp) {
        return sp.getSoLuongTon() != null && sp.getSoLuongTon() > 0 && sp.getSoLuongTon() < 10;
    }

    // ═══════════════════════════════════════════════════════
    // SEARCH & FILTER
    // ═══════════════════════════════════════════════════════

    /**
     * Lấy tất cả sản phẩm
     */
    public List<ChitietSP> getAllSanPham() {
        return dao.getAll();
    }

    /**
     * Tìm kiếm sản phẩm
     */
    public List<ChitietSP> searchSanPham(String keyword) {
        return dao.search(keyword);
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public ChitietSP getSanPhamById(int id) {
        return dao.getById(id);
    }

    /**
     * Lấy danh sách sản phẩm còn hàng
     */
    public List<ChitietSP> getProductsInStock() {
        List<ChitietSP> all = dao.getAll();
        all.removeIf(p -> !isInStock(p));
        return all;
    }

    /**
     * Lấy danh sách sản phẩm sắp hết
     */
    public List<ChitietSP> getLowStockProducts() {
        List<ChitietSP> all = dao.getAll();
        all.removeIf(p -> !isLowStock(p));
        return all;
    }
}
