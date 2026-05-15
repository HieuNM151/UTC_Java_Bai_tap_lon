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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BanHangPanel extends JPanel {

    // ════════════════════════════════════════════════════════════
    // THEME
    // ════════════════════════════════════════════════════════════
    private static final Color BG          = new Color(245, 247, 251);
    private static final Color WHITE       = Color.WHITE;
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color ACCENT      = new Color(79, 70, 229);
    private static final Color ACCENT_H    = new Color(67, 56, 202);
    private static final Color SUCCESS     = new Color(22, 163, 74);
    private static final Color SUCCESS_H   = new Color(21, 128, 61);
    private static final Color DANGER      = new Color(239, 68, 68);
    private static final Color DANGER_H    = new Color(220, 38, 38);
    private static final Color WARNING     = new Color(234, 88, 12);
    private static final Color WARNING_H   = new Color(194, 65, 12);
    private static final Color BORDER      = new Color(226, 232, 240);
    private static final Color BORDER_DARK = new Color(203, 213, 225);
    private static final Color TEXT_DARK   = new Color(15, 23, 42);
    private static final Color TEXT_MID    = new Color(71, 85, 105);
    private static final Color TEXT_GRAY   = new Color(148, 163, 184);
    private static final Color ROW_ALT     = new Color(248, 250, 252);
    private static final Color SELECT_BG   = new Color(238, 242, 255);

    private static final Font FONT_12  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_13  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_14  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_12  = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font BOLD_13  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font BOLD_14  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BOLD_16  = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BOLD_18  = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BOLD_26  = new Font("Segoe UI", Font.BOLD, 26);

    private static final int MAX_HOA_DON = 5;
    private static final DecimalFormat FMT = new DecimalFormat("#,###");

    // ════════════════════════════════════════════════════════════
    // DAO + STATE
    // ════════════════════════════════════════════════════════════
    private final ChiTietSPDAO spDAO = new ChiTietSPDAO();
    private final HoaDonDAO hdDAO = new HoaDonDAO();
    private final KhachHangDAO khDAO = new KhachHangDAO();
    private final User nhanVien;

    private int currentHoaDonIdx = 0;
    private final List<HoaDonData> hoaDonList = new ArrayList<>();

    private JTextField txtTimSP;
    private JTable tblSanPham;
    private DefaultTableModel modelSP;
    private JTabbedPane tabbedPane;

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

    public BanHangPanel(User nhanVien) {
        this.nhanVien = nhanVien;
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

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
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(),
                buildRightPanel()
        );
        split.setDividerLocation(620);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setBackground(BG);
        split.setContinuousLayout(true);

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 10));

        JPanel searchCard = createCardPanel(new BorderLayout(10, 0), 12, 12, 12, 12);

        txtTimSP = new JTextField();
        txtTimSP.setFont(FONT_14);
        txtTimSP.setForeground(TEXT_DARK);
        txtTimSP.setPreferredSize(new Dimension(0, 42));
        txtTimSP.setToolTipText("Tìm sản phẩm theo mã, tên, màu sắc, kích cỡ...");
        txtTimSP.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_DARK, 1, true),
                new EmptyBorder(0, 14, 0, 14)
        ));

        JButton btnTim = buildBtn("Tìm", ACCENT, ACCENT_H, 92, 42);
        btnTim.addActionListener(e -> loadSanPham(txtTimSP.getText().trim()));
        txtTimSP.addActionListener(e -> loadSanPham(txtTimSP.getText().trim()));

        searchCard.add(txtTimSP, BorderLayout.CENTER);
        searchCard.add(btnTim, BorderLayout.EAST);

        JPanel tableCard = createCardPanel(new BorderLayout(0, 10), 0, 0, 0, 0);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(new EmptyBorder(14, 16, 0, 16));

        JLabel title = new JLabel("Danh sách sản phẩm");
        title.setFont(BOLD_16);
        title.setForeground(TEXT_DARK);

        JLabel hintTop = new JLabel("Double click hoặc Enter để thêm vào giỏ");
        hintTop.setFont(FONT_12);
        hintTop.setForeground(TEXT_GRAY);
        hintTop.setHorizontalAlignment(SwingConstants.RIGHT);

        tableHeader.add(title, BorderLayout.WEST);
        tableHeader.add(hintTop, BorderLayout.EAST);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Màu / Cỡ", "Tồn", "Giá bán"};
        modelSP = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblSanPham = new JTable(modelSP);
        styleTable(tblSanPham);
        setColumnWidth(tblSanPham, 0, 75);
        setColumnWidth(tblSanPham, 1, 210);
        setColumnWidth(tblSanPham, 2, 105);
        setColumnWidth(tblSanPham, 3, 60);
        setColumnWidth(tblSanPham, 4, 100);

        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) themVaoGio();
            }
        });
        tblSanPham.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    themVaoGio();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblSanPham);
        scroll.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        scroll.getViewport().setBackground(WHITE);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        wrapper.add(searchCard, BorderLayout.NORTH);
        wrapper.add(tableCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildRightPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(20, 10, 20, 20));

        JPanel card = createCardPanel(new BorderLayout(), 0, 0, 0, 0);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(BOLD_13);
        tabbedPane.setBackground(WHITE);
        tabbedPane.setForeground(TEXT_MID);
        tabbedPane.setBorder(null);

        for (int i = 0; i < MAX_HOA_DON; i++) {
            tabbedPane.addTab("HD " + (i + 1), buildGioHangPanel(i));
        }

        tabbedPane.addChangeListener(this::onTabChange);
        card.add(tabbedPane, BorderLayout.CENTER);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildGioHangPanel(int hoaDonIdx) {
        HoaDonData data = hoaDonList.get(hoaDonIdx);

        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JPanel titleLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleLeft.setOpaque(false);
        JLabel title = new JLabel("Giỏ hàng");
        title.setFont(BOLD_18);
        title.setForeground(TEXT_DARK);

        data.lblSoLuongGio = new JLabel("0 sản phẩm");
        data.lblSoLuongGio.setOpaque(true);
        data.lblSoLuongGio.setBackground(SELECT_BG);
        data.lblSoLuongGio.setForeground(ACCENT);
        data.lblSoLuongGio.setFont(BOLD_12);
        data.lblSoLuongGio.setBorder(new EmptyBorder(5, 10, 5, 10));

        titleLeft.add(title);
        titleLeft.add(data.lblSoLuongGio);

        titleRow.add(titleLeft, BorderLayout.WEST);
        top.add(titleRow, BorderLayout.NORTH);
        top.add(buildKhachHangRow(hoaDonIdx), BorderLayout.CENTER);

        String[] colsGio = {"", "Sản phẩm", "Đơn giá", "SL", "Thành tiền", ""};
        data.modelGio = new DefaultTableModel(colsGio, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 3 || c == 5; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0) return Boolean.class;
                if (c == 3) return Integer.class;
                return Object.class;
            }
        };

        JTable tblGioHang = new JTable(data.modelGio);
        styleTable(tblGioHang);
        setColumnWidth(tblGioHang, 0, 34);
        setColumnWidth(tblGioHang, 1, 190);
        setColumnWidth(tblGioHang, 2, 90);
        setColumnWidth(tblGioHang, 3, 48);
        setColumnWidth(tblGioHang, 4, 110);
        setColumnWidth(tblGioHang, 5, 42);

        tblGioHang.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("X", DANGER));
        tblGioHang.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), () -> {
            int row = tblGioHang.getSelectedRow();
            if (row >= 0) xoaKhoiGio(hoaDonIdx, row);
        }));

        data.modelGio.addTableModelListener(e -> {
            if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;

            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row < 0 || row >= data.gioHang.size()) return;

            if (col == 0) {
                capNhatTongTien(hoaDonIdx);
            }

            if (col == 3) {
                Object val = data.modelGio.getValueAt(row, 3);
                try {
                    int newSL = Integer.parseInt(val.toString());
                    if (newSL <= 0) {
                        data.modelGio.setValueAt(1, row, 3);
                        return;
                    }
                    data.gioHang.get(row).setSoLuong(newSL);
                    BigDecimal donGia = data.gioHang.get(row).getDonGia();
                    data.modelGio.setValueAt(FMT.format(donGia.multiply(BigDecimal.valueOf(newSL))), row, 4);
                    capNhatTongTien(hoaDonIdx);
                } catch (Exception ex) {
                    data.modelGio.setValueAt(data.gioHang.get(row).getSoLuong(), row, 3);
                }
            }
        });

        JScrollPane scrollGio = new JScrollPane(tblGioHang);
        scrollGio.setBorder(new LineBorder(BORDER, 1, true));
        scrollGio.getViewport().setBackground(WHITE);

        p.add(top, BorderLayout.NORTH);
        p.add(scrollGio, BorderLayout.CENTER);
        p.add(buildFooter(hoaDonIdx), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildKhachHangRow(int hoaDonIdx) {
        HoaDonData data = hoaDonList.get(hoaDonIdx);

        JPanel wrapper = new JPanel(new BorderLayout(12, 0));
        wrapper.setOpaque(false);

        JPanel khCard = new JPanel(new BorderLayout(8, 0));
        khCard.setBackground(new Color(248, 250, 252));
        khCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel icon = new JLabel("👤");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        data.lblKhachHang = new JLabel("Khách lẻ");
        data.lblKhachHang.setFont(BOLD_14);
        data.lblKhachHang.setForeground(TEXT_DARK);

        khCard.add(icon, BorderLayout.WEST);
        khCard.add(data.lblKhachHang, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnDanhSach = buildBtn("Danh sách KH", ACCENT, ACCENT_H, 132, 40);
        btnDanhSach.addActionListener(e -> danhSachKhachHang(hoaDonIdx));

        JButton btnBoTick = buildBtn("Bỏ tick tất cả", WARNING, WARNING_H, 128, 40);
        btnBoTick.addActionListener(e -> boChonTatCaSanPham(hoaDonIdx));

        actions.add(btnDanhSach);
        actions.add(btnBoTick);

        wrapper.add(khCard, BorderLayout.CENTER);
        wrapper.add(actions, BorderLayout.EAST);
        return wrapper;
    }

    private JPanel buildFooter(int hoaDonIdx) {
        HoaDonData data = hoaDonList.get(hoaDonIdx);

        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(4, 0, 0, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(Box.createVerticalStrut(14));

        JPanel paymentRow = new JPanel(new BorderLayout());
        paymentRow.setOpaque(false);
        paymentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel lPTT = new JLabel("Phương thức thanh toán");
        lPTT.setFont(BOLD_13);
        lPTT.setForeground(TEXT_MID);

        JComboBox<String> cboThanhToan = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        cboThanhToan.setFont(FONT_13);
        cboThanhToan.setPreferredSize(new Dimension(170, 36));

        paymentRow.add(lPTT, BorderLayout.WEST);
        paymentRow.add(cboThanhToan, BorderLayout.EAST);
        p.add(paymentRow);
        p.add(Box.createVerticalStrut(10));

        JPanel totalCard = new JPanel(new BorderLayout());
        totalCard.setBackground(new Color(248, 250, 252));
        totalCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        totalCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lbTong = new JLabel("Tổng tiền");
        lbTong.setFont(BOLD_14);
        lbTong.setForeground(TEXT_DARK);

        data.lblTongTien = new JLabel("0 VND");
        data.lblTongTien.setFont(BOLD_26);
        data.lblTongTien.setForeground(ACCENT);
        data.lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);

        totalCard.add(lbTong, BorderLayout.WEST);
        totalCard.add(data.lblTongTien, BorderLayout.EAST);
        p.add(totalCard);
        p.add(Box.createVerticalStrut(12));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        data.btnHuyGio = buildBtn("Hủy giỏ hàng", DANGER, DANGER_H, 0, 46);
        data.btnHuyGio.addActionListener(e -> huyGioHang(hoaDonIdx));

        data.btnThanhToan = buildBtn("Thanh toán", SUCCESS, SUCCESS_H, 0, 46);
        data.btnThanhToan.setFont(BOLD_14);
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
                        if (sp.getTenKichCo() != null) mauCo += (mauCo.isEmpty() ? "" : " / ") + sp.getTenKichCo();

                        modelSP.addRow(new Object[]{
                                sp.getMa(),
                                sp.getTen(),
                                mauCo,
                                sp.getSoLuongTon(),
                                FMT.format(sp.getGiaBan()) + " đ"
                        });
                    }
                    tblSanPham.putClientProperty("data", list);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMsg("Lỗi tải sản phẩm: " + ex.getMessage(), "Lỗi", false);
                }
            }
        };
        worker.execute();
    }

    @SuppressWarnings("unchecked")
    private void themVaoGio() {
        int viewRow = tblSanPham.getSelectedRow();
        if (viewRow < 0) {
            showMsg("Vui lòng chọn sản phẩm!", "Thông báo", false);
            return;
        }

        int row = tblSanPham.convertRowIndexToModel(viewRow);
        List<ChitietSP> data = (List<ChitietSP>) tblSanPham.getClientProperty("data");
        if (data == null || row < 0 || row >= data.size()) return;

        ChitietSP sp = data.get(row);
        HoaDonData hoaDon = hoaDonList.get(currentHoaDonIdx);

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

        HoaDonChiTiet ct = new HoaDonChiTiet(
                sp.getId(),
                sp.getMa(),
                sp.getTen(),
                sp.getTenMauSac(),
                sp.getTenKichCo(),
                1,
                sp.getGiaBan()
        );

        hoaDon.gioHang.add(ct);
        hoaDon.modelGio.addRow(new Object[]{
                true,
                ct.getTenHienThi(),
                FMT.format(sp.getGiaBan()),
                1,
                FMT.format(sp.getGiaBan()),
                "X"
        });
        capNhatTongTien(currentHoaDonIdx);
    }

    private void xoaKhoiGio(int hoaDonIdx, int viewRow) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        int row = viewRow;
        if (row >= 0 && row < hoaDon.gioHang.size()) {
            hoaDon.gioHang.remove(row);
            hoaDon.modelGio.removeRow(row);
            capNhatTongTien(hoaDonIdx);
        }
    }

    private void capNhatTongTien(int hoaDonIdx) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        BigDecimal tong = BigDecimal.ZERO;
        int soLuongChon = 0;

        for (int i = 0; i < hoaDon.gioHang.size(); i++) {
            Boolean isChecked = (Boolean) hoaDon.modelGio.getValueAt(i, 0);
            if (Boolean.TRUE.equals(isChecked)) {
                tong = tong.add(hoaDon.gioHang.get(i).getThanhTien());
                soLuongChon++;
            }
        }

        hoaDon.lblTongTien.setText(FMT.format(tong) + " VND");
        hoaDon.lblSoLuongGio.setText(soLuongChon + "/" + hoaDon.gioHang.size() + " sản phẩm");
    }

    private void chonKhachHang(int hoaDonIdx, KhachHang kh) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        hoaDon.khachHang = kh;
        hoaDon.lblKhachHang.setText(kh.getHoTenDay() + (kh.getSdt() != null ? " - " + kh.getSdt() : ""));
        hoaDon.lblKhachHang.setForeground(SUCCESS);
    }

    private void boChonTatCaSanPham(int hoaDonIdx) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        if (hoaDon.modelGio.getRowCount() == 0) return;

        for (int i = 0; i < hoaDon.modelGio.getRowCount(); i++) {
            hoaDon.modelGio.setValueAt(false, i, 0);
        }
        capNhatTongTien(hoaDonIdx);
    }

    private void danhSachKhachHang(int hoaDonIdx) {
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override protected List<KhachHang> doInBackground() {
                return khDAO.search("");
            }

            @Override protected void done() {
                try {
                    List<KhachHang> list = get();
                    if (list.isEmpty()) {
                        showMsg("Không có khách hàng nào!", "Thông báo", false);
                        return;
                    }
                    showKhachHangDialog(hoaDonIdx, list);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMsg("Lỗi tải khách hàng: " + ex.getMessage(), "Lỗi", false);
                }
            }
        };
        worker.execute();
    }

    private void showKhachHangDialog(int hoaDonIdx, List<KhachHang> list) {
        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Danh sách khách hàng",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dlg.setSize(720, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 0));

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Chọn khách hàng");
        title.setFont(BOLD_18);
        title.setForeground(TEXT_DARK);

        String[] cols = {"Tên", "SĐT", "Email", "Điểm thưởng"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (KhachHang kh : list) {
            model.addRow(new Object[]{
                    kh.getHoTenDay(),
                    kh.getSdt() != null ? kh.getSdt() : "",
                    kh.getEmail() != null ? kh.getEmail() : "",
                    kh.getDiemThuong() != null ? kh.getDiemThuong() : 0
            });
        }

        JTable tbl = new JTable(model);
        styleTable(tbl);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(new LineBorder(BORDER, 1, true));

        tbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tbl.getSelectedRow();
                    if (row >= 0) {
                        chonKhachHang(hoaDonIdx, list.get(tbl.convertRowIndexToModel(row)));
                        dlg.dispose();
                    }
                }
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnKhachLe = buildBtn("Khách lẻ", new Color(100, 116, 139), new Color(71, 85, 105), 100, 40);
        btnKhachLe.addActionListener(e -> {
            HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
            hoaDon.khachHang = null;
            hoaDon.lblKhachHang.setText("Khách lẻ");
            hoaDon.lblKhachHang.setForeground(TEXT_DARK);
            dlg.dispose();
        });

        JButton btnDong = buildBtn("Đóng", DANGER, DANGER_H, 90, 40);
        btnDong.addActionListener(e -> dlg.dispose());

        JButton btnChon = buildBtn("Chọn", ACCENT, ACCENT_H, 90, 40);
        btnChon.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row >= 0) {
                chonKhachHang(hoaDonIdx, list.get(tbl.convertRowIndexToModel(row)));
                dlg.dispose();
            } else {
                showMsg("Vui lòng chọn khách hàng!", "Cảnh báo", false);
            }
        });

        btnPanel.add(btnKhachLe);
        btnPanel.add(btnDong);
        btnPanel.add(btnChon);

        root.add(title, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        dlg.add(root, BorderLayout.CENTER);
        dlg.setVisible(true);
    }

    private void huyGioHang(int hoaDonIdx) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);
        if (hoaDon.gioHang.isEmpty()) return;

        int r = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn hủy giỏ hàng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (r == JOptionPane.YES_OPTION) {
            hoaDon.gioHang.clear();
            hoaDon.modelGio.setRowCount(0);
            hoaDon.khachHang = null;
            hoaDon.lblKhachHang.setText("Khách lẻ");
            hoaDon.lblKhachHang.setForeground(TEXT_DARK);
            capNhatTongTien(hoaDonIdx);
        }
    }

    private void thanhToan(int hoaDonIdx, JComboBox<String> cboPhuongThuc) {
        HoaDonData hoaDon = hoaDonList.get(hoaDonIdx);

        if (hoaDon.gioHang.isEmpty()) {
            showMsg("Giỏ hàng trống! Vui lòng thêm sản phẩm.", "Thông báo", false);
            return;
        }

        BigDecimal tong = BigDecimal.ZERO;
        List<HoaDonChiTiet> ctThanhToan = new ArrayList<>();

        for (int i = 0; i < hoaDon.gioHang.size(); i++) {
            Boolean isChecked = (Boolean) hoaDon.modelGio.getValueAt(i, 0);
            if (Boolean.TRUE.equals(isChecked)) {
                HoaDonChiTiet ct = hoaDon.gioHang.get(i);
                tong = tong.add(ct.getThanhTien());
                ctThanhToan.add(ct);
            }
        }

        if (ctThanhToan.isEmpty()) {
            showMsg("Vui lòng chọn sản phẩm để thanh toán!", "Cảnh báo", false);
            return;
        }

        String khTen = hoaDon.khachHang != null ? hoaDon.khachHang.getHoTenDay() : "Khách lẻ";
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "<html>" +
                        "<div style='font-family:Segoe UI;font-size:12px;width:280px;'>" +
                        "<h3>Xác nhận thanh toán</h3>" +
                        "<b>Hóa đơn:</b> HD " + (hoaDonIdx + 1) + "<br>" +
                        "<b>Khách hàng:</b> " + khTen + "<br>" +
                        "<b>Tổng tiền:</b> " + FMT.format(tong) + " VND<br>" +
                        "<b>Phương thức:</b> " + cboPhuongThuc.getSelectedItem() +
                        "</div></html>",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

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
                hd.setGhiChu(cboPhuongThuc.getSelectedItem() != null ? cboPhuongThuc.getSelectedItem().toString() : "");
                hd.setTongTien(finalTong);
                return hdDAO.taoHoaDon(hd, finalCtThanhToan);
            }

            @Override protected void done() {
                try {
                    int idHD = get();
                    if (idHD > 0) {
                        for (int i = hoaDon.gioHang.size() - 1; i >= 0; i--) {
                            Boolean isChecked = (Boolean) hoaDon.modelGio.getValueAt(i, 0);
                            if (Boolean.TRUE.equals(isChecked)) {
                                hoaDon.gioHang.remove(i);
                                hoaDon.modelGio.removeRow(i);
                            }
                        }

                        if (hoaDon.gioHang.isEmpty()) {
                            hoaDon.khachHang = null;
                            hoaDon.lblKhachHang.setText("Khách lẻ");
                            hoaDon.lblKhachHang.setForeground(TEXT_DARK);
                        }

                        capNhatTongTien(hoaDonIdx);
                        loadSanPham("");
                        showMsg("<html>Thanh toán thành công!<br>Mã hóa đơn: HD-" + idHD + "</html>", "Thành công", true);
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
        JOptionPane.showMessageDialog(
                this,
                msg,
                title,
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
        );
    }

    // ════════════════════════════════════════════════════════════
    // UI HELPERS
    // ════════════════════════════════════════════════════════════
    private JPanel createCardPanel(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(top, left, bottom, right)
        ));
        return p;
    }

    private void styleTable(JTable table) {
        table.setFont(FONT_13);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(SELECT_BG);
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(WHITE);
        table.setForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_12);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_MID);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer objectRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setForeground(TEXT_DARK);
                if (!sel) setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                if (col == 3) setHorizontalAlignment(SwingConstants.CENTER);
                else if (col == 4 || col == 2) setHorizontalAlignment(SwingConstants.RIGHT);
                else setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, objectRenderer);

        table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(TEXT_DARK);
                if (!sel) setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                return this;
            }
        });

        table.setDefaultRenderer(Boolean.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                JCheckBox cb = new JCheckBox();
                cb.setSelected(Boolean.TRUE.equals(val));
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setOpaque(true);
                cb.setBackground(sel ? SELECT_BG : (row % 2 == 0 ? WHITE : ROW_ALT));
                return cb;
            }
        });
    }

    private JButton buildBtn(String text, Color fill, Color hover, int width, int height) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(BOLD_13);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 12, 0, 12));
        if (width > 0 || height > 0) {
            btn.setPreferredSize(new Dimension(width > 0 ? width : 120, height > 0 ? height : 40));
        }
        return btn;
    }

    private void setColumnWidth(JTable table, int col, int width) {
        TableColumn column = table.getColumnModel().getColumn(col);
        column.setPreferredWidth(width);
        if (col == 0 || col == 3 || col == 5) {
            column.setMaxWidth(width + 20);
        }
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        private final Color color;

        ButtonRenderer(String text, Color color) {
            this.color = color;
            setText(text);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(color);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            setForeground(color);
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private final JButton btn;
        private final Runnable action;

        ButtonEditor(JCheckBox cb, Runnable action) {
            super(cb);
            this.action = action;
            btn = new JButton("X");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        @Override public Object getCellEditorValue() {
            return "X";
        }
    }
}
