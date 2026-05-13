package com.quanlydatvemaybay.ui.dialogs;

import com.quanlydatvemaybay.dao.SanPhamDAO;
import com.quanlydatvemaybay.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.util.List;

public class SanPhamDialog extends JDialog {

    private SanPhamDAO dao;
    private ChitietSP sanPham;
    private boolean confirmed = false;

    // Components
    private JTextField txtMa, txtTen, txtMoTa, txtSoLuong, txtGiaNhap, txtGiaBan;
    private JComboBox<DanhMucSP> cbDanhMuc;
    private JComboBox<MauSac> cbMauSac;
    private JComboBox<KichCo> cbKichCo;
    private JComboBox<NSX> cbNSX;
    private JComboBox<ThuongHieu> cbThuongHieu;
    private JComboBox<ChatLieu> cbChatLieu;
    private JButton btnLuu, btnHuy;

    // ── Màu sắc (match với SanPhamPanel) ────────────────────
    private static final Color BG_COLOR      = new Color(248, 250, 252);  // Background
    private static final Color CARD_BG       = Color.WHITE;               // Card background
    private static final Color ACCENT        = new Color(99, 102, 241);   // Indigo (Lưu)
    private static final Color ACCENT_DARK   = new Color(79, 70, 229);
    private static final Color CANCEL_BTN    = new Color(148, 163, 184);  // Gray (Hủy)
    private static final Color LABEL_COLOR   = new Color(71, 85, 105);    // Text Mid
    private static final Color BORDER_COLOR  = new Color(226, 232, 240);  // Border
    private static final Color TEXT_DARK     = new Color(15, 23, 42);     // Text Dark
    private static final Color ERROR_COLOR   = new Color(239, 68, 68);    // Danger Red

    public SanPhamDialog(JFrame parent, ChitietSP sp, SanPhamDAO dao) {
        super(parent, sp == null ? "Thêm sản phẩm mới" : "Sửa sản phẩm", true);
        this.sanPham = sp;
        this.dao = dao;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(parent);
        initUI();
        if (sp != null) populateData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = buildHeader();
        main.add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = buildFormPanel();
        main.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = buildButtonPanel();
        main.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        String title = sanPham == null ? "➕ Thêm sản phẩm mới" : "✏️ Chỉnh sửa sản phẩm";
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        String desc = sanPham == null ? "Nhập thông tin sản phẩm mới" : "Cập nhật thông tin sản phẩm";
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

        // ...existing code...
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Mã sản phẩm:"), gbc);
        gbc.gridx = 1;
        txtMa = createTextField(15);
        p.add(txtMa, gbc);
        row++;

        // Tên SP
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1;
        txtTen = createTextField(15);
        p.add(txtTen, gbc);
        row++;

        // Mô tả
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        txtMoTa = createTextField(15);
        p.add(txtMoTa, gbc);
        row++;

        // Danh mục
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Danh mục:"), gbc);
        gbc.gridx = 1;
        cbDanhMuc = createComboBox(dao.getAllDanhMuc());
        p.add(cbDanhMuc, gbc);
        row++;

        // Mầu sắc
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Mầu sắc:"), gbc);
        gbc.gridx = 1;
        cbMauSac = createComboBox(dao.getAllMauSac());
        p.add(cbMauSac, gbc);
        row++;

        // Kích cỡ
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Kích cỡ:"), gbc);
        gbc.gridx = 1;
        cbKichCo = createComboBox(dao.getAllKichCo());
        p.add(cbKichCo, gbc);
        row++;

        // NSX
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Nhà sản xuất:"), gbc);
        gbc.gridx = 1;
        cbNSX = createComboBox(dao.getAllNSX());
        p.add(cbNSX, gbc);
        row++;

        // Thương hiệu
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Thương hiệu:"), gbc);
        gbc.gridx = 1;
        cbThuongHieu = createComboBox(dao.getAllThuongHieu());
        p.add(cbThuongHieu, gbc);
        row++;

        // Chất liệu
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Chất liệu:"), gbc);
        gbc.gridx = 1;
        cbChatLieu = createComboBox(dao.getAllChatLieu());
        p.add(cbChatLieu, gbc);
        row++;

        // Số lượng tồn
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Số lượng tồn:"), gbc);
        gbc.gridx = 1;
        txtSoLuong = createTextField(15);
        p.add(txtSoLuong, gbc);
        row++;

        // Giá nhập
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Giá nhập:"), gbc);
        gbc.gridx = 1;
        txtGiaNhap = createTextField(15);
        p.add(txtGiaNhap, gbc);
        row++;

        // Giá bán
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel("Giá bán:"), gbc);
        gbc.gridx = 1;
        txtGiaBan = createTextField(15);
        p.add(txtGiaBan, gbc);
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
        if (sanPham == null) return;

        txtMa.setText(sanPham.getMa());
        txtTen.setText(sanPham.getTen());
        txtMoTa.setText(sanPham.getMoTa());
        txtSoLuong.setText(String.valueOf(sanPham.getSoLuongTon()));
        txtGiaNhap.setText(sanPham.getGiaNhap() != null ? sanPham.getGiaNhap().toString() : "");
        txtGiaBan.setText(sanPham.getGiaBan() != null ? sanPham.getGiaBan().toString() : "");

        selectComboBoxById(cbDanhMuc, sanPham.getIdDMuc());
        selectComboBoxById(cbMauSac, sanPham.getIdMauSac());
        selectComboBoxById(cbKichCo, sanPham.getIdKC());
        selectComboBoxById(cbNSX, sanPham.getIdNsx());
        selectComboBoxById(cbThuongHieu, sanPham.getIdTH());
        selectComboBoxById(cbChatLieu, sanPham.getIdCL());
    }

    private void save() {
        // Validation
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã và tên sản phẩm!", "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtSoLuong.getText().trim().isEmpty() || txtGiaNhap.getText().trim().isEmpty() || txtGiaBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ số lượng và giá!", "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ChitietSP sp = new ChitietSP();
            if (sanPham != null) sp.setId(sanPham.getId());

            sp.setMa(txtMa.getText().trim());
            sp.setTen(txtTen.getText().trim());
            sp.setMoTa(txtMoTa.getText().trim());

            DanhMucSP dm = (DanhMucSP) cbDanhMuc.getSelectedItem();
            if (dm != null) sp.setIdDMuc(dm.getId());

            MauSac ms = (MauSac) cbMauSac.getSelectedItem();
            if (ms != null) sp.setIdMauSac(ms.getId());

            KichCo kc = (KichCo) cbKichCo.getSelectedItem();
            if (kc != null) sp.setIdKC(kc.getId());

            NSX nsx = (NSX) cbNSX.getSelectedItem();
            if (nsx != null) sp.setIdNsx(nsx.getId());

            ThuongHieu th = (ThuongHieu) cbThuongHieu.getSelectedItem();
            if (th != null) sp.setIdTH(th.getId());

            ChatLieu cl = (ChatLieu) cbChatLieu.getSelectedItem();
            if (cl != null) sp.setIdCL(cl.getId());

            int sluong = Integer.parseInt(txtSoLuong.getText().trim());
            if (sluong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng không được âm!", "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            sp.setSoLuongTon(sluong);

            BigDecimal giaNhap = new BigDecimal(txtGiaNhap.getText().trim());
            BigDecimal giaBan = new BigDecimal(txtGiaBan.getText().trim());

            if (giaNhap.compareTo(BigDecimal.ZERO) < 0 || giaBan.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá không được âm!", "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sp.setGiaNhap(giaNhap);
            sp.setGiaBan(giaBan);

            boolean success;
            if (sanPham == null) {
                success = dao.add(sp);
            } else {
                success = dao.update(sp);
            }

            if (success) {
                String msg = sanPham == null ? "✓ Thêm sản phẩm thành công!" : "✓ Cập nhật sản phẩm thành công!";
                JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu dữ liệu. Kiểm tra lại mã sản phẩm (mã có thể trùng)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho giá và số lượng!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
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
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background gradient - sáng hơn khi hover
                Color fillColor = getModel().isRollover() ?
                    new Color(bg.getRed() - 15, bg.getGreen() - 15, bg.getBlue() - 15) : bg;

                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                // Border
                g2.setColor(new Color(bg.getRed() - 40, bg.getGreen() - 40, bg.getBlue() - 40));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);

                g2.dispose();
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
            if (item instanceof DanhMucSP && ((DanhMucSP) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            } else if (item instanceof MauSac && ((MauSac) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            } else if (item instanceof KichCo && ((KichCo) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            } else if (item instanceof NSX && ((NSX) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            } else if (item instanceof ThuongHieu && ((ThuongHieu) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            } else if (item instanceof ChatLieu && ((ChatLieu) item).getId() == id) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }
}
