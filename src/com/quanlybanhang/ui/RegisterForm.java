package com.quanlybanhang.ui;

import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.entity.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class RegisterForm extends JDialog {

    private static final Color BG        = new Color(248, 250, 252);
    private static final Color BG_DARK   = new Color(15, 23, 42);
    private static final Color ACCENT    = new Color(99, 102, 241);
    private static final Color ACCENT_H  = new Color(79, 70, 229);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_GRAY = new Color(100, 116, 139);
    private static final Color BORDER_C  = new Color(203, 213, 225);
    private static final Color ERROR_C   = new Color(239, 68, 68);
    private static final Color SUCCESS_C = new Color(34, 197, 94);

    private JTextField     txtHo, txtTenDem, txtTen, txtEmail, txtTaiKhoan, txtSdt;
    private JPasswordField txtMatKhau, txtXacNhan;
    private JRadioButton   rdNam, rdNu;
    private JLabel         lblStatus;
    private JButton        btnDangKy, btnHuy;

    private final UserDAO userDAO = new UserDAO();

    public RegisterForm(JFrame parent) {
        super(parent, "Đăng ký tài khoản", true);
        setSize(680, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 680, 620, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Header ──────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,new Color(15,23,42),getWidth(),0,new Color(30,41,59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // circle deco
                g2.setColor(new Color(99,102,241,25));
                g2.fillOval(getWidth()-100, -30, 160, 160);
                g2.dispose();
            }
        };
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(680, 90));
        p.setBorder(new EmptyBorder(20, 30, 20, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JLabel icon = new JLabel("📝");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Đăng ký tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Tạo tài khoản mới cho nhân viên");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(148, 163, 184));
        texts.add(title);
        texts.add(sub);
        left.add(icon);
        left.add(texts);

        // Nút đóng
        JButton btnX = new JButton("✕");
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnX.setForeground(new Color(148, 163, 184));
        btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dispose());

        p.add(left, BorderLayout.CENTER);
        p.add(btnX, BorderLayout.EAST);
        return p;
    }

    // ── Body (form fields) ───────────────────────────────────────
    private JScrollPane buildBody() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 40, 10, 40));

        // ── Hàng 1: Họ | Tên đệm | Tên ──
        p.add(makeRowLabel("Họ và tên *"));
        JPanel row1 = new JPanel(new GridLayout(1, 3, 10, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtHo     = buildTextField("Họ");
        txtTenDem = buildTextField("Tên đệm");
        txtTen    = buildTextField("Tên *");
        row1.add(txtHo);
        row1.add(txtTenDem);
        row1.add(txtTen);
        p.add(row1);
        p.add(Box.createVerticalStrut(12));

        // ── Hàng 2: Email | SĐT ──
        JPanel row2lbl = new JPanel(new GridLayout(1, 2, 10, 0));
        row2lbl.setOpaque(false);
        row2lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row2lbl.add(makeRowLabel("Email *"));
        row2lbl.add(makeRowLabel("Số điện thoại"));
        p.add(row2lbl);
        JPanel row2 = new JPanel(new GridLayout(1, 2, 10, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtEmail = buildTextField("example@email.com");
        txtSdt   = buildTextField("0xxxxxxxxx");
        row2.add(txtEmail);
        row2.add(txtSdt);
        p.add(row2);
        p.add(Box.createVerticalStrut(12));

        // ── Hàng 3: Tài khoản | Giới tính ──
        JPanel row3lbl = new JPanel(new GridLayout(1, 2, 10, 0));
        row3lbl.setOpaque(false);
        row3lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row3lbl.add(makeRowLabel("Tài khoản *"));
        row3lbl.add(makeRowLabel("Giới tính"));
        p.add(row3lbl);
        JPanel row3 = new JPanel(new GridLayout(1, 2, 10, 0));
        row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtTaiKhoan = buildTextField("Tên đăng nhập...");
        // Radio giới tính
        JPanel pGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        pGT.setBackground(Color.WHITE);
        pGT.setBorder(BorderFactory.createCompoundBorder(
            new LoginForm.RoundBorder(BORDER_C, 8, 1), new EmptyBorder(0,4,0,4)));
        rdNam = new JRadioButton("Nam");
        rdNu  = new JRadioButton("Nữ");
        rdNam.setSelected(true);
        rdNam.setBackground(Color.WHITE);
        rdNu.setBackground(Color.WHITE);
        rdNam.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdNu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ButtonGroup bg = new ButtonGroup();
        bg.add(rdNam); bg.add(rdNu);
        pGT.add(rdNam); pGT.add(rdNu);
        row3.add(txtTaiKhoan);
        row3.add(pGT);
        p.add(row3);
        p.add(Box.createVerticalStrut(12));

        // ── Hàng 4: Mật khẩu | Xác nhận ──
        JPanel row4lbl = new JPanel(new GridLayout(1, 2, 10, 0));
        row4lbl.setOpaque(false);
        row4lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row4lbl.add(makeRowLabel("Mật khẩu *"));
        row4lbl.add(makeRowLabel("Xác nhận mật khẩu *"));
        p.add(row4lbl);
        JPanel row4 = new JPanel(new GridLayout(1, 2, 10, 0));
        row4.setOpaque(false);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtMatKhau = buildPasswordField("Tối thiểu 6 ký tự");
        txtXacNhan = buildPasswordField("Nhập lại mật khẩu");
        row4.add(txtMatKhau);
        row4.add(txtXacNhan);
        p.add(row4);
        p.add(Box.createVerticalStrut(12));

        // ── Status ──
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lblStatus);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(8);
        return scroll;
    }

    // ── Footer (buttons) ─────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        p.setBackground(new Color(241, 245, 249));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_C));

        btnHuy = buildOutlineButton("Huỷ");
        btnHuy.addActionListener(e -> dispose());

        btnDangKy = buildFillButton("Đăng ký");
        btnDangKy.addActionListener(e -> handleRegister());

        p.add(btnHuy);
        p.add(btnDangKy);
        return p;
    }

    // ── Xử lý đăng ký ───────────────────────────────────────────
    private void handleRegister() {
        String ho      = txtHo.getText().trim();
        String tenDem  = txtTenDem.getText().trim();
        String ten     = txtTen.getText().trim();
        String email   = txtEmail.getText().trim();
        String sdt     = txtSdt.getText().trim();
        String tk      = txtTaiKhoan.getText().trim();
        String mk      = new String(txtMatKhau.getPassword());
        String xn      = new String(txtXacNhan.getPassword());

        // Validate
        if (ten.isEmpty() || email.isEmpty() || tk.isEmpty() || mk.isEmpty()) {
            showStatus("Vui lòng điền đầy đủ các trường bắt buộc (*)!", ERROR_C);
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showStatus("Email không hợp lệ!", ERROR_C);
            return;
        }
        if (mk.length() < 6) {
            showStatus("Mật khẩu phải có ít nhất 6 ký tự!", ERROR_C);
            return;
        }
        if (!mk.equals(xn)) {
            showStatus("Mật khẩu xác nhận không khớp!", ERROR_C);
            return;
        }

        btnDangKy.setEnabled(false);
        btnDangKy.setText("Đang xử lý...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() {
                // Kiểm tra trùng tài khoản / email
                if (userDAO.existsTaiKhoan(tk))    return "Tài khoản đã tồn tại!";
                if (userDAO.existsEmail(email))     return "Email đã được sử dụng!";

                User u = new User();
                u.setHo(ho.isEmpty() ? null : ho);
                u.setTenDem(tenDem.isEmpty() ? null : tenDem);
                u.setTen(ten);
                u.setEmail(email);
                u.setSdt(sdt.isEmpty() ? null : sdt);
                u.setTaiKhoan(tk);
                u.setMatKhau(mk);
                u.setGioitinh(rdNam.isSelected());
                u.setTrangThai(true);
                u.setIdCV(2); // Mặc định: nhân viên

                boolean ok = userDAO.dangKy(u);
                return ok ? "OK" : "Lỗi khi tạo tài khoản, vui lòng thử lại!";
            }

            @Override protected void done() {
                try {
                    String result = get();
                    if ("OK".equals(result)) {
                        showStatus("✓ Đăng ký thành công!", SUCCESS_C);
                        JOptionPane.showMessageDialog(RegisterForm.this,
                            "Tài khoản \"" + tk + "\" đã được tạo thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        showStatus(result, ERROR_C);
                    }
                } catch (Exception ex) {
                    showStatus("Lỗi: " + ex.getMessage(), ERROR_C);
                } finally {
                    btnDangKy.setEnabled(true);
                    btnDangKy.setText("Đăng ký");
                }
            }
        };
        worker.execute();
    }

    private void showStatus(String msg, Color color) {
        lblStatus.setText("<html>" + msg + "</html>");
        lblStatus.setForeground(color);
    }

    // ── Helpers UI ───────────────────────────────────────────────
    private JLabel makeRowLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_DARK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
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
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LoginForm.RoundBorder(BORDER_C, 8, 1),
            new EmptyBorder(8, 12, 8, 12)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LoginForm.RoundBorder(ACCENT, 8, 2),
                    new EmptyBorder(8, 12, 8, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LoginForm.RoundBorder(BORDER_C, 8, 1),
                    new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    private JButton buildFillButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_H : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241,245,249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_C);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
