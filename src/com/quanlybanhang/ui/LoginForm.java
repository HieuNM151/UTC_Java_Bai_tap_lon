package com.quanlybanhang.ui;

import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.entity.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends JFrame {

    private static final Color BG_LEFT    = new Color(15, 23, 42);
    private static final Color BG_RIGHT   = new Color(248, 250, 252);
    private static final Color ACCENT     = new Color(99, 102, 241);
    private static final Color ACCENT_H   = new Color(79, 70, 229);
    private static final Color TEXT_DARK  = new Color(15, 23, 42);
    private static final Color TEXT_GRAY  = new Color(100, 116, 139);
    private static final Color BORDER_CLR = new Color(203, 213, 225);
    private static final Color ERROR_CLR  = new Color(239, 68, 68);

    private JTextField     txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JCheckBox      chkShowPass;
    private JLabel         lblError;
    private JButton        btnDangNhap;

    private final UserDAO userDAO = new UserDAO();

    public LoginForm() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 820, 540, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setPreferredSize(new Dimension(820, 540));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());
        setContentPane(root);
    }

    // ── Panel trái ───────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,new Color(15,23,42),0,getHeight(),new Color(30,41,59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(99, 102, 241, 30));
                g2.fillOval(-60, -60, 250, 250);
                g2.setColor(new Color(99, 102, 241, 20));
                g2.fillOval(150, 300, 200, 200);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(60, 40, 40, 40));

        JLabel icon = new JLabel("🛒");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = makeLabel("Quản Lý", new Font("Segoe UI", Font.BOLD, 26), Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = makeLabel("Bán Hàng", new Font("Segoe UI", Font.BOLD, 22), ACCENT);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(120, 1));
        sep.setForeground(new Color(99, 102, 241, 100));

        JLabel desc = makeLabel(
                "<html><center>Hệ thống quản lý<br>bán hàng thời trang</center></html>",
                new Font("Segoe UI", Font.PLAIN, 13),
                new Color(148, 163, 184));
        desc.setAlignmentX(CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(icon);
        p.add(Box.createVerticalStrut(16));
        p.add(title);
        p.add(sub);
        p.add(Box.createVerticalStrut(20));
        p.add(sep);
        p.add(Box.createVerticalStrut(16));
        p.add(desc);
        p.add(Box.createVerticalGlue());

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setForeground(new Color(148, 163, 184));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setAlignmentX(RIGHT_ALIGNMENT);
        btnClose.addActionListener(e -> System.exit(0));
        p.add(btnClose);
        return p;
    }

    // ── Panel phải ───────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_RIGHT);
        p.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setBackground(BG_RIGHT);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 10, 0, 10));
        form.setMaximumSize(new Dimension(320, 450));

        JLabel lbTitle = makeLabel("Đăng nhập", new Font("Segoe UI", Font.BOLD, 24), TEXT_DARK);
        lbTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbSub = makeLabel("Chào mừng trở lại 👋", new Font("Segoe UI", Font.PLAIN, 13), TEXT_GRAY);
        lbSub.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbTK = makeLabel("Tài khoản / Email", new Font("Segoe UI", Font.BOLD, 12), TEXT_DARK);
        lbTK.setAlignmentX(LEFT_ALIGNMENT);
        txtTaiKhoan = buildTextField("Nhập tài khoản hoặc email...");

        JLabel lbMK = makeLabel("Mật khẩu", new Font("Segoe UI", Font.BOLD, 12), TEXT_DARK);
        lbMK.setAlignmentX(LEFT_ALIGNMENT);
        txtMatKhau = buildPasswordField("Nhập mật khẩu...");

        chkShowPass = new JCheckBox("Hiện mật khẩu");
        chkShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPass.setForeground(TEXT_GRAY);
        chkShowPass.setBackground(BG_RIGHT);
        chkShowPass.setAlignmentX(LEFT_ALIGNMENT);
        chkShowPass.addActionListener(e ->
                txtMatKhau.setEchoChar(chkShowPass.isSelected() ? (char)0 : '●'));

        lblError = makeLabel("", new Font("Segoe UI", Font.PLAIN, 12), ERROR_CLR);
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        btnDangNhap = buildButton("Đăng nhập", ACCENT, ACCENT_H);
        btnDangNhap.addActionListener(e -> handleLogin());

        // ── Nút đăng ký ──
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setBackground(BG_RIGHT);
        regRow.setAlignmentX(LEFT_ALIGNMENT);
        regRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel regHint = new JLabel("Chưa có tài khoản?");
        regHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regHint.setForeground(TEXT_GRAY);
        JButton btnReg = new JButton("Đăng ký ngay");
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReg.setForeground(ACCENT);
        btnReg.setContentAreaFilled(false);
        btnReg.setBorderPainted(false);
        btnReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReg.addActionListener(e -> {
            RegisterForm reg = new RegisterForm(this);
            reg.setVisible(true);
        });
        regRow.add(regHint);
        regRow.add(btnReg);

        // Enter để login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        txtTaiKhoan.addKeyListener(enterKey);
        txtMatKhau.addKeyListener(enterKey);

        form.add(lbTitle);
        form.add(Box.createVerticalStrut(4));
        form.add(lbSub);
        form.add(Box.createVerticalStrut(28));
        form.add(lbTK);
        form.add(Box.createVerticalStrut(6));
        form.add(txtTaiKhoan);
        form.add(Box.createVerticalStrut(16));
        form.add(lbMK);
        form.add(Box.createVerticalStrut(6));
        form.add(txtMatKhau);
        form.add(Box.createVerticalStrut(8));
        form.add(chkShowPass);
        form.add(Box.createVerticalStrut(6));
        form.add(lblError);
        form.add(Box.createVerticalStrut(20));
        form.add(btnDangNhap);
        form.add(Box.createVerticalStrut(12));
        form.add(regRow);

        p.add(form);
        return p;
    }

    // ── Xử lý đăng nhập ─────────────────────────────────────────
    private void handleLogin() {
        String tk = txtTaiKhoan.getText().trim();
        String mk = new String(txtMatKhau.getPassword()).trim();

        if (tk.isEmpty() || mk.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        btnDangNhap.setEnabled(false);
        btnDangNhap.setText("Đang kiểm tra...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override protected User doInBackground() {
                return userDAO.dangNhap(tk, mk);
            }
            @Override protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        lblError.setText("");
                        dispose();
                        // ✅ Mở MainForm sau khi đăng nhập
                        SwingUtilities.invokeLater(() -> {
                            MainForm main = new MainForm(user);
                            main.setVisible(true);
                        });
                    } else {
                        if (userDAO.existsTaiKhoan(tk))
                            showError("Mật khẩu không đúng hoặc tài khoản bị khoá!");
                        else
                            showError("Tài khoản / Email không tồn tại!");
                    }
                } catch (Exception ex) {
                    showError("Lỗi kết nối: " + ex.getMessage());
                } finally {
                    btnDangNhap.setEnabled(true);
                    btnDangNhap.setText("Đăng nhập");
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg) {
        lblError.setText("<html>" + msg + "</html>");
    }

    // ── Helpers ──────────────────────────────────────────────────
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private JTextField buildTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(180, 190, 205));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left,
                            getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 2);
                }
            }
        };
        styleField(tf);
        return tf;
    }

    private JPasswordField buildPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(180, 190, 205));
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left,
                            getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 2);
                }
            }
        };
        pf.setEchoChar('●');
        styleField(pf);
        return pf;
    }

    private void styleField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_DARK);
        tf.setBackground(Color.WHITE);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(BORDER_CLR, 8, 1), new EmptyBorder(8, 12, 8, 12)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(ACCENT, 8, 2), new EmptyBorder(8, 12, 8, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(BORDER_CLR, 8, 1), new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    private JButton buildButton(String text, Color fill, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() || getModel().isPressed() ? hover : fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── RoundBorder (dùng chung với RegisterForm) ────────────────
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius, thickness;
        public RoundBorder(Color color, int radius, int thickness) {
            this.color = color; this.radius = radius; this.thickness = thickness;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x+1, y+1, w-2, h-2, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}