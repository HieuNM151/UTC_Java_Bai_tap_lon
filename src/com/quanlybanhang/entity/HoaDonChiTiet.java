package com.quanlybanhang.entity;

import java.math.BigDecimal;

public class HoaDonChiTiet {
    private int idHD;
    private int idCTSP;
    private int soLuong;
    private BigDecimal donGia;
    private String tenSP;
    private String maSP;
    private String tenMauSac;
    private String tenKichCo;

    public HoaDonChiTiet() {}

    public HoaDonChiTiet(int idCTSP, String maSP, String tenSP,
                         String tenMauSac, String tenKichCo,
                         int soLuong, BigDecimal donGia) {
        this.idCTSP = idCTSP; this.maSP = maSP; this.tenSP = tenSP;
        this.tenMauSac = tenMauSac; this.tenKichCo = tenKichCo;
        this.soLuong = soLuong; this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        if (donGia == null) return BigDecimal.ZERO;
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    public String getTenHienThi() {
        StringBuilder sb = new StringBuilder(tenSP != null ? tenSP : "");
        if (tenMauSac != null) sb.append(" - ").append(tenMauSac);
        if (tenKichCo  != null) sb.append(" / ").append(tenKichCo);
        return sb.toString();
    }

    public int getIdHD() { return idHD; }
    public void setIdHD(int idHD) { this.idHD = idHD; }
    public int getIdCTSP() { return idCTSP; }
    public void setIdCTSP(int idCTSP) { this.idCTSP = idCTSP; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public String getTenMauSac() { return tenMauSac; }
    public void setTenMauSac(String tenMauSac) { this.tenMauSac = tenMauSac; }
    public String getTenKichCo() { return tenKichCo; }
    public void setTenKichCo(String tenKichCo) { this.tenKichCo = tenKichCo; }
}