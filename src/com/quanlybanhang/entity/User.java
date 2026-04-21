package com.quanlybanhang.entity;

import java.time.LocalDate;

public class User {
    private int id;
    private String ten;
    private String tenDem;
    private String ho;
    private LocalDate ngaySinh;
    private Boolean gioitinh;
    private String sdt;
    private Integer idCV;
    private String taiKhoan;
    private String matKhau;
    private String email;
    private Boolean trangThai;

    public User() {}

    public User(int id, String ten, String tenDem, String ho,
                LocalDate ngaySinh, Boolean gioitinh, String sdt,
                Integer idCV, String taiKhoan, String matKhau,
                String email, Boolean trangThai) {
        this.id = id;
        this.ten = ten;
        this.tenDem = tenDem;
        this.ho = ho;
        this.ngaySinh = ngaySinh;
        this.gioitinh = gioitinh;
        this.sdt = sdt;
        this.idCV = idCV;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.email = email;
        this.trangThai = trangThai;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getTenDem() { return tenDem; }
    public void setTenDem(String tenDem) { this.tenDem = tenDem; }

    public String getHo() { return ho; }
    public void setHo(String ho) { this.ho = ho; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public Boolean getGioitinh() { return gioitinh; }
    public void setGioitinh(Boolean gioitinh) { this.gioitinh = gioitinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public Integer getIdCV() { return idCV; }
    public void setIdCV(Integer idCV) { this.idCV = idCV; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getTrangThai() { return trangThai; }
    public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }

    /** Họ tên đầy đủ */
    public String getHoTenDay() {
        return (ho != null ? ho + " " : "")
                + (tenDem != null ? tenDem + " " : "")
                + ten;
    }
}
