package com.quanlydatvemaybay.ui.dialogs;

import com.quanlydatvemaybay.dao.HoaDonChiTietDAO;
import com.quanlydatvemaybay.entity.HoaDon;
import com.quanlydatvemaybay.entity.HoaDonChiTiet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class HoaDonDetailDialog extends JDialog {

    // ── Màu sắc ────────────────────────────────────────────
    private static final Color BG         = new Color(248, 250, 252);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BORDER_C   = new Color(226, 232, 240);
    private static final Color TEXT_DARK  = new Color(15, 23, 42);
    private static final Color TEXT_MID   = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color ROW_ALT    = new Color(249, 250, 251);
    private static final Color ROW_SEL    = new Color(238, 242, 255);
    private static final Color HDR_BG     = new Color(241, 245, 249);
    private static final Color C_INDIGO   = new Color(99, 102, 241);
    private static final Color C_GREEN    = new Color(34, 197, 94);

    private final HoaDonChiTietDAO chiTietDAO = new HoaDonChiTietDAO();
    private final DecimalFormat df = new DecimalFormat("#,##0");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private JTable tblChiTiet;
    private DefaultTableModel tableModel;
    private JLabel lblMaHD, lblNgayTao, lblKhachHang, lblNhanVien;
    private JLabel lblTinhTrang, lblGhiChu, lblTongTien;

    public HoaDonDetailDialog(JFrame owner, HoaDon hoaDon) {
        super(owner, "Chi tiết hóa đơn #" + hoaDon.getMa(), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setResizable(true);

        initUI(hoaDon);
    }

    private void initUI(HoaDon hoaDon) {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Phần thông tin chung
        mainPanel.add(buildInfoPanel(hoaDon), BorderLayout.NORTH);

        // Phần bảng chi tiết
        mainPanel.add(buildDetailPanel(hoaDon), BorderLayout.CENTER);

        // Phần nút đóng
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel buildInfoPanel(HoaDon hoaDon) {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 4, 16, 12));
        p.setBackground(WHITE);

        // Hàng 1
        p.add(createInfoField("Mã hóa đơn", hoaDon.getMa()));
        p.add(createInfoField("Ngày tạo", hoaDon.getNgayTao() != null ? sdf.format(java.sql.Timestamp.valueOf(hoaDon.getNgayTao())) : "N/A"));
        p.add(createInfoField("Khách hàng", hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : "Không có"));
        p.add(createInfoField("Nhân viên", hoaDon.getTenNhanVien() != null ? hoaDon.getTenNhanVien() : "Không có"));

        // Hàng 2
        p.add(createInfoField("Tính trạng", hoaDon.getTinhTrang() != null && hoaDon.getTinhTrang() ? "✓ Đã thanh toán" : "◇ Chưa thanh toán"));
        p.add(createInfoField("Ghi chú", hoaDon.getGhiChu() != null ? hoaDon.getGhiChu() : "Không có"));

        String tongTienStr = hoaDon.getTongTien() != null ? df.format(hoaDon.getTongTien()) + " ₫" : "0 ₫";
        JPanel total = createInfoField("Tổng tiền", tongTienStr);
        p.add(total);

        return p;
    }

    private JPanel createInfoField(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(WHITE);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(TEXT_LIGHT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValue.setForeground(TEXT_DARK);

        p.add(lblLabel, BorderLayout.NORTH);
        p.add(lblValue, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildDetailPanel(HoaDon hoaDon) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(WHITE);

        // Tiêu đề
        JLabel title = new JLabel("Chi tiết sản phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_DARK);

        // Bảng
        buildTable();
        loadChiTiet(hoaDon.getId());

        JScrollPane scroll = new JScrollPane(tblChiTiet);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        p.add(title, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private void buildTable() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Màu sắc", "Kích cỡ", "Số lượng", "Đơn giá", "Thành tiền"};
        tableModel = new DefaultTableModel(new Object[0][0], cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblChiTiet = new JTable(tableModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SEL);
                    c.setForeground(TEXT_DARK);
                } else {
                    c.setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                    c.setForeground(TEXT_DARK);
                }
                return c;
            }
        };
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblChiTiet.setRowHeight(36);
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChiTiet.setShowGrid(false);
        tblChiTiet.setIntercellSpacing(new Dimension(0, 0));

        // Header
        JTableHeader header = tblChiTiet.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(HDR_BG);
        header.setForeground(TEXT_MID);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));

        // Căn phải cột số
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        int[] rightCols = {4, 5, 6};
        for (int i : rightCols) tblChiTiet.getColumnModel().getColumn(i).setCellRenderer(right);

        // Độ rộng cột
        int[] widths = {80, 200, 100, 80, 70, 120, 120};
        for (int i = 0; i < widths.length; i++) {
            tblChiTiet.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void loadChiTiet(int idHoaDon) {
        SwingWorker<List<HoaDonChiTiet>, Void> w = new SwingWorker<>() {
            @Override protected List<HoaDonChiTiet> doInBackground() {
                return chiTietDAO.getByHoaDonId(idHoaDon);
            }

            @Override protected void done() {
                try {
                    List<HoaDonChiTiet> chiTietList = get();
                    tableModel.setRowCount(0);
                    for (HoaDonChiTiet ct : chiTietList) {
                        tableModel.addRow(new Object[]{
                                ct.getMaSP(),
                                ct.getTenSP(),
                                ct.getTenMauSac() != null ? ct.getTenMauSac() : "—",
                                ct.getTenKichCo() != null ? ct.getTenKichCo() : "—",
                                ct.getSoLuong(),
                                df.format(ct.getDonGia()),
                                df.format(ct.getThanhTien())
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private JPanel buildButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setBackground(WHITE);

        JButton btnClose = new JButton("Đóng") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(99, 102, 241) : C_INDIGO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setForeground(WHITE);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        p.add(btnClose);
        return p;
    }
}
