package com.quanlybanhang.ui.dialogs;

import com.quanlybanhang.entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class KhachHangDialog extends JDialog {

    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_H = new Color(79, 70, 229);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color DANGER_H = new Color(220, 38, 38);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MID = new Color(71, 85, 105);
    private static final Color BORDER_C = new Color(226, 232, 240);

    private JTextField txtHo, txtTenDem, txtTen, txtEmail, txtSdt, txtDiem;
    private JComboBox<String> cbGioiTinh;
    private JSpinner spNgaySinh;
    private JButton btnSave, btnCancel;
    private boolean saved = false;
    private KhachHang khachHang;

    public KhachHangDialog(JFrame owner, KhachHang khachHang) {
        super(owner, khachHang == null || khachHang.getId() == 0 ? "Thêm khách hàng" : "Sửa khách hàng", true);
        this.khachHang = khachHang != null ? khachHang : new KhachHang();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
        loadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createLabel("Họ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtHo = createTextField(), gbc);

        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(createLabel("Tên đệm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtTenDem = createTextField(), gbc);

        // Hàng 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(createLabel("Tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtTen = createTextField(), gbc);

        // Hàng 4
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtEmail = createTextField(), gbc);

        // Hàng 5
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(createLabel("SĐT:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtSdt = createTextField(), gbc);

        // Hàng 6
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formPanel.add(createLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(cbGioiTinh, gbc);

        // Hàng 7
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formPanel.add(createLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        spNgaySinh = new JSpinner(new javax.swing.SpinnerDateModel());
        spNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy");
        spNgaySinh.setEditor(editor);
        formPanel.add(spNgaySinh, gbc);

        // Hàng 8
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        formPanel.add(createLabel("Điểm thưởng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDiem = createTextField();
        formPanel.add(txtDiem, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        btnSave = createButton("Lưu", ACCENT, ACCENT_H);
        btnSave.addActionListener(e -> save());
        btnCancel = createButton("Hủy", DANGER, DANGER_H);
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MID);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                new EmptyBorder(6, 8, 6, 8)));
        tf.setPreferredSize(new Dimension(0, 32));
        return tf;
    }

    private JButton createButton(String text, Color color, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverColor : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadData() {
        if (khachHang != null && khachHang.getId() > 0) {
            txtHo.setText(khachHang.getHo() != null ? khachHang.getHo() : "");
            txtTenDem.setText(khachHang.getTenDem() != null ? khachHang.getTenDem() : "");
            txtTen.setText(khachHang.getTen() != null ? khachHang.getTen() : "");
            txtEmail.setText(khachHang.getEmail() != null ? khachHang.getEmail() : "");
            txtSdt.setText(khachHang.getSdt() != null ? khachHang.getSdt() : "");
            if (khachHang.getGioitinh() != null) {
                cbGioiTinh.setSelectedIndex(khachHang.getGioitinh() ? 0 : 1);
            }
            if (khachHang.getNgaySinh() != null) {
                spNgaySinh.setValue(java.sql.Date.valueOf(khachHang.getNgaySinh()));
            }
            txtDiem.setText(khachHang.getDiemThuong() != null ? khachHang.getDiemThuong().toString() : "0");
        }
    }

    private void save() {
        if (txtTen.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        khachHang.setHo(txtHo.getText().trim());
        khachHang.setTenDem(txtTenDem.getText().trim());
        khachHang.setTen(txtTen.getText().trim());
        khachHang.setEmail(txtEmail.getText().trim());
        khachHang.setSdt(txtSdt.getText().trim());
        khachHang.setGioitinh(cbGioiTinh.getSelectedIndex() == 0);

        if (spNgaySinh.getValue() instanceof java.util.Date) {
            java.util.Date d = (java.util.Date) spNgaySinh.getValue();
            khachHang.setNgaySinh(new java.sql.Date(d.getTime()).toLocalDate());
        }

        try {
            int diem = Integer.parseInt(txtDiem.getText().trim());
            khachHang.setDiemThuong(diem);
        } catch (NumberFormatException ex) {
            khachHang.setDiemThuong(0);
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }
}
