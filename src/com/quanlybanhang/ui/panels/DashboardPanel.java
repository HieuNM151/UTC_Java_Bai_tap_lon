package com.quanlybanhang.ui.panels;

import com.quanlybanhang.entity.User;
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
import java.util.Objects;

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
    private JLabel lblYear, lblMonth;
    private JSpinner spnStartDate, spnEndDate;

    private JLabel lblInvoiceCount, lblRevenue, lblProfit, lblProfitMargin;
    private JTable tblTopProducts, tblInventory;

    /** Nhân viên: không hiển thị bộ lọc / số liệu / bảng thống kê. */
    public DashboardPanel(User currentUser) {
        Objects.requireNonNull(currentUser, "currentUser");
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        if (!currentUser.isAdmin()) {
            add(buildStaffSummaryPanel(currentUser), BorderLayout.CENTER);
            return;
        }

        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadDashboard();
    }

    private JPanel buildStaffSummaryPanel(User u) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        JLabel msg = new JLabel("<html><div style='width:420px;text-align:center'>"
                + "<h2 style='color:#0f172a;margin:0'>Tổng quan</h2>"
                + "<p style='color:#64748b;font-size:13px;margin-top:12px'>"
                + "Báo cáo và thống kê chỉ dành cho quản trị viên.<br>"
                + "Vui lòng dùng mục <b>Bán hàng</b> để phục vụ khách."
                + "</p></div></html>");
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        wrap.add(msg, c);
        return wrap;
    }

    private JPanel buildFilterPanel() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(12, 16, 12, 16)));

        FlowLayout fl = new FlowLayout(FlowLayout.LEFT, 10, 8);
        fl.setAlignOnBaseline(true);
        JPanel row = new JPanel(fl);
        row.setOpaque(false);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        int ctrlH = 30;

        JLabel lbl1 = new JLabel("Thống kê theo:");
        lbl1.setFont(labelFont);
        lbl1.setForeground(LABEL_COLOR);
        row.add(lbl1);

        cbFilter = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm", "Khoảng ngày"});
        cbFilter.setFont(labelFont);
        cbFilter.setPreferredSize(new Dimension(128, ctrlH));
        cbFilter.addActionListener(e -> updateFilterUI());
        row.add(cbFilter);

        lblDay = new JLabel("Ngày:");
        lblDay.setFont(labelFont);
        lblDay.setForeground(LABEL_COLOR);
        row.add(lblDay);

        SpinnerDateModel dayModel = new SpinnerDateModel();
        dayModel.setValue(java.sql.Date.valueOf(LocalDate.now()));
        JSpinner spnDay = new JSpinner(dayModel);
        JSpinner.DateEditor deDay = new JSpinner.DateEditor(spnDay, "dd/MM/yyyy");
        spnDay.setEditor(deDay);
        spnDay.setPreferredSize(new Dimension(118, ctrlH));
        row.add(spnDay);

        lblYear = new JLabel("Năm:");
        lblYear.setFont(labelFont);
        lblYear.setForeground(LABEL_COLOR);
        row.add(lblYear);

        Integer[] years = new Integer[5];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) years[i] = currentYear - i;
        cbYear = new JComboBox<>(years);
        cbYear.setFont(labelFont);
        cbYear.setPreferredSize(new Dimension(84, ctrlH));
        row.add(cbYear);

        lblMonth = new JLabel("Tháng:");
        lblMonth.setFont(labelFont);
        lblMonth.setForeground(LABEL_COLOR);
        row.add(lblMonth);

        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) months[i] = i + 1;
        cbMonth = new JComboBox<>(months);
        cbMonth.setFont(labelFont);
        cbMonth.setPreferredSize(new Dimension(72, ctrlH));
        cbMonth.setSelectedItem(LocalDate.now().getMonthValue());
        row.add(cbMonth);

        lblStartDate = new JLabel("Từ ngày:");
        lblStartDate.setFont(labelFont);
        lblStartDate.setForeground(LABEL_COLOR);
        lblStartDate.setVisible(false);
        row.add(lblStartDate);

        SpinnerDateModel startModel = new SpinnerDateModel();
        startModel.setValue(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        spnStartDate = new JSpinner(startModel);
        JSpinner.DateEditor deStart = new JSpinner.DateEditor(spnStartDate, "dd/MM/yyyy");
        spnStartDate.setEditor(deStart);
        spnStartDate.setPreferredSize(new Dimension(118, ctrlH));
        spnStartDate.setVisible(false);
        row.add(spnStartDate);

        lblEndDate = new JLabel("Đến ngày:");
        lblEndDate.setFont(labelFont);
        lblEndDate.setForeground(LABEL_COLOR);
        lblEndDate.setVisible(false);
        row.add(lblEndDate);

        SpinnerDateModel endModel = new SpinnerDateModel();
        endModel.setValue(java.sql.Date.valueOf(LocalDate.now()));
        spnEndDate = new JSpinner(endModel);
        JSpinner.DateEditor deEnd = new JSpinner.DateEditor(spnEndDate, "dd/MM/yyyy");
        spnEndDate.setEditor(deEnd);
        spnEndDate.setPreferredSize(new Dimension(118, ctrlH));
        spnEndDate.setVisible(false);
        row.add(spnEndDate);

        btnApply = createStyledButton("Cập nhật", ACCENT);
        btnApply.setPreferredSize(new Dimension(112, ctrlH));
        btnApply.addActionListener(e -> loadDashboard());

        p.add(row, BorderLayout.CENTER);
        p.add(btnApply, BorderLayout.EAST);

        this.spnDay = spnDay;
        updateFilterUI();
        return p;
    }

    private JSpinner spnDay;
    private JLabel lblDay;

    private void updateFilterUI() {
        String selected = (String) cbFilter.getSelectedItem();

        lblDay.setVisible(false);
        spnDay.setVisible(false);
        lblYear.setVisible(false);
        cbYear.setVisible(false);
        lblMonth.setVisible(false);
        cbMonth.setVisible(false);
        lblStartDate.setVisible(false);
        spnStartDate.setVisible(false);
        lblEndDate.setVisible(false);
        spnEndDate.setVisible(false);

        if ("Ngày".equals(selected)) {
            lblDay.setVisible(true);
            spnDay.setVisible(true);
        } else if ("Tháng".equals(selected)) {
            lblYear.setVisible(true);
            cbYear.setVisible(true);
            lblMonth.setVisible(true);
            cbMonth.setVisible(true);
        } else if ("Năm".equals(selected)) {
            lblYear.setVisible(true);
            cbYear.setVisible(true);
        } else if ("Khoảng ngày".equals(selected)) {
            lblStartDate.setVisible(true);
            spnStartDate.setVisible(true);
            lblEndDate.setVisible(true);
            spnEndDate.setVisible(true);
        }
        revalidate();
        repaint();
    }

    private JPanel buildMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        p.add(buildStatsPanel(), BorderLayout.NORTH);
        p.add(buildTablesPanel(), BorderLayout.CENTER);

        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildStatsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 12);
        c.weightx = 1;

        c.gridx = 0;
        p.add(createStatCard("Số hóa đơn", lblInvoiceCount = createValueLabel("0"), SUCCESS), c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 12);
        p.add(createStatCard("Doanh thu", lblRevenue = createValueLabel("₫0"), ACCENT), c);
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 12);
        p.add(createStatCard("Lãi suất", lblProfit = createValueLabel("₫0"), WARNING), c);
        c.gridx = 3;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(createStatCard("% Biên lợi", lblProfitMargin = createValueLabel("0%"), DANGER), c);

        return p;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
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
        card.setMinimumSize(new Dimension(0, 108));

        JPanel top = new JPanel(new BorderLayout(0, 0));
        top.setOpaque(false);

        JPanel swatch = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(10, 10);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        swatch.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(color);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(swatch);
        titleRow.add(lblTitle);

        top.add(titleRow, BorderLayout.WEST);
        card.add(top, BorderLayout.NORTH);
        valueLabel.setVerticalAlignment(SwingConstants.TOP);
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
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 12);
        c.gridx = 0;
        p.add(buildTablePanel("Sản phẩm bán chạy nhất", buildTopProductsTable()), c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(buildTablePanel("Sản phẩm tồn kho", buildInventoryTable()), c);

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
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

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
        tblTopProducts.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
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
        tblInventory.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
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
