package com.quanlybanhang.ui.panels;

import com.quanlybanhang.dao.SanPhamDAO;
import com.quanlybanhang.entity.ChitietSP;
import com.quanlybanhang.ui.dialogs.SanPhamDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;

public class SanPhamPanel extends JPanel {

    // ── Màu sắc ────────────────────────────────────────────
    private static final Color BG         = new Color(248, 250, 252);
    private static final Color WHITE      = Color.WHITE;
    private static final Color ACCENT     = new Color(99, 102, 241);   // indigo
    private static final Color ACCENT_H   = new Color(79, 70, 229);
    private static final Color AMBER      = new Color(245, 158, 11);   // vàng cam
    private static final Color AMBER_H    = new Color(217, 119, 6);
    private static final Color DANGER     = new Color(239, 68, 68);    // đỏ
    private static final Color DANGER_H   = new Color(220, 38, 38);
    private static final Color BORDER_C   = new Color(226, 232, 240);
    private static final Color TEXT_DARK  = new Color(15, 23, 42);
    private static final Color TEXT_MID   = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color ROW_ALT    = new Color(249, 250, 251);
    private static final Color ROW_SEL    = new Color(238, 242, 255);
    private static final Color HDR_BG     = new Color(241, 245, 249);

    private final SanPhamDAO dao = new SanPhamDAO();

    private JTable            tblSanPham;
    private DefaultTableModel tableModel;
    private JTextField        txtTimKiem;
    private JButton           btnThem, btnSua, btnXoa;
    private JLabel            lblCount;

    public SanPhamPanel() {
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

    // ── Thanh trên: tiêu đề + tìm kiếm + nút thêm ────────
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
        JLabel title = lbl("Quản lý sản phẩm", new Font("Segoe UI", Font.BOLD, 18), TEXT_DARK);
        lblCount = lbl("Đang tải...", new Font("Segoe UI", Font.PLAIN, 12), TEXT_LIGHT);
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
        searchBox.setPreferredSize(new Dimension(320, 38));

        // Icon kính lúp (vẽ tay)
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

        // Phải: nút thêm
        btnThem = buildFilledBtn("+ Thêm sản phẩm", ACCENT, ACCENT_H);
        btnThem.setPreferredSize(new Dimension(160, 38));
        btnThem.addActionListener(e -> addProduct());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(btnThem);

        p.add(left,      BorderLayout.WEST);
        p.add(searchBox, BorderLayout.CENTER);
        p.add(right,     BorderLayout.EAST);
        return p;
    }

    // ── Nội dung: bảng + thanh nút ───────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Bảng
        buildTable();
        JScrollPane scroll = new JScrollPane(tblSanPham);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        p.add(scroll, BorderLayout.CENTER);

        // Thanh nút dưới
        p.add(buildActionBar(), BorderLayout.SOUTH);
        return p;
    }

    private void buildTable() {
        String[] cols = {"ID", "Mã SP", "Tên sản phẩm", "Danh mục", "Màu sắc", "Kích cỡ", "Tồn kho", "Giá nhập", "Giá bán"};
        tableModel = new DefaultTableModel(new Object[0][0], cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblSanPham = new JTable(tableModel) {
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
        tblSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblSanPham.setRowHeight(38);
        tblSanPham.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSanPham.setShowGrid(false);
        tblSanPham.setIntercellSpacing(new Dimension(0, 0));

        // Header
        JTableHeader header = tblSanPham.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(HDR_BG);
        header.setForeground(TEXT_MID);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));

        // Căn phải cột số
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        int[] rightCols = {0, 6, 7, 8};
        for (int i : rightCols) tblSanPham.getColumnModel().getColumn(i).setCellRenderer(right);

        // Ẩn cột ID
        tblSanPham.getColumnModel().getColumn(0).setMinWidth(0);
        tblSanPham.getColumnModel().getColumn(0).setMaxWidth(0);

        // Độ rộng cột
        int[] widths = {0, 80, 200, 110, 90, 80, 70, 110, 110};
        for (int i = 0; i < widths.length; i++)
            if (widths[i] > 0) tblSanPham.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Badge tồn kho màu
        tblSanPham.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                                                                     boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    try {
                        int v = Integer.parseInt(val.toString());
                        l.setForeground(v == 0 ? DANGER : v < 5 ? AMBER : new Color(22, 163, 74));
                        l.setFont(getFont().deriveFont(Font.BOLD));
                    } catch (Exception ignored) {}
                }
                return l;
            }
        });

        // Double-click mở sửa
        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editProduct();
            }
        });
    }

    private JPanel buildActionBar() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(BG);

        // Thông tin chọn
        JLabel hint = lbl("Double-click để sửa | Chọn hàng rồi nhấn Sửa / Xóa",
                new Font("Segoe UI", Font.ITALIC, 11), TEXT_LIGHT);

        // Nút Sửa + Xoá
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnSua = buildFilledBtn("Sửa sản phẩm", AMBER, AMBER_H);
        btnSua.setPreferredSize(new Dimension(140, 38));
        btnSua.addActionListener(e -> editProduct());

        btnXoa = buildFilledBtn("Xóa sản phẩm", DANGER, DANGER_H);
        btnXoa.setPreferredSize(new Dimension(140, 38));
        btnXoa.addActionListener(e -> deleteProduct());

        btnPanel.add(btnSua);
        btnPanel.add(btnXoa);

        p.add(hint,     BorderLayout.WEST);
        p.add(btnPanel, BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════
    // ACTIONS
    // ══════════════════════════════════════════════════════
    private void loadData() {
        SwingWorker<List<ChitietSP>, Void> w = new SwingWorker<>() {
            @Override protected List<ChitietSP> doInBackground() { return dao.getAll(); }
            @Override protected void done() {
                try { fillTable(get()); } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void search() {
        String kw = txtTimKiem.getText().trim();
        SwingWorker<List<ChitietSP>, Void> w = new SwingWorker<>() {
            @Override protected List<ChitietSP> doInBackground() {
                return kw.isEmpty() ? dao.getAll() : dao.search(kw);
            }
            @Override protected void done() {
                try { fillTable(get()); } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void fillTable(List<ChitietSP> list) {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,###");
        for (ChitietSP sp : list) {
            tableModel.addRow(new Object[]{
                    sp.getId(),
                    sp.getMa(),
                    sp.getTen(),
                    sp.getTenDanhMuc() != null ? sp.getTenDanhMuc() : "—",
                    sp.getTenMauSac()  != null ? sp.getTenMauSac()  : "—",
                    sp.getTenKichCo()  != null ? sp.getTenKichCo()  : "—",
                    sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0,
                    sp.getGiaNhap()    != null ? df.format(sp.getGiaNhap()) : "0",
                    sp.getGiaBan()     != null ? df.format(sp.getGiaBan())  : "0"
            });
        }
        lblCount.setText("Tổng: " + list.size() + " sản phẩm");
    }

    private void addProduct() {
        SanPhamDialog dlg = new SanPhamDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), null, dao);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) loadData();
    }

    private void editProduct() {
        int row = tblSanPham.getSelectedRow();
        if (row < 0) {
            showWarn("Vui lòng chọn sản phẩm để sửa!");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        ChitietSP sp = dao.getById(id);
        SanPhamDialog dlg = new SanPhamDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), sp, dao);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) loadData();
    }

    private void deleteProduct() {
        int row = tblSanPham.getSelectedRow();
        if (row < 0) { showWarn("Vui lòng chọn sản phẩm để xóa!"); return; }
        String ten = tableModel.getValueAt(row, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn xóa sản phẩm:\n\"" + ten + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = (int) tableModel.getValueAt(row, 0);
        if (dao.delete(id)) {
            JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại! Sản phẩm có thể đang được dùng trong hóa đơn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════
    private JButton buildFilledBtn(String text, Color fill, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow nhẹ
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(1, 3, getWidth()-2, getHeight()-2, 8, 8);
                // Nền nút
                g2.setColor(!isEnabled() ? TEXT_LIGHT : getModel().isPressed() ? hover.darker() : getModel().isRollover() ? hover : fill);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-3, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}