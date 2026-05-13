package com.quanlybanhang.ui.panels;

import com.quanlybanhang.service.StatisticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private StatisticsService statsService = new StatisticsService();

    // Màu sắc
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(249, 115, 22);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color LABEL_COLOR = new Color(71, 85, 105);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_DARK = new Color(15, 23, 42);

    private JComboBox<String> cbFilter;
    private JComboBox<Integer> cbYear, cbMonth;
    private JButton btnApply;
    private JLabel lblStartDate, lblEndDate;
    private JSpinner spnStartDate, spnEndDate;

    private JLabel lblInvoiceCount, lblRevenue, lblProfit, lblProfitMargin;
    private JTable tblTopProducts, tblInventory;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadDashboard();
    }

    private JPanel buildFilterPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        // Filter type
        JLabel lbl1 = new JLabel("Thống kê theo:");
        lbl1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(lbl1);

        cbFilter = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm", "Khoảng ngày"});
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFilter.setPreferredSize(new Dimension(120, 30));
        cbFilter.addActionListener(e -> updateFilterUI());
        p.add(cbFilter);

        // Day selector
        JLabel lblDay = new JLabel("Ngày:");
        lblDay.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDay.setVisible(true);
        p.add(lblDay);

        SpinnerDateModel dayModel = new SpinnerDateModel();
        dayModel.setValue(java.sql.Date.valueOf(LocalDate.now()));
        JSpinner spnDay = new JSpinner(dayModel);
        JSpinner.DateEditor deDay = new JSpinner.DateEditor(spnDay, "dd/MM/yyyy");
        spnDay.setEditor(deDay);
        spnDay.setPreferredSize(new Dimension(110, 30));
        spnDay.setVisible(true);
        p.add(spnDay);

        // Year
        JLabel lbl2 = new JLabel("Năm:");
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(lbl2);

        Integer[] years = new Integer[5];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) years[i] = currentYear - i;
        cbYear = new JComboBox<>(years);
        cbYear.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbYear.setPreferredSize(new Dimension(80, 30));
        p.add(cbYear);

        // Month
        JLabel lbl3 = new JLabel("Tháng:");
        lbl3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(lbl3);

        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) months[i] = i + 1;
        cbMonth = new JComboBox<>(months);
        cbMonth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbMonth.setPreferredSize(new Dimension(70, 30));
        cbMonth.setSelectedItem(LocalDate.now().getMonthValue());
        p.add(cbMonth);

        // Date range
        lblStartDate = new JLabel("Từ ngày:");
        lblStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStartDate.setVisible(false);
        p.add(lblStartDate);

        SpinnerDateModel startModel = new SpinnerDateModel();
        startModel.setValue(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        spnStartDate = new JSpinner(startModel);
        JSpinner.DateEditor deStart = new JSpinner.DateEditor(spnStartDate, "dd/MM/yyyy");
        spnStartDate.setEditor(deStart);
        spnStartDate.setPreferredSize(new Dimension(110, 30));
        spnStartDate.setVisible(false);
        p.add(spnStartDate);

        lblEndDate = new JLabel("Đến ngày:");
        lblEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEndDate.setVisible(false);
        p.add(lblEndDate);

        SpinnerDateModel endModel = new SpinnerDateModel();
        endModel.setValue(java.sql.Date.valueOf(LocalDate.now()));
        spnEndDate = new JSpinner(endModel);
        JSpinner.DateEditor deEnd = new JSpinner.DateEditor(spnEndDate, "dd/MM/yyyy");
        spnEndDate.setEditor(deEnd);
        spnEndDate.setPreferredSize(new Dimension(110, 30));
        spnEndDate.setVisible(false);
        p.add(spnEndDate);

        // Apply button
        btnApply = createStyledButton("Cập nhật", ACCENT);
        btnApply.setPreferredSize(new Dimension(120, 30));
        btnApply.addActionListener(e -> loadDashboard());
        p.add(btnApply);

        // Lưu spnDay vào biến instance
        this.spnDay = spnDay;
        this.lblDay = lblDay;

        return p;
    }

    private JSpinner spnDay;
    private JLabel lblDay;

    private void updateFilterUI() {
        String selected = (String) cbFilter.getSelectedItem();

        // Ẩn tất cả trước
        lblDay.setVisible(false);
        spnDay.setVisible(false);
        cbYear.setVisible(false);
        cbMonth.setVisible(false);
        lblStartDate.setVisible(false);
        spnStartDate.setVisible(false);
        lblEndDate.setVisible(false);
        spnEndDate.setVisible(false);

        // Hiển thị theo loại thống kê
        if ("Ngày".equals(selected)) {
            lblDay.setVisible(true);
            spnDay.setVisible(true);
        } else if ("Tháng".equals(selected)) {
            cbYear.setVisible(true);
            cbMonth.setVisible(true);
        } else if ("Năm".equals(selected)) {
            cbYear.setVisible(true);
        } else if ("Khoảng ngày".equals(selected)) {
            lblStartDate.setVisible(true);
            spnStartDate.setVisible(true);
            lblEndDate.setVisible(true);
            spnEndDate.setVisible(true);
        }
    }

    private JPanel buildMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        p.add(buildStatsPanel(), BorderLayout.NORTH);
        p.add(buildTablesPanel(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBackground(BG_COLOR);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_COLOR);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildStatsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);

        p.add(createStatCard("■ Số hóa đơn", lblInvoiceCount = createValueLabel("0"), SUCCESS));
        p.add(createStatCard("● Doanh thu", lblRevenue = createValueLabel("₫0"), ACCENT));
        p.add(createStatCard("▲ Lãi suất", lblProfit = createValueLabel("₫0"), WARNING));
        p.add(createStatCard("% Biên lợi", lblProfitMargin = createValueLabel("0%"), DANGER));

        return p;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }

    private JPanel buildTablesPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);

        p.add(buildTablePanel("Sản phẩm bán chạy nhất", buildTopProductsTable()));
        p.add(buildTablePanel("Sản phẩm tồn kho", buildInventoryTable()));

        return p;
    }

    private JPanel buildTablePanel(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(TEXT_DARK);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setBackground(CARD_BG);
        scroll.getViewport().setBackground(CARD_BG);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JTable buildTopProductsTable() {
        tblTopProducts = new JTable(new DefaultTableModel(
            new String[]{"Tên sản phẩm", "Mã", "Số lượng bán", "Giá nhập", "Giá bán", "Doanh thu"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tblTopProducts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tblTopProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblTopProducts.setRowHeight(28);
        return tblTopProducts;
    }

    private JTable buildInventoryTable() {
        tblInventory = new JTable(new DefaultTableModel(
            new String[]{"Tên sản phẩm", "Mã", "Số lượng tồn", "Giá nhập", "Giá bán"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tblInventory.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tblInventory.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblInventory.setRowHeight(28);
        return tblInventory;
    }

    private void loadDashboard() {
        String filterType = (String) cbFilter.getSelectedItem();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        if ("Ngày".equals(filterType)) {
            // Lấy ngày từ spnDay
            java.util.Date selected = (java.util.Date) spnDay.getValue();
            startDate = new java.sql.Date(selected.getTime()).toLocalDate();
            endDate = startDate;
        } else if ("Tháng".equals(filterType)) {
            // Lấy tháng và năm từ combobox
            int year = (Integer) cbYear.getSelectedItem();
            int month = (Integer) cbMonth.getSelectedItem();
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } else if ("Năm".equals(filterType)) {
            // Lấy năm từ combobox
            int year = (Integer) cbYear.getSelectedItem();
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        } else if ("Khoảng ngày".equals(filterType)) {
            // Lấy khoảng ngày từ spinner
            java.util.Date sd = (java.util.Date) spnStartDate.getValue();
            java.util.Date ed = (java.util.Date) spnEndDate.getValue();
            startDate = new java.sql.Date(sd.getTime()).toLocalDate();
            endDate = new java.sql.Date(ed.getTime()).toLocalDate();
        }

        updateStatistics(startDate, endDate);
        updateTopProducts(startDate, endDate);
        updateInventory();
    }

    private void updateStatistics(LocalDate startDate, LocalDate endDate) {
        long invoiceCount = statsService.getInvoiceCountByDateRange(startDate, endDate);
        Map<String, BigDecimal> revenueMap = statsService.getRevenueByDateRange(startDate, endDate);
        BigDecimal revenue = revenueMap.getOrDefault("doanhThu", BigDecimal.ZERO);
        BigDecimal profitMargin = statsService.getProfitMarginByDateRange(startDate, endDate);

        // Tính lãi (Profit)
        BigDecimal profit = revenue.multiply(profitMargin).divide(new BigDecimal("100"), 0, java.math.RoundingMode.HALF_UP);

        lblInvoiceCount.setText(String.valueOf(invoiceCount));
        lblRevenue.setText(formatCurrency(revenue));
        lblProfit.setText(formatCurrency(profit));
        lblProfitMargin.setText(profitMargin.setScale(2, java.math.RoundingMode.HALF_UP) + "%");
    }

    private void updateTopProducts(LocalDate startDate, LocalDate endDate) {
        DefaultTableModel model = (DefaultTableModel) tblTopProducts.getModel();
        model.setRowCount(0);

        List<Map<String, Object>> products = statsService.getTopSellingProducts(startDate, endDate, 10);
        for (Map<String, Object> p : products) {
            model.addRow(new Object[]{
                p.get("ten"),
                p.get("ma"),
                p.get("soLuongBan"),
                formatCurrency((BigDecimal) p.get("giaNhap")),
                formatCurrency((BigDecimal) p.get("giaBan")),
                formatCurrency((BigDecimal) p.get("doanhThu"))
            });
        }
    }

    private void updateInventory() {
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
        model.setRowCount(0);

        List<Map<String, Object>> products = statsService.getProductsInStock(10);
        for (Map<String, Object> p : products) {
            model.addRow(new Object[]{
                p.get("ten"),
                p.get("ma"),
                p.get("soLuongTon"),
                formatCurrency((BigDecimal) p.get("giaNhap")),
                formatCurrency((BigDecimal) p.get("giaBan"))
            });
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        long value = amount.longValue();
        return String.format("%,d ₫", value);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                Color fillColor = getModel().isRollover() ?
                    new Color(bg.getRed() - 15, bg.getGreen() - 15, bg.getBlue() - 15) : bg;
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
