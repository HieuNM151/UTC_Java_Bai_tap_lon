package com.quanlybanhang.entity;

import java.math.BigDecimal;

public class ChitietSP {
    private int id;
    private String ma;
    private String ten;
    private Integer idNsx, idMauSac, idDMuc, idKC, idCL, idTH, idKM;
    private String moTa;
    private Integer soLuongTon;
    private BigDecimal giaNhap;
    private BigDecimal giaBan;
    private String qrCode;

    // Join fields
    private String tenMauSac;
    private String tenKichCo;
    private String tenThuongHieu;
    private String tenDanhMuc;

    public ChitietSP() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public Integer getIdNsx() { return idNsx; }
    public void setIdNsx(Integer v) { this.idNsx = v; }
    public Integer getIdMauSac() { return idMauSac; }
    public void setIdMauSac(Integer v) { this.idMauSac = v; }
    public Integer getIdDMuc() { return idDMuc; }
    public void setIdDMuc(Integer v) { this.idDMuc = v; }
    public Integer getIdKC() { return idKC; }
    public void setIdKC(Integer v) { this.idKC = v; }
    public Integer getIdCL() { return idCL; }
    public void setIdCL(Integer v) { this.idCL = v; }
    public Integer getIdTH() { return idTH; }
    public void setIdTH(Integer v) { this.idTH = v; }
    public Integer getIdKM() { return idKM; }
    public void setIdKM(Integer v) { this.idKM = v; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String v) { this.moTa = v; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer v) { this.soLuongTon = v; }
    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal v) { this.giaNhap = v; }
    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal v) { this.giaBan = v; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String v) { this.qrCode = v; }
    public String getTenMauSac() { return tenMauSac; }
    public void setTenMauSac(String v) { this.tenMauSac = v; }
    public String getTenKichCo() { return tenKichCo; }
    public void setTenKichCo(String v) { this.tenKichCo = v; }
    public String getTenThuongHieu() { return tenThuongHieu; }
    public void setTenThuongHieu(String v) { this.tenThuongHieu = v; }
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String v) { this.tenDanhMuc = v; }

    public String getTenHienThi() {
        StringBuilder sb = new StringBuilder(ten);
        if (tenMauSac != null) sb.append(" - ").append(tenMauSac);
        if (tenKichCo  != null) sb.append(" / ").append(tenKichCo);
        return sb.toString();
    }
}
