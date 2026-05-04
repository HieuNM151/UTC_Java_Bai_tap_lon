package com.quanlydatvemaybay.ui.dialogs;

import com.quanlydatvemaybay.dao.HoaDonDAO;
import com.quanlydatvemaybay.dao.KhachHangDAO;
import com.quanlydatvemaybay.dao.UserDAO;
import com.quanlydatvemaybay.entity.HoaDon;
import com.quanlydatvemaybay.entity.KhachHang;
import com.quanlydatvemaybay.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDonDialog extends JDialog {

    private HoaDonDAO dao;
    private HoaDon hoaDon;
    private boolean confirmed = false;

    // Components
    private JTextField txtMa, txtTongTien, txtGhichu;
    private JComboBox<KhachHang> cbKhachHang;
    private JComboBox<User> cbNhanVien;
    private JCheckBox cbThanhToan;
    private JButton btnLuu, btnHuy;

    // ── Màu sắc ────────────────────────────────────────────
    private static final Color BG_COLOR      = new Color(248, 250, 252);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color ACCENT        = new Color(99, 102, 241);
    private static final Color CANCEL_BTN    = new Color(148, 163, 184);
    private static final Color LABEL_COLOR   = new Color(71, 85, 105);
    private static final Color BORDER_COLOR  = new Color(226, 232, 240);
    private static final Color TEXT_DARK     = new Color(15, 23, 42);

    public HoaDonDialog(JFrame parent, HoaDon hd, HoaDonDAO dao) {
        super(parent, hd == null ? "Thêm hóa đơn mới" : "Sửa hóa đơn", true);
        this.hoaDon = hd;
        this.dao = dao;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        initUI();
        if (hd != null) populateData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildFormPanel(), BorderLayout.CENTER);
        main.add(buildButtonPanel(), BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        String title = hoaDon == null ? "➕ Thêm hóa đơn mới" : "✏️ Chỉnh sửa hóa đơn";
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        String desc = hoaDon == null ? "Tạo hóa đơn mới cho khách hàng" : "Cập nhật thông tin hóa đơn";
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(LABEL_COLOR);
        lblDesc.setAlignmentX(LEFT_ALIGNMENT);

        p.add(lblTitle);
        p.add(Box.createVerticalStrut(4));
        p.add(lblDesc);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new MatteBorder(1, 1, 1, 1, BORDER_COLOR));

        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setLayout(new GridBagLayout());
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Mã HD
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Mã hóa đơn:"), gbc);
        gbc.gridx = 1;
        txtMa = createTextField(15);
        p.add(txtMa, gbc);
        row++;

        // Khách hàng
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Khách hàng:"), gbc);
        gbc.gridx = 1;
        cbKhachHang = createComboBox(new KhachHangDAO().getAll());
        p.add(cbKhachHang, gbc);
        row++;

        // Nhân viên
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        cbNhanVien = createComboBox(new UserDAO().getAll());
        p.add(cbNhanVien, gbc);
        row++;

        // Tổng tiền
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        txtTongTien = createTextField(15);
        txtTongTien.setText("0");
        p.add(txtTongTien, gbc);
        row++;

        // Trạng thái thanh toán
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Đã thanh toán:"), gbc);
        gbc.gridx = 1;
        cbThanhToan = new JCheckBox();
        cbThanhToan.setBackground(CARD_BG);
        p.add(cbThanhToan, gbc);
        row++;

        // Ghi chú
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        txtGhichu = createTextField(15);
        p.add(txtGhichu, gbc);
        row++;

        // Spacer
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weighty = 1;
        p.add(Box.createVerticalGlue(), gbc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBackground(CARD_BG);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD_BG);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnLuu = createButton("✓ Lưu", ACCENT, Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(100, 38));
        btnLuu.addActionListener(e -> save());
        p.add(btnLuu);

        btnHuy = createButton("✕ Hủy", CANCEL_BTN, Color.WHITE);
        btnHuy.setPreferredSize(new Dimension(100, 38));
        btnHuy.addActionListener(e -> dispose());
        p.add(btnHuy);

        return p;
    }

    private void populateData() {
        if (hoaDon == null) return;
        txtMa.setText(hoaDon.getMa());
        txtTongTien.setText(hoaDon.getTongTien() != null ? hoaDon.getTongTien().toString() : "0");
        txtGhichu.setText(hoaDon.getGhiChu() != null ? hoaDon.getGhiChu() : "");
        cbThanhToan.setSelected(hoaDon.getTinhTrang() != null && hoaDon.getTinhTrang());

        selectComboBoxById(cbKhachHang, hoaDon.getIdKH());
        selectComboBoxById(cbNhanVien, hoaDon.getIdNV());
    }

    private void save() {
        if (txtMa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn!", "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            HoaDon hd = new HoaDon();
            if (hoaDon != null) hd.setId(hoaDon.getId());

            hd.setMa(txtMa.getText().trim());

            KhachHang kh = (KhachHang) cbKhachHang.getSelectedItem();
            if (kh != null) hd.setIdKH(kh.getId());

            User nv = (User) cbNhanVien.getSelectedItem();
            if (nv != null) hd.setIdNV(nv.getId());

            hd.setNgayTao(LocalDateTime.now());
            hd.setTinhTrang(cbThanhToan.isSelected());
            hd.setGhiChu(txtGhichu.getText().trim());
            hd.setTongTien(new BigDecimal(txtTongTien.getText().trim()));

            boolean success;
            if (hoaDon == null) {
                success = dao.add(hd);
            } else {
                success = dao.update(hd);
            }

            if (success) {
                String msg = hoaDon == null ? "✓ Thêm hóa đơn thành công!" : "✓ Cập nhật hóa đơn thành công!";
                JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu dữ liệu. Kiểm tra lại mã hóa đơn (mã có thể trùng)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho tổng tiền!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // ═══════════════════════════════════════════════════════
    // HELPER
    // ═══════════════════════════════════════════════════════

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(LABEL_COLOR);
        return lbl;
    }

    private JTextField createTextField(int columns) {
        JTextField txt = new JTextField(columns);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txt.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        return txt;
    }

    private <T> JComboBox<T> createComboBox(List<T> items) {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        for (T item : items) {
            cb.addItem(item);
        }
        return cb;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                Color fillColor = getModel().isRollover() ?
                    new Color(bg.getRed() - 15, bg.getGreen() - 15, bg.getBlue() - 15) : bg;

                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2.setColor(new Color(bg.getRed() - 40, bg.getGreen() - 40, bg.getBlue() - 40));
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);

                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    private void selectComboBoxById(JComboBox<?> cb, Integer id) {
        if (id == null) return;
        for (int i = 0; i < cb.getItemCount(); i++) {
            Object item = cb.getItemAt(i);
            if (item instanceof KhachHang && ((KhachHang) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            } else if (item instanceof User && ((User) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }
}
