package com.quanlybanhang.ui;

import com.quanlybanhang.dao.ChiTietSPDAO;
import com.quanlybanhang.dao.HoaDonDAO;
import com.quanlybanhang.dao.KhachHangDAO;
import com.quanlybanhang.entity.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BanHangPanel extends JPanel {

    // ── Màu ────────────────────────────────────────────────────
    private static final Color BG        = new Color(248, 250, 252);
    private static final Color WHITE     = Color.WHITE;
    private static final Color ACCENT    = new Color(99, 102, 241);
    private static final Color ACCENT_H  = new Color(79, 70, 229);
    private static final Color SUCCESS   = new Color(22, 163, 74);
    private static final Color DANGER    = new Color(220, 38, 38);
    private static final Color BORDER    = new Color(226, 232, 240);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MID  = new Color(71, 85, 105);
    private static final Color TEXT_GRAY = new Color(148, 163, 184);
    private static final Color ROW_ALT   = new Color(248, 250, 252);

    private static final int MAX_HOA_DON = 5;

    // ── DAO ─────────────────────────────────────────────────────
    private final ChiTietSPDAO  spDAO  = new ChiTietSPDAO();
    private final HoaDonDAO     hdDAO  = new HoaDonDAO();
    private final KhachHangDAO  khDAO  = new KhachHangDAO();
    private final User          nhanVien;

    // ── State: 5 hóa đơn riêng biệt ─────────────────────────────
    private int currentHoaDonIdx = 0; // Index của HD hiện tại (0-4)
    private final List<HoaDonData> hoaDonList = new ArrayList<>(); // 5 hóa đơn

    // ── Inner class: dữ liệu 1 hóa đơn ──────────────────────────
    private static class HoaDonData {
        List<HoaDonChiTiet> gioHang = new ArrayList<>();
        KhachHang khachHang = null;
        DefaultTableModel modelGio;
        JLabel lblKhachHang;
        JLabel lblTongTien;
        JLabel lblSoLuongGio;
        JButton btnThanhToan;
        JButton btnHuyGio;
    }

    // ── UI refs ─────────────────────────────────────────────────
    private JTextField    txtTimSP;
    private JTable        tblSanPham;
    private DefaultTableModel modelSP;
    private JTabbedPane   tabbedPane; // 5 tabs cho 5 hóa đơn

    private static final DecimalFormat FMT = new DecimalFormat("#,###");

    public BanHangPanel(User nhanVien) {
        this.nhanVien = nhanVien;
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        // Khởi tạo 5 hóa đơn
        for (int i = 0; i < MAX_HOA_DON; i++) {
            hoaDonList.add(new HoaDonData());
        }

        build();
        loadSanPham("");
    }

    // ════════════════════════════════════════════════════════════
    // BUILD UI
    // ════════════════════════════════════════════════════════════
    private void build() {
        // Chia đôi: trái = danh sách SP, phải = 5 giỏ hàng (tabs)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(620);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(BG);
        add(split, BorderLayout.CENTER);
    }

    // ── Panel trái: tìm kiếm + danh sách sản phẩm ───────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 16, 16, 8));

        // ── Thanh tìm kiếm ──
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        txtTimSP = new JTextField();
        txtTimSP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimSP.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        txtTimSP.putClientProperty("placeholder", "Tìm kiếm sản phẩm theo tên, mã, màu sắc...");
        txtTimSP.setToolTipText("Tìm kiếm sản phẩm theo tên, mã, màu sắc...");

        JButton btnTim = buildBtn("Tim", ACCENT, ACCENT_H);
        btnTim.setPreferredSize(new Dimension(80, 36));
        btnTim.addActionListener(e -> loadSanPham(txtTimSP.getText().trim()));
        txtTimSP.addActionListener(e -> loadSanPham(txtTimSP.getText().trim()));

        searchBar.add(txtTimSP, BorderLayout.CENTER);
        searchBar.add(btnTim,   BorderLayout.EAST);

        // ── Bảng sản phẩm ──
        String[] cols = {"Mã SP", "Tên sản phẩm", "Màu / Cỡ", "Tồn kho", "Giá bán"};
        modelSP = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPham = new JTable(modelSP);
        styleTable(tblSanPham);
        tblSanPham.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblSanPham.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblSanPham.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblSanPham.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblSanPham.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Double-click hoặc Enter để thêm vào giỏ
        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) themVaoGio();
            }
        });
        tblSanPham.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) themVaoGio();
            }
        });

        JScrollPane scroll = new JScrollPane(tblSanPham);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(WHITE);

        JLabel hint = new JLabel("Nhan doi chuot hoac Enter de them vao gio hang");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_GRAY);

        p.add(searchBar, BorderLayout.NORTH);
        p.add(scroll,    BorderLayout.CENTER);
        p.add(hint,      BorderLayout.SOUTH);
        return p;
    }

    // ── Panel phải: 5 tabs giỏ hàng ─────────────────────────────
    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(WHITE);

        // Tabs cho 5 hóa đơn
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (int i = 0; i < MAX_HOA_DON; i++) {
            JPanel tabContent = buildGioHangPanel(i);
            tabbedPane.addTab("HD " + (i + 1), tabContent);
        }

        tabbedPane.addChangeListener(this::onTabChange);

        p.add(tabbedPane, BorderLayout.CENTER);
        return p;
    }

    // ── Một tab giỏ hàng ────────────────────────────────────────
    private JPanel buildGioHangPanel(int hoaDonIdx) {
        HoaDonData data = hoaDonList.get(hoaDonIdx);

        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 0, 0, BORDER),
                new EmptyBorder(16, 12, 16, 16)));

        // ── Tiêu đề + số lượng ──
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Giỏ hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        data.lblSoLuongGio = new JLabel("(0 sản phẩm)");
        data.lblSoLuongGio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        data.lblSoLuongGio.setForeground(TEXT_GRAY);
        titleRow.add(title);
        titleRow.add(data.lblSoLuongGio);

        // ── Khách hàng ──
        JPanel khRow = buildKhachHangRow(hoaDonIdx);

        // ── Bảng giỏ hàng ──
        String[] colsGio = {"", "Sản phẩm", "Đơn giá", "SL", "Thành tiền", ""};
        data.modelGio = new DefaultTableModel(colsGio, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 3; } // checkbox + SL
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0) ? Boolean.class : (c == 3 ? Integer.class : Object.class);
            }
        };
        JTable tblGioHang = new JTable(data.modelGio);
        styleTable(tblGioHang);
        tblGioHang.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblGioHang.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblGioHang.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblGioHang.getColumnModel().getColumn(3).setPreferredWidth(40);
        tblGioHang.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblGioHang.getColumnModel().getColumn(5).setPreferredWidth(30);

        // Nút xóa trong bảng (cột 5)
        tblGioHang.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("X", DANGER));
        tblGioHang.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), () -> {
            int row = tblGioHang.getSelectedRow();
            if (row >= 0) xoaKhoiGio(hoaDonIdx, row);
        }));

        // Mouse listener để bắt click vào nút X
        tblGioHang.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tblGioHang.rowAtPoint(e.getPoint());
                int col = tblGioHang.columnAtPoint(e.getPoint());

                // Nếu click vào cột X (cột 5), xóa ngay
                if (col == 5 && row >= 0) {
                    xoaKhoiGio(hoaDonIdx, row);
                }
            }
        });

        // Cập nhật khi thay đổi checkbox hoặc SL
        data.modelGio.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                // Khi checkbox thay đổi (cột 0)
                if (col == 0 && row >= 0 && row < data.gioHang.size()) {
                    capNhatTongTien(hoaDonIdx);
                }

                // Khi SL thay đổi (cột 3)
                if (col == 3 && row >= 0 && row < data.gioHang.size()) {
                    Object val = data.modelGio.getValueAt(row, 3);
                    try {
                        int newSL = Integer.parseInt(val.toString());
                        if (newSL > 0) {
                            data.gioHang.get(row).setSoLuong(newSL);
                            BigDecimal donGia = data.gioHang.get(row).getDonGia();
                            data.modelGio.setValueAt(FMT.format(donGia.multiply(BigDecimal.valueOf(newSL))), row, 4);
                            capNhatTongTien(hoaDonIdx);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        });

        JScrollPane scrollGio = new JScrollPane(tblGioHang);
        scrollGio.setBorder(new LineBorder(BORDER, 1, true));
        scrollGio.getViewport().setBackground(WHITE);

        // ── Footer thanh toán ──
        JPanel footer = buildFooter(hoaDonIdx);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(titleRow, BorderLayout.NORTH);
        top.add(khRow,    BorderLayout.SOUTH);

        p.add(top,       BorderLayout.NORTH);
        p.add(scrollGio, BorderLayout.CENTER);
        p.add(footer,    BorderLayout.SOUTH);
        return p;
    }

    // ── Hàng thông tin khách hàng ─────────────────────────────────
    private JPanel buildKhachHangRow(int hoaDonIdx) {
        HoaDonData data = hoaDonList.get(hoaDonIdx);

        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        data.lblKhachHang = new JLabel("Khách hàng: Khách lẻ");
        data.lblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        data.lblKhachHang.setForeground(TEXT_MID);

        // Nút Danh sách khách hàng
        JButton btnDanhSach = buildBtn("Danh sách KH", new Color(99, 102, 241), new Color(79, 70, 229));
        btnDanhSach.setPreferredSize(new Dimension(130, 32));
        btnDanhSach.addActionListener(e -> danhSachKhachHang(hoaDonIdx));

        // Nút BỎ TICK TẤT CẢ sản phẩm (thay vì bỏ khách hàng)
        JButton btnBoTick = buildBtn("Bỏ tick tất cả", new Color(234, 88, 12), new Color(194, 65, 12));
        btnBoTick.setPreferredSize(new Dimension(120, 32));
        btnBoTick.addActionListener(e -> boChonTatCaSanPham(hoaDonIdx));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(btnDanhSach);
        rightPanel.add(btnBoTick);

        p.add(data.lblKhachHang, BorderLayout.WEST);
        p.add(rightPanel, BorderLayout.EAST);

        return p;
    }

    // ── Footer: tổng tiền + nút ──────────────────────────────────
    private JPanel buildFooter(int hoaDonIdx) {
        HoaDonData data = hoaDonList.get(hoaDonIdx);

        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Đường kẻ
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(Box.createVerticalStrut(12));

        // Phương thức thanh toán
        JPanel pttRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pttRow.setOpaque(false);
        JLabel lPTT = new JLabel("Phương thức:");
        lPTT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lPTT.setForeground(TEXT_MID);
        JComboBox<String> cboThanhToan = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        cboThanhToan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pttRow.add(lPTT);
        pttRow.add(cboThanhToan);
        pttRow.setAlignmentX(LEFT_ALIGNMENT);
        p.add(pttRow);
        p.add(Box.createVerticalStrut(10));

        // Tổng tiền
        JPanel tongRow = new JPanel(new BorderLayout());
        tongRow.setOpaque(false);
        tongRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel lbTong = new JLabel("Tổng tiền:");
        lbTong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTong.setForeground(TEXT_DARK);
        data.lblTongTien = new JLabel("0 VND");
        data.lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        data.lblTongTien.setForeground(ACCENT);
        data.lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);
        tongRow.add(lbTong,     BorderLayout.WEST);
        tongRow.add(data.lblTongTien, BorderLayout.EAST);
        p.add(tongRow);
        p.add(Box.createVerticalStrut(12));

        // Nút
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        data.btnHuyGio = buildBtn("Hủy giỏ hàng", DANGER, new Color(185,28,28));
        data.btnHuyGio.addActionListener(e -> huyGioHang(hoaDonIdx));

        data.btnThanhToan = buildBtn("Thanh toán", SUCCESS, new Color(15,118,54));
        data.btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        data.btnThanhToan.addActionListener(e -> thanhToan(hoaDonIdx, cboThanhToan));

        btnRow.add(data.btnHuyGio);
        btnRow.add(data.btnThanhToan);
        p.add(btnRow);

        return p;
    }

    // ════════════════════════════════════════════════════════════
    // LOGIC
    // ════════════════════════════════════════════════════════════

    private void onTabChange(ChangeEvent e) {
        currentHoaDonIdx = tabbedPane.getSelectedIndex();
    }

    /** Load danh sách sản phẩm */
    private void loadSanPham(String keyword) {
        SwingWorker<List<ChitietSP>, Void> worker = new SwingWorker<>() {
            @Override protected List<ChitietSP> doInBackground() {
                return spDAO.search(keyword);
            }
            @Override protected void done() {
                try {
                    List<ChitietSP> list = get();
                    modelSP.setRowCount(0);
                    for (ChitietSP sp : list) {
                        String mauCo = "";
                        if (sp.getTenMauSac() != null) mauCo += sp.getTenMauSac();
                        if (sp.getTenKichCo()  != null) mauCo += (mauCo.isEmpty() ? "" : " / ") + sp.getTenKichCo();
                        modelSP.addRow(new Object[]{
                                sp.getMa(),
                                sp.getTen(),
                                mauCo,
                                sp.getSoLuongTon(),
                                FMT.format(sp.getGiaBan()) + " đ"
                        });
                    }
                    // Lưu danh sách để lấy theo index
                    tblSanPham.putClientProperty("data", list);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        worker.execute();
    }

    /** Thêm sản phẩm được chọn vào giỏ của hóa đơn hiện tại */
    @SuppressWarnings("unchecked")
    private void themVaoGio() {
        int row = tblSanPham.getSelectedRow();
        if (row < 0) { showMsg("Vui lòng chọn sản phẩm!", "Thông báo", false); return; }

        List<ChitietSP> data = (List<ChitietSP>) tblSanPham.getClientProperty("data");
        if (data == null || row >= data.size()) return;

        ChitietSP sp = data.get(row);
        HoaDonData hoaDon = hoaDonList.get(currentHoaDonIdx);

        // Kiểm tra đã có trong giỏ chưa → tăng SL
        for (int i = 0; i < hoaDon.gioHang.size(); i++) {
            if (hoaDon.gioHang.get(i).getIdCTSP() == sp.getId()) {
                int newSL = hoaDon.gioHang.get(i).getSoLuong() + 1;
                if (newSL > sp.getSoLuongTon()) {
                    showMsg("Vượt quá số lượng tồn kho!", "Cảnh báo", false);
                    return;
                }
                hoaDon.gioHang.get(i).setSoLuong(newSL);
                hoaDon.modelGio.setValueAt(newSL, i, 3);
                hoaDon.modelGio.setValueAt(FMT.format(sp.getGiaBan().multiply(BigDecimal.valueOf(newSL))), i, 4);
                capNhatTongTien(currentHoaDonIdx);
                return;
            }
        }

        // Thêm mới vào giỏ
        HoaDonChiTiet ct = new HoaDonChiTiet(
                sp.getId(), sp.getMa(), sp.getTen(),
                sp.getTenMauSac(), sp.getTenKichCo(),
                1, sp.getGiaBan()
        );
        hoaDon.gioHang.add(ct);
        hoaDon.modelGio.addRow(new Object[]{
                true, // checkbox - chọn mặc định
                ct.getTenHienThi(),
                FMT.format(sp.getGiaBan()),
                1,
                FMT.format(sp.getGiaBan()),
                "X"
        });
        capNhatTongTien(currentHoaDonIdx);
    }

    /** Xóa item khỏi giỏ */
    private void xoaKhoiGio(int hoaDonIdx, int row) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        if (row >= 0 && row < hoaDon.gioHang.size()) {
            hoaDon.gioHang.remove(row);
            hoaDon.modelGio.removeRow(row);
            capNhatTongTien(hoaDonIdx);
        }
    }

    /** Cập nhật tổng tiền và số lượng giỏ (chỉ tính item được chọn) */
    private void capNhatTongTien(int hoaDonIdx) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        BigDecimal tong = BigDecimal.ZERO;
        int soLuongChon = 0;

        for (int i = 0; i < hoaDon.gioHang.size(); i++) {
            // Kiểm tra checkbox (cột 0)
            Boolean isChecked = (Boolean) hoaDon.modelGio.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                tong = tong.add(hoaDon.gioHang.get(i).getThanhTien());
                soLuongChon++;
            }
        }
        hoaDon.lblTongTien.setText(FMT.format(tong) + " VND");
        hoaDon.lblSoLuongGio.setText("(" + soLuongChon + "/" + hoaDon.gioHang.size() + " sản phẩm)");
    }

    /** Tìm kiếm khách hàng */
    private void timKhachHang(int hoaDonIdx, String kw) {
        if (kw.isEmpty()) return;

        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override protected List<KhachHang> doInBackground() {
                return khDAO.search(kw);
            }
            @Override protected void done() {
                try {
                    List<KhachHang> list = get();
                    if (list.isEmpty()) {
                        showMsg("Khong tim thay khach hang!", "Thong bao", false);
                        return;
                    }
                    if (list.size() == 1) {
                        chonKhachHang(hoaDonIdx, list.get(0));
                        return;
                    }
                    // Nhiều kết quả → hiện dialog chọn
                    String[] options = list.stream()
                            .map(k -> k.getHoTenDay() + " - " + (k.getSdt() != null ? k.getSdt() : k.getEmail()))
                            .toArray(String[]::new);
                    int idx = JOptionPane.showOptionDialog(
                            BanHangPanel.this,
                            "Chon khach hang:",
                            "Ket qua tim kiem",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null, options, options[0]);
                    if (idx >= 0) chonKhachHang(hoaDonIdx, list.get(idx));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void chonKhachHang(int hoaDonIdx, KhachHang kh) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        hoaDon.khachHang = kh;
        hoaDon.lblKhachHang.setText("KH: " + kh.getHoTenDay()
                + (kh.getSdt() != null ? " - " + kh.getSdt() : ""));
        hoaDon.lblKhachHang.setForeground(new Color(22, 163, 74));
    }
    /** Bỏ tick tất cả sản phẩm trong giỏ hàng */
    private void boChonTatCaSanPham(int hoaDonIdx) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);

        if (hoaDon.modelGio.getRowCount() == 0) return;

        boolean daBoTick = false;

        for (int i = 0; i < hoaDon.modelGio.getRowCount(); i++) {
            if (Boolean.TRUE.equals(hoaDon.modelGio.getValueAt(i, 0))) {
                hoaDon.modelGio.setValueAt(false, i, 0);
                daBoTick = true;
            }
        }

        if (daBoTick) {
            capNhatTongTien(hoaDonIdx);
        }
    }
    /** Mở dialog danh sách khách hàng */
    private void danhSachKhachHang(int hoaDonIdx) {
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override protected List<KhachHang> doInBackground() {
                return khDAO.search(""); // Lấy tất cả khách hàng
            }
            @Override protected void done() {
                try {
                    List<KhachHang> list = get();
                    if (list.isEmpty()) {
                        showMsg("Khong co khach hang nao!", "Thong bao", false);
                        return;
                    }

                    // Tạo JDialog để hiển thị danh sách
                    JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(BanHangPanel.this),
                            "Danh sách khách hàng", Dialog.ModalityType.APPLICATION_MODAL);
                    dlg.setSize(600, 400);
                    dlg.setLocationRelativeTo(BanHangPanel.this);

                    // Bảng hiển thị khách hàng
                    String[] cols = {"Tên", "SĐT", "Email", "Điểm thưởng"};
                    DefaultTableModel model = new DefaultTableModel(cols, 0) {
                        @Override public boolean isCellEditable(int r, int c) { return false; }
                    };
                    for (KhachHang kh : list) {
                        model.addRow(new Object[]{
                                kh.getHoTenDay(),
                                kh.getSdt() != null ? kh.getSdt() : "",
                                kh.getEmail(),
                                kh.getDiemThuong() != null ? kh.getDiemThuong() : 0
                        });
                    }

                    JTable tbl = new JTable(model);
                    styleTable(tbl);
                    JScrollPane scroll = new JScrollPane(tbl);

                    // Nút chọn + khách lẻ
                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                    btnPanel.setBackground(WHITE);

                    JButton btnChon = buildBtn("Chọn", ACCENT, ACCENT_H);
                    btnChon.addActionListener(e -> {
                        int row = tbl.getSelectedRow();
                        if (row >= 0) {
                            chonKhachHang(hoaDonIdx, list.get(row));
                            dlg.dispose();
                        } else {
                            showMsg("Vui lòng chọn khách hàng!", "Cảnh báo", false);
                        }
                    });

                    JButton btnKhachLe = buildBtn("Khách lẻ", new Color(100,116,139), new Color(71,85,105));
                    btnKhachLe.addActionListener(e -> {
                        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
                        hoaDon.khachHang = null;
                        hoaDon.lblKhachHang.setText("Khách hàng: Khách lẻ");
                        hoaDon.lblKhachHang.setForeground(TEXT_MID);
                        dlg.dispose();
                    });

                    JButton btnDong = buildBtn("Đóng", DANGER, new Color(185,28,28));
                    btnDong.addActionListener(e -> dlg.dispose());

                    btnPanel.add(btnChon);
                    btnPanel.add(btnKhachLe);
                    btnPanel.add(btnDong);

                    dlg.add(scroll, BorderLayout.CENTER);
                    dlg.add(btnPanel, BorderLayout.SOUTH);
                    dlg.setVisible(true);

                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        worker.execute();
    }

    /** Huỷ giỏ hàng */
    private void huyGioHang(int hoaDonIdx) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        if (hoaDon.gioHang.isEmpty()) return;
        int r = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy giỏ hàng?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            hoaDon.gioHang.clear();
            hoaDon.modelGio.setRowCount(0);
            hoaDon.khachHang = null;
            hoaDon.lblKhachHang.setText("Khách hàng: Khách lẻ");
            hoaDon.lblKhachHang.setForeground(TEXT_MID);
            capNhatTongTien(hoaDonIdx);
        }
    }

    /** Thanh toán một hóa đơn */
    private void thanhToan(int hoaDonIdx, JComboBox<String> cboPhuongThuc) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);

        if (hoaDon.gioHang.isEmpty()) {
            showMsg("Giỏ hàng trống! vui lòng thêm sản phẩm.", "Thông báo", false);
            return;
        }

        // Tính tổng - chỉ những item được chọn
        BigDecimal tong = BigDecimal.ZERO;
        List<HoaDonChiTiet> ctThanhToan = new ArrayList<>();
        for (int i = 0; i < hoaDon.gioHang.size(); i++) {
            Boolean isChecked = (Boolean) hoaDon.modelGio.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                HoaDonChiTiet ct = hoaDon.gioHang.get(i);
                tong = tong.add(ct.getThanhTien());
                ctThanhToan.add(ct);
            }
        }

        if (ctThanhToan.isEmpty()) {
            showMsg("Vui lòng chọn sản phẩm để thanh toán!", "Cảnh báo", false);
            return;
        }

        // Xác nhận
        String khTen = hoaDon.khachHang != null ? hoaDon.khachHang.getHoTenDay() : "Khách lẻ";
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Xác nhận thanh toán?<br>" +
                        "<b>Hóa đơn:</b> HD " + (hoaDonIdx + 1) + "<br>" +
                        "<b>Khách hàng:</b> " + khTen + "<br>" +
                        "<b>Tổng tiền:</b> " + FMT.format(tong) + " VND<br>" +
                        "<b>Phương thức:</b> " + cboPhuongThuc.getSelectedItem() + "</html>",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        hoaDon.btnThanhToan.setEnabled(false);
        hoaDon.btnThanhToan.setText("Đang xử lý...");

        final BigDecimal finalTong = tong;
        final List<HoaDonChiTiet> finalCtThanhToan = ctThanhToan;
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override protected Integer doInBackground() {
                HoaDon hd = new HoaDon();
                hd.setIdNV(nhanVien.getId());
                if (hoaDon.khachHang != null) hd.setIdKH(hoaDon.khachHang.getId());
                hd.setMa(HoaDonDAO.sinhMaHoaDon());
                hd.setNgayTao(LocalDateTime.now());
                hd.setNgayThanhToan(LocalDate.now());
                hd.setTinhTrang(true);
                hd.setGhiChu(cboPhuongThuc.getSelectedItem() != null ?
                        cboPhuongThuc.getSelectedItem().toString() : "");
                hd.setTongTien(finalTong);
                return hdDAO.taoHoaDon(hd, finalCtThanhToan);
            }
            @Override protected void done() {
                try {
                    int idHD = get();
                    if (idHD > 0) {
                        // Xóa những item đã thanh toán khỏi giỏ
                        for (int i = hoaDon.gioHang.size() - 1; i >= 0; i--) {
                            Boolean isChecked = (Boolean) hoaDon.modelGio.getValueAt(i, 0);
                            if (isChecked != null && isChecked) {
                                hoaDon.gioHang.remove(i);
                                hoaDon.modelGio.removeRow(i);
                            }
                        }

                        // Nếu giỏ trống, reset khách hàng
                        if (hoaDon.gioHang.isEmpty()) {
                            hoaDon.khachHang = null;
                            hoaDon.lblKhachHang.setText("Khách hàng: Khách lẻ");
                            hoaDon.lblKhachHang.setForeground(TEXT_MID);
                        }

                        capNhatTongTien(hoaDonIdx);
                        loadSanPham(""); // refresh ton kho

                        showMsg("<html>Thanh toán thành công!<br>Mã hóa đơn: HD-" + idHD + "</html>",
                                "Thành công", true);
                    } else {
                        showMsg("Lỗi khi tạo hóa đơn. Vui lòng thử lại!", "Lỗi", false);
                    }
                } catch (Exception ex) {
                    showMsg("Lỗi: " + ex.getMessage(), "Lỗi", false);
                } finally {
                    hoaDon.btnThanhToan.setEnabled(true);
                    hoaDon.btnThanhToan.setText("Thanh toán");
                }
            }
        };
        worker.execute();
    }

    private void showMsg(String msg, String title, boolean success) {
        JOptionPane.showMessageDialog(this, msg, title,
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    // ════════════════════════════════════════════════════════════
    // HELPERS UI
    // ════════════════════════════════════════════════════════════
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_MID);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 38));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                return this;
            }
        });
        table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                return this;
            }
        });

        // Renderer cho Boolean (checkbox)
        table.setDefaultRenderer(Boolean.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                JCheckBox cb = new JCheckBox();
                cb.setSelected(val != null && (Boolean) val);
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setBackground(sel ? new Color(238, 242, 255) : (row % 2 == 0 ? WHITE : ROW_ALT));
                return cb;
            }
        });
    }

    private JButton buildBtn(String text, Color fill, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Renderer nút X trong bảng ────────────────────────────────
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer(String text, Color color) {
            setText(text);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(color);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            return this;
        }
    }

    // ── Editor nút X trong bảng ──────────────────────────────────
    static class ButtonEditor extends DefaultCellEditor {
        private final JButton btn;
        private final Runnable action;
        ButtonEditor(JCheckBox cb, Runnable action) {
            super(cb);
            this.action = action;
            btn = new JButton("X");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(DANGER);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                fireEditingStopped();
                action.run();
            });
        }
        @Override public Component getTableCellEditorComponent(
                JTable t, Object val, boolean sel, int row, int col) {
            return btn;
        }
        @Override public Object getCellEditorValue() { return "X"; }
    }
}