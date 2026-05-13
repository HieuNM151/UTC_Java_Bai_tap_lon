package com.quanlydatvemaybay.entity;

import java.time.LocalDate;

public class KhachHang {
    private int id;
    private String ten, tenDem, ho, email, sdt;
    private Boolean gioitinh;
    private LocalDate ngaySinh;
    private Integer diemThuong;

    public KhachHang() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String v) { this.ten = v; }
    public String getTenDem() { return tenDem; }
    public void setTenDem(String v) { this.tenDem = v; }
    public String getHo() { return ho; }
    public void setHo(String v) { this.ho = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getSdt() { return sdt; }
    public void setSdt(String v) { this.sdt = v; }
    public Boolean getGioitinh() { return gioitinh; }
    public void setGioitinh(Boolean v) { this.gioitinh = v; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate v) { this.ngaySinh = v; }
    public Integer getDiemThuong() { return diemThuong; }
    public void setDiemThuong(Integer v) { this.diemThuong = v; }

    public String getHoTenDay() {
        return (ho != null ? ho + " " : "")
             + (tenDem != null ? tenDem + " " : "")
             + ten;
    }

    @Override
    public String toString() {
        return getHoTenDay() + (sdt != null ? " (" + sdt + ")" : "");
    }
}