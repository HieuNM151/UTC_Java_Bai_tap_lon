package com.quanlybanhang.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HoaDon {
    private int id;
    private Integer idKH;
    private Integer idNV;
    private String ma;
    private LocalDateTime ngayTao;
    private LocalDate ngayThanhToan;
    private Boolean tinhTrang;
    private String ghiChu;
    private BigDecimal tongTien;
    private String tenKhachHang;
    private String tenNhanVien;

    public HoaDon() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdKH() { return idKH; }
    public void setIdKH(Integer idKH) { this.idKH = idKH; }
    public Integer getIdNV() { return idNV; }
    public void setIdNV(Integer idNV) { this.idNV = idNV; }
    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public LocalDate getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDate ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public Boolean getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(Boolean tinhTrang) { this.tinhTrang = tinhTrang; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
}