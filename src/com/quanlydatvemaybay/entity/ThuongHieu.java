package com.quanlydatvemaybay.entity;

public class ThuongHieu {
    private int id;
    private String ten;

    public ThuongHieu() {}

    public ThuongHieu(int id, String ten) {
        this.id = id;
        this.ten = ten;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    @Override
    public String toString() { return ten; }
}
