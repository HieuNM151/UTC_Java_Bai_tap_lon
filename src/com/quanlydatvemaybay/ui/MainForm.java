package com.quanlydatvemaybay.ui;

import com.quanlydatvemaybay.entity.User;
import com.quanlydatvemaybay.ui.panels.SanPhamPanel;
import com.quanlydatvemaybay.ui.panels.HoaDonPanel;
import com.quanlydatvemaybay.ui.panels.KhachHangPanel;
import com.quanlydatvemaybay.ui.panels.DashboardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainForm extends JFrame {

    // ── Bảng màu sáng nhẹ nhàng ─────────────────────────────────
    private static final Color SIDEBAR_BG     = new Color(30, 41, 59);
    private static final Color MAIN_BG        = new Color(248, 250, 252);
    private static final Color TOPBAR_BG      = Color.WHITE;
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color BORDER_C       = new Color(226, 232, 240);
    private static final Color ACCENT         = new Color(99, 102, 241);
    private static final Color TEXT_DARK      = new Color(15, 23, 42);
    private static final Color TEXT_MID       = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT     = new Color(148, 163, 184);

    private static final Color C_INDIGO = new Color(99,  102, 241);
    private static final Color C_TEAL   = new Color(20,  184, 166);
    private static final Color C_AMBER  = new Color(245, 158, 11);
    private static final Color C_ROSE   = new Color(244, 63,  94);

    private final User currentUser;
    private JPanel contentPanel;
    private JLabel lblClock;
    private JLabel activeItem = null;

    private static final String[] MENU_VI = {
            "Tổng quan", "Bán hàng", "Sản phẩm",
            "Khách hàng", "Hóa đơn", "Khuyến mãi", "Báo cáo"
    };

    // Dùng ký tự ASCII đơn giản để tránh lỗi font
    private static final String[] MENU_PREFIX = {
            "■", "●", "▪", "○", "≡", "◆", "▶"
    };

    public MainForm(User user) {
        this.currentUser = user;
        setTitle("Quản Lý Bán Hàng — " + user.getHoTenDay());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        initUI();
    }

    // ══════════════════════════════════════════════════════
    // FONT HELPER — chọn font hỗ trợ tiếng Việt tốt nhất
    // ══════════════════════════════════════════════════════
    private static Font getViFont(int style, int size) {
        String[] candidates = {
                "Segoe UI",        // Windows — hỗ trợ tiếng Việt rất tốt
                "Arial Unicode MS",
                "Noto Sans",       // Linux
                "SansSerif"        // fallback luôn có
        };
        for (String name : candidates) {
            Font f = new Font(name, style, size);
            if (!f.getFamily().equals("Dialog")) return f;
        }
        return new Font("SansSerif", style, size);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);
        setContentPane(root);
        Timer clock = new Timer(1000, e -> updateClock());
        clock.start();
        updateClock();
    }

    // ══════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setPreferredSize(new Dimension(220, 0));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(buildLogo());
        p.add(sideDivider());

        for (int i = 0; i < MENU_VI.length; i++) {
            JLabel item = buildMenuItem(MENU_VI[i], MENU_PREFIX[i], i == 0);
            p.add(item);
            if (i == 0) activeItem = item;
        }

        p.add(Box.createVerticalGlue());
        p.add(sideDivider());
        p.add(buildUserCard());
        return p;
    }

    private JPanel buildLogo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 18));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(220, 68));

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, 38, 38, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getViFont(Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Q", (38 - fm.stringWidth("Q")) / 2, (38 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(38, 38); }
        };
        iconBox.setOpaque(false);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("Quản Lý");
        t1.setFont(getViFont(Font.BOLD, 14));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("Bán Hàng");
        t2.setFont(getViFont(Font.PLAIN, 11));
        t2.setForeground(TEXT_LIGHT);
        texts.add(t1);
        texts.add(t2);

        p.add(iconBox);
        p.add(texts);
        return p;
    }

    private JLabel buildMenuItem(String labelText, String prefix, boolean initActive) {
        JLabel lbl = new JLabel("  " + prefix + "  " + labelText) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (Boolean.TRUE.equals(getClientProperty("active"))) {
                    g2.setColor(new Color(99, 102, 241, 45));
                    g2.fillRoundRect(8, 3, getWidth() - 16, getHeight() - 6, 8, 8);
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 8, 3, getHeight() - 16, 3, 3);
                } else if (Boolean.TRUE.equals(getClientProperty("hover"))) {
                    g2.setColor(new Color(255, 255, 255, 10));
                    g2.fillRoundRect(8, 3, getWidth() - 16, getHeight() - 6, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        // ✅ Dùng getViFont() thay vì "Segoe UI Emoji"
        lbl.setFont(getViFont(initActive ? Font.BOLD : Font.PLAIN, 13));
        lbl.setForeground(initActive ? Color.WHITE : TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(11, 12, 11, 12));
        lbl.setMaximumSize(new Dimension(220, 44));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (initActive) lbl.putClientProperty("active", true);

        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(lbl.getClientProperty("active"))) {
                    lbl.putClientProperty("hover", true);
                    lbl.setForeground(Color.WHITE);
                    lbl.repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                lbl.putClientProperty("hover", false);
                if (!Boolean.TRUE.equals(lbl.getClientProperty("active")))
                    lbl.setForeground(TEXT_LIGHT);
                lbl.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (activeItem != null) {
                    activeItem.putClientProperty("active", false);
                    // ✅ Sửa: dùng getViFont() thay vì "Segoe UI Emoji"
                    activeItem.setFont(getViFont(Font.PLAIN, 13));
                    activeItem.setForeground(TEXT_LIGHT);
                    activeItem.repaint();
                }
                activeItem = lbl;
                lbl.putClientProperty("active", true);
                lbl.putClientProperty("hover", false);
                // ✅ Sửa: dùng getViFont() thay vì "Segoe UI Emoji"
                lbl.setFont(getViFont(Font.BOLD, 13));
                lbl.setForeground(Color.WHITE);
                lbl.repaint();
                switchPanel(labelText);
            }
        });
        return lbl;
    }

    private JPanel buildUserCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 14));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(220, 68));

        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillOval(0, 0, 36, 36);
                g2.setColor(Color.WHITE);
                g2.setFont(getViFont(Font.BOLD, 15));
                String s = String.valueOf(currentUser.getTen().charAt(0)).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, (36 - fm.stringWidth(s)) / 2, (36 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
        };
        avatar.setOpaque(false);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(currentUser.getTen());
        name.setFont(getViFont(Font.BOLD, 12));
        name.setForeground(Color.WHITE);
        JLabel role = new JLabel("Nhân viên");
        role.setFont(getViFont(Font.PLAIN, 11));
        role.setForeground(TEXT_LIGHT);
        info.add(name);
        info.add(role);

        JButton btnOut = new JButton("Đăng xuất");
        btnOut.setFont(getViFont(Font.BOLD, 11));
        btnOut.setForeground(new Color(248, 113, 113));
        btnOut.setContentAreaFilled(false);
        btnOut.setBorderPainted(false);
        btnOut.setFocusPainted(false);
        btnOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOut.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            }
        });

        p.add(avatar);
        p.add(info);
        p.add(btnOut);
        return p;
    }

    private JSeparator sideDivider() {
        JSeparator s = new JSeparator();
        s.setMaximumSize(new Dimension(220, 1));
        s.setForeground(new Color(51, 65, 85));
        return s;
    }

    // ══════════════════════════════════════════════════════
    // CONTENT
    // ══════════════════════════════════════════════════════
    private JPanel buildContent() {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(MAIN_BG);
        w.add(buildTopBar(), BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(MAIN_BG);
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPanel.add(new DashboardPanel(), BorderLayout.CENTER);

        w.add(contentPanel, BorderLayout.CENTER);
        return w;
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(TOPBAR_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(14, 28, 14, 28)));

        JLabel greeting = new JLabel("Xin chào, " + currentUser.getHoTenDay());
        greeting.setFont(getViFont(Font.BOLD, 15));
        greeting.setForeground(TEXT_DARK);

        lblClock = new JLabel();
        lblClock.setFont(getViFont(Font.PLAIN, 13));
        lblClock.setForeground(TEXT_MID);

        p.add(greeting, BorderLayout.WEST);
        p.add(lblClock, BorderLayout.EAST);
        return p;
    }

    private void updateClock() {
        lblClock.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss  —  dd/MM/yyyy")));
    }

    // ══════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel title = lbl("Tổng quan hệ thống", getViFont(Font.BOLD, 20), TEXT_DARK);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = lbl("Thống kê", getViFont(Font.PLAIN, 13), TEXT_MID);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(22));

        // 4 stat cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 128));
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.add(buildStatCard("Hóa đơn hôm nay",    "—", C_INDIGO, "HD"));
        cards.add(buildStatCard("Doanh thu",            "—", C_TEAL,   "DT"));
        cards.add(buildStatCard("Tiền lời",             "—", C_AMBER,  "TL"));
        cards.add(buildStatCard("Sản phẩm bán chạy",   "—", C_ROSE,   "SP"));
        p.add(cards);
        p.add(Box.createVerticalStrut(28));

        return p;
    }

    private JPanel buildStatCard(String labelText, String value, Color accent, String abbr) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                g2.setColor(new Color(0, 0, 0, 7));
                g2.fillRoundRect(3, 5, getWidth() - 3, getHeight() - 3, 14, 14);
                // body
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, 14, 14);
                // border
                g2.setColor(BORDER_C);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 7, 14, 14);
                // accent bar top
                g2.setColor(accent);
                g2.fillRect(1, 0, getWidth() - 6, 4);
                g2.fillRoundRect(1, 0, getWidth() - 6, 8, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 14, 18));
        card.setOpaque(false);

        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color soft = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 20);
                g2.setColor(soft);
                g2.fillRoundRect(0, 0, 40, 40, 10, 10);
                g2.setColor(accent);
                g2.setFont(getViFont(Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(abbr, (40 - fm.stringWidth(abbr)) / 2,
                        (40 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(40, 40); }
            @Override public Dimension getMaximumSize()   { return getPreferredSize(); }
        };
        badge.setOpaque(false);
        badge.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lVal = lbl(value, getViFont(Font.BOLD, 26), TEXT_DARK);
        lVal.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lLbl = lbl(labelText, getViFont(Font.PLAIN, 12), TEXT_MID);
        lLbl.setAlignmentX(LEFT_ALIGNMENT);

        card.add(badge);
        card.add(Box.createVerticalStrut(10));
        card.add(lVal);
        card.add(Box.createVerticalStrut(2));
        card.add(lLbl);
        return card;
    }

    private JButton buildActionBtn(String text, Color color) {
        Color softBg = new Color(color.getRed(), color.getGreen(), color.getBlue(), 18);
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(color);
                } else {
                    g2.setColor(softBg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!getModel().isRollover()) {
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(getViFont(Font.BOLD, 13));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(195, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(color);       btn.repaint(); }
        });
        return btn;
    }

    // ── Placeholder ──────────────────────────────────────────────
    private JPanel buildPlaceholder(String title) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241, 18));
                g2.fillRoundRect(0, 0, 80, 80, 20, 20);
                g2.setColor(new Color(99, 102, 241, 160));
                g2.setFont(getViFont(Font.BOLD, 26));
                String s = title.length() >= 2 ? title.substring(0, 2).toUpperCase() : title.toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, (80 - fm.stringWidth(s)) / 2, (80 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
            @Override public Dimension getMaximumSize()   { return getPreferredSize(); }
        };
        iconBox.setOpaque(false);
        iconBox.setAlignmentX(CENTER_ALIGNMENT);

        JLabel l1 = lbl(title, getViFont(Font.BOLD, 20), TEXT_DARK);
        l1.setAlignmentX(CENTER_ALIGNMENT);
        JLabel l2 = lbl("Chức năng đang phát triển...", getViFont(Font.PLAIN, 13), TEXT_MID);
        l2.setAlignmentX(CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(iconBox);
        p.add(Box.createVerticalStrut(14));
        p.add(l1);
        p.add(Box.createVerticalStrut(6));
        p.add(l2);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private void switchPanel(String menu) {
        contentPanel.removeAll();
        JPanel panel;
        switch (menu) {
            case "Tổng quan" -> {
                contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
                panel = new DashboardPanel();
            }
            case "Bán hàng" -> {
                contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
                panel = new BanHangPanel(currentUser);
            }
            case "Sản phẩm" -> {
                contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
                panel = new SanPhamPanel();
            }
            case "Khách hàng" -> {
                contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
                panel = new KhachHangPanel();
            }
            case "Hóa đơn" -> {
                contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
                panel = new HoaDonPanel();
            }
            default -> {
                contentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));
                panel = buildPlaceholder(menu);
            }
        }
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
}