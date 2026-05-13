package com.quanlybanhang.ui.panels;

import com.quanlybanhang.dao.HoaDonDAO;
import com.quanlybanhang.entity.HoaDon;
import com.quanlybanhang.ui.dialogs.HoaDonDetailDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class HoaDonPanel extends JPanel {

    // ── Màu sắc ────────────────────────────────────────────
    private static final Color BG         = new Color(248, 250, 252);
    private static final Color WHITE      = Color.WHITE;
    private static final Color ACCENT     = new Color(99, 102, 241);   // indigo
    private static final Color ACCENT_H   = new Color(79, 70, 229);
    private static final Color BORDER_C   = new Color(226, 232, 240);
    private static final Color TEXT_DARK  = new Color(15, 23, 42);
    private static final Color TEXT_MID   = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color ROW_ALT    = new Color(249, 250, 251);
    private static final Color ROW_SEL    = new Color(238, 242, 255);
    private static final Color HDR_BG     = new Color(241, 245, 249);
    private static final Color C_GREEN    = new Color(34, 197, 94);
    private static final Color C_ORANGE   = new Color(249, 115, 22);

    private final HoaDonDAO dao = new HoaDonDAO();
    private final DecimalFormat df = new DecimalFormat("#,##0");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private JTable tblHoaDon;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JLabel lblCount;

    public HoaDonPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initUI();
        loadData();
    }

    // ══════════════════════════════════════════════════════
    // BUILD UI
    // ══════════════════════════════════════════════════════
    private void initUI() {
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    // ── Thanh trên: tiêu đề + tìm kiếm ────────────────────
    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(16, 20, 16, 20)));

        // Trái: tiêu đề
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Quản lý hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        lblCount = new JLabel("Đang tải...");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(TEXT_LIGHT);
        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(lblCount);

        // Giữa: ô tìm kiếm
        JPanel searchBox = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(BORDER_C);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        searchBox.setOpaque(false);
        searchBox.setPreferredSize(new Dimension(400, 38));

        // Icon kính lúp
        JPanel iconLens = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_LIGHT);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(8, 7, 14, 14);
                g2.drawLine(20, 19, 26, 25);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36, 38); }
        };
        iconLens.setOpaque(false);

        txtTimKiem = new JTextField();
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimKiem.setForeground(TEXT_DARK);
        txtTimKiem.setBorder(new EmptyBorder(0, 4, 0, 8));
        txtTimKiem.setOpaque(false);
        txtTimKiem.setToolTipText("Tìm theo mã hoặc tên khách hàng...");
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) search();
                else if (txtTimKiem.getText().isEmpty()) loadData();
            }
        });

        JButton btnSearch = new JButton("Tìm") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_H : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setForeground(WHITE);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(56, 38));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> search());

        searchBox.add(iconLens,   BorderLayout.WEST);
        searchBox.add(txtTimKiem, BorderLayout.CENTER);
        searchBox.add(btnSearch,  BorderLayout.EAST);

        p.add(left,      BorderLayout.WEST);
        p.add(searchBox, BorderLayout.CENTER);
        return p;
    }

    // ── Nội dung: bảng ──────────────────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Bảng
        buildTable();
        JScrollPane scroll = new JScrollPane(tblHoaDon);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        p.add(scroll, BorderLayout.CENTER);

        // Thông tin dưới
        p.add(buildInfoBar(), BorderLayout.SOUTH);
        return p;
    }

    private void buildTable() {
        String[] cols = {"ID", "Mã HD", "Ngày tạo", "Khách hàng", "Nhân viên", "Trạng thái", "Tổng tiền"};
        tableModel = new DefaultTableModel(new Object[0][0], cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblHoaDon = new JTable(tableModel) {
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
        tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblHoaDon.setRowHeight(38);
        tblHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHoaDon.setShowGrid(false);
        tblHoaDon.setIntercellSpacing(new Dimension(0, 0));

        // Header
        JTableHeader header = tblHoaDon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(HDR_BG);
        header.setForeground(TEXT_MID);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));

        // Căn phải cột số
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblHoaDon.getColumnModel().getColumn(6).setCellRenderer(right);

        // Ẩn cột ID
        tblHoaDon.getColumnModel().getColumn(0).setMinWidth(0);
        tblHoaDon.getColumnModel().getColumn(0).setMaxWidth(0);

        // Độ rộng cột
        int[] widths = {0, 100, 150, 150, 120, 120, 130};
        for (int i = 0; i < widths.length; i++)
            if (widths[i] > 0) tblHoaDon.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Badge trạng thái
        tblHoaDon.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                                                                     boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    String text = val != null ? val.toString() : "Chưa thanh toán";
                    if (text.contains("Đã")) {
                        l.setForeground(C_GREEN);
                        l.setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        l.setForeground(C_ORANGE);
                        l.setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return l;
            }
        });

        // Định dạng tiền tệ
        tblHoaDon.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                                                                     boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                return l;
            }
        });

        // Click để xem chi tiết
        tblHoaDon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tblHoaDon.getSelectedRow();
                if (row >= 0 && e.getClickCount() >= 1) {
                    viewDetail(row);
                }
            }
        });
    }

    private JPanel buildInfoBar() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(BG);
        JLabel hint = new JLabel("💡 Click vào hàng để xem chi tiết hóa đơn");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_LIGHT);
        p.add(hint, BorderLayout.WEST);
        return p;
    }

    // ══════════════════════════════════════════════════════
    // ACTIONS
    // ══════════════════════════════════════════════════════
    private void loadData() {
        SwingWorker<List<HoaDon>, Void> w = new SwingWorker<>() {
            @Override protected List<HoaDon> doInBackground() {
                return dao.getAll();
            }

            @Override protected void done() {
                try {
                    fillTable(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void search() {
        String kw = txtTimKiem.getText().trim();
        SwingWorker<List<HoaDon>, Void> w = new SwingWorker<>() {
            @Override protected List<HoaDon> doInBackground() {
                return kw.isEmpty() ? dao.getAll() : dao.search(kw);
            }

            @Override protected void done() {
                try {
                    fillTable(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void fillTable(List<HoaDon> list) {
        tableModel.setRowCount(0);
        for (HoaDon hd : list) {
            String tinhTrang = hd.getTinhTrang() != null && hd.getTinhTrang() ? "✓ Đã thanh toán" : "◇ Chưa thanh toán";
            String ngayTao = hd.getNgayTao() != null ? sdf.format(java.sql.Timestamp.valueOf(hd.getNgayTao())) : "N/A";
            String tongTien = hd.getTongTien() != null ? df.format(hd.getTongTien()) + " ₫" : "0 ₫";

            tableModel.addRow(new Object[]{
                    hd.getId(),
                    hd.getMa(),
                    ngayTao,
                    hd.getTenKhachHang() != null ? hd.getTenKhachHang() : "—",
                    hd.getTenNhanVien() != null ? hd.getTenNhanVien() : "—",
                    tinhTrang,
                    tongTien
            });
        }

        lblCount.setText("Tổng: " + list.size() + " hóa đơn");
    }

    private void viewDetail(int row) {
        int idHD = (int) tableModel.getValueAt(row, 0);
        HoaDon hd = dao.getById(idHD);
        if (hd != null) {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            HoaDonDetailDialog dialog = new HoaDonDetailDialog(parent, hd);
            dialog.setVisible(true);
        }
    }
}
