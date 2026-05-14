package com.quanlybanhang.ui.dialogs;

import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.entity.User;
import com.quanlybanhang.ui.LoginForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;

/**
 * Thêm hoặc sửa tài khoản. Mật khẩu để trống khi sửa = giữ nguyên.
 */
public class NhanVienDialog extends JDialog {

    private static final Color BG_PAGE = new Color(241, 245, 249);
    private static final Color BG_HEADER_TOP = new Color(15, 23, 42);
    private static final Color BG_HEADER_END = new Color(30, 41, 59);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_HOVER = new Color(79, 70, 229);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_FIELD = new Color(226, 232, 240);
    private static final Color CARD_BORDER = new Color(226, 232, 240);
    private static final Color ERROR_C = new Color(239, 68, 68);
    private static final Color FOOTER_BG = new Color(248, 250, 252);

    private final UserDAO userDAO = new UserDAO();
    private final User editing;
    private final Runnable onSaved;
    private final boolean staffManagementMode;

    private JTextField txtHo, txtTenDem, txtTen, txtEmail, txtTaiKhoan, txtSdt;
    private JPasswordField txtMatKhau, txtXacNhan;
    private JRadioButton rdNam, rdNu;
    private JComboBox<UserDAO.ChucVuOption> cbChucVu;
    private JLabel lblErr;
    private JButton btnSave;

    public NhanVienDialog(Window owner, User editingUser, Runnable onSaved) {
        this(owner, editingUser, onSaved, false);
    }

    public NhanVienDialog(Window owner, User editingUser, Runnable onSaved, boolean staffManagementMode) {
        super(owner, editingUser == null ? "Thêm nhân viên" : "Sửa nhân viên", ModalityType.APPLICATION_MODAL);
        this.editing = editingUser;
        this.onSaved = onSaved;
        this.staffManagementMode = staffManagementMode;
        setSize(720, 668);
        setLocationRelativeTo(owner);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 720, 668, 18, 18));
        init();
    }

    private void init() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PAGE);

        root.add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = buildBodyScroll();
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.getViewport().setBackground(BG_PAGE);
        root.add(scroll, BorderLayout.CENTER);

        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        applyEditingData();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_HEADER_TOP, getWidth(), 0, BG_HEADER_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(99, 102, 241, 28));
                g2.fillOval(getWidth() - 120, -40, 200, 200);
                g2.dispose();
            }
        };
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(720, 96));
        p.setBorder(new EmptyBorder(18, 28, 18, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        JLabel icon = new JLabel(editing == null ? "✦" : "✎");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        icon.setForeground(new Color(165, 180, 252));
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(editing == null ? "Thêm nhân viên" : "Sửa nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel(editing == null
                ? "Điền thông tin để tạo tài khoản làm việc"
                : "Cập nhật thông tin tài khoản");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(148, 163, 184));
        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(sub);
        left.add(icon);
        left.add(texts);

        JButton btnX = new JButton("✕");
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnX.setForeground(new Color(148, 163, 184));
        btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dispose());

        p.add(left, BorderLayout.CENTER);
        p.add(btnX, BorderLayout.EAST);

        final Point dragOffset = new Point();
        p.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragOffset.setLocation(e.getXOnScreen() - getLocationOnScreen().x,
                        e.getYOnScreen() - getLocationOnScreen().y);
            }
        });
        p.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - dragOffset.x, e.getYOnScreen() - dragOffset.y);
            }
        });

        return p;
    }

    private JScrollPane buildBodyScroll() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(20, 28, 12, 28));

        wrap.add(sectionCard("Họ và tên", buildNameRow()));
        wrap.add(Box.createVerticalStrut(14));
        wrap.add(sectionCard("Liên hệ", buildContactRow()));
        wrap.add(Box.createVerticalStrut(14));
        wrap.add(sectionCard("Tài khoản & chức vụ", buildAccountRow()));
        wrap.add(Box.createVerticalStrut(14));
        wrap.add(sectionCard("Giới tính", buildGenderRow()));
        wrap.add(Box.createVerticalStrut(14));
        wrap.add(sectionCard(
                editing == null ? "Mật khẩu" : "Mật khẩu (để trống khi sửa nếu giữ nguyên)",
                buildPasswordRow()));

        lblErr = new JLabel(" ");
        lblErr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErr.setForeground(ERROR_C);
        lblErr.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblErr.setBorder(new EmptyBorder(8, 4, 0, 4));
        wrap.add(lblErr);

        wrap.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(wrap);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        return sp;
    }

    /** Khối nền trắng bo góc + tiêu đề nhóm */
    private JPanel sectionCard(String sectionTitle, JPanel inner) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 18, 18, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cap = new JLabel(sectionTitle);
        cap.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cap.setForeground(TEXT_DARK);
        card.add(cap, BorderLayout.NORTH);
        inner.setOpaque(false);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildNameRow() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.gridy = 0;
        c.gridx = 0;
        grid.add(fieldCaption("Họ"), c);
        c.gridx = 1;
        grid.add(fieldCaption("Tên đệm"), c);
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        grid.add(fieldCaption("Tên *"), c);
        c.gridy = 1;
        c.insets = new Insets(4, 0, 0, 10);
        c.gridx = 0;
        txtHo = tf("Ví dụ: Nguyễn");
        grid.add(txtHo, c);
        c.gridx = 1;
        txtTenDem = tf("Văn");
        grid.add(txtTenDem, c);
        c.gridx = 2;
        c.insets = new Insets(4, 0, 0, 0);
        txtTen = tf("A *");
        grid.add(txtTen, c);
        return grid;
    }

    private JPanel buildContactRow() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.gridy = 0;
        c.gridx = 0;
        grid.add(fieldCaption("Email *"), c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        grid.add(fieldCaption("Số điện thoại"), c);
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(4, 0, 0, 10);
        txtEmail = tf("email@domain.com");
        grid.add(txtEmail, c);
        c.gridx = 1;
        c.insets = new Insets(4, 0, 0, 0);
        txtSdt = tf("0xxxxxxxxx");
        grid.add(txtSdt, c);
        return grid;
    }

    private JPanel buildAccountRow() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.gridy = 0;
        c.gridx = 0;
        grid.add(fieldCaption("Tài khoản đăng nhập *"), c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        grid.add(fieldCaption("Chức vụ *"), c);
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(4, 0, 0, 10);
        txtTaiKhoan = tf("ten_dang_nhap");
        grid.add(txtTaiKhoan, c);
        c.gridx = 1;
        c.insets = new Insets(4, 0, 0, 0);
        cbChucVu = new JComboBox<>();
        cbChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbChucVu.setBackground(Color.WHITE);
        styleCombo(cbChucVu);
        fillChucVuCombo();
        grid.add(cbChucVu, c);
        return grid;
    }

    private JPanel buildGenderRow() {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        box.setOpaque(false);
        rdNam = new JRadioButton("Nam");
        rdNu = new JRadioButton("Nữ");
        rdNam.setSelected(true);
        rdNam.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdNu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdNam.setForeground(TEXT_DARK);
        rdNu.setForeground(TEXT_DARK);
        rdNam.setOpaque(false);
        rdNu.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rdNam);
        bg.add(rdNu);
        box.add(rdNam);
        box.add(rdNu);
        return box;
    }

    private JPanel buildPasswordRow() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.gridy = 0;
        c.gridx = 0;
        grid.add(fieldCaption(editing == null ? "Mật khẩu *" : "Mật khẩu mới"), c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        grid.add(fieldCaption(editing == null ? "Xác nhận *" : "Xác nhận"), c);
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(4, 0, 0, 10);
        txtMatKhau = pf(editing == null ? "Tối thiểu 6 ký tự" : "Để trống = giữ cũ");
        grid.add(txtMatKhau, c);
        c.gridx = 1;
        c.insets = new Insets(4, 0, 0, 0);
        txtXacNhan = pf("Nhập lại mật khẩu");
        grid.add(txtXacNhan, c);
        return grid;
    }

    private JLabel fieldCaption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JTextField tf(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left,
                            getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                }
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_DARK);
        tf.setPreferredSize(new Dimension(120, 42));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LoginForm.RoundBorder(BORDER_FIELD, 10, 1),
                new EmptyBorder(10, 12, 10, 12)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new LoginForm.RoundBorder(ACCENT, 10, 2),
                        new EmptyBorder(10, 12, 10, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new LoginForm.RoundBorder(BORDER_FIELD, 10, 1),
                        new EmptyBorder(10, 12, 10, 12)));
            }
        });
        return tf;
    }

    private JPasswordField pf(String placeholder) {
        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left,
                            getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                }
            }
        };
        pf.setEchoChar('●');
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setForeground(TEXT_DARK);
        pf.setPreferredSize(new Dimension(120, 42));
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LoginForm.RoundBorder(BORDER_FIELD, 10, 1),
                new EmptyBorder(10, 12, 10, 12)));
        pf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                        new LoginForm.RoundBorder(ACCENT, 10, 2),
                        new EmptyBorder(10, 12, 10, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                        new LoginForm.RoundBorder(BORDER_FIELD, 10, 1),
                        new EmptyBorder(10, 12, 10, 12)));
            }
        });
        return pf;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setPreferredSize(new Dimension(120, 42));
        combo.setBorder(BorderFactory.createCompoundBorder(
                new LoginForm.RoundBorder(BORDER_FIELD, 10, 1),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FOOTER_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, CARD_BORDER),
                new EmptyBorder(14, 28, 18, 28)));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JButton btnCancel = outlineButton("Huỷ");
        btnCancel.addActionListener(e -> dispose());
        btnSave = fillButton(editing == null ? "Tạo tài khoản" : "Lưu thay đổi");
        btnSave.addActionListener(e -> save());
        right.add(btnCancel);
        right.add(btnSave);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JButton outlineButton(String text) {
        Color line = BORDER_FIELD;
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(line);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(TEXT_DARK);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(100, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton fillButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() || getModel().isPressed() ? ACCENT_HOVER : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(editing == null ? 148 : 128, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void applyEditingData() {
        if (editing == null) return;
        txtHo.setText(nvl(editing.getHo()));
        txtTenDem.setText(nvl(editing.getTenDem()));
        txtTen.setText(nvl(editing.getTen()));
        txtEmail.setText(nvl(editing.getEmail()));
        txtSdt.setText(nvl(editing.getSdt()));
        txtTaiKhoan.setText(nvl(editing.getTaiKhoan()));
        if (editing.getGioitinh() != null) {
            if (Boolean.TRUE.equals(editing.getGioitinh())) rdNam.setSelected(true);
            else rdNu.setSelected(true);
        }
        Integer idcv = editing.getIdCV();
        if (idcv != null) {
            for (int i = 0; i < cbChucVu.getItemCount(); i++) {
                if (cbChucVu.getItemAt(i).id == idcv) {
                    cbChucVu.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void fillChucVuCombo() {
        cbChucVu.removeAllItems();
        for (UserDAO.ChucVuOption o : userDAO.listChucVu()) {
            if (staffManagementMode && o.isAdminRole()) continue;
            cbChucVu.addItem(o);
        }
        if (cbChucVu.getItemCount() == 0) {
            for (UserDAO.ChucVuOption o : userDAO.listChucVu()) cbChucVu.addItem(o);
        }
        if (cbChucVu.getItemCount() == 1) cbChucVu.setEnabled(false);
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    private void save() {
        lblErr.setText(" ");
        String ho = txtHo.getText().trim();
        String tenDem = txtTenDem.getText().trim();
        String ten = txtTen.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSdt.getText().trim();
        String tk = txtTaiKhoan.getText().trim();
        String mk = new String(txtMatKhau.getPassword());
        String xn = new String(txtXacNhan.getPassword());
        UserDAO.ChucVuOption cv = (UserDAO.ChucVuOption) cbChucVu.getSelectedItem();
        int idCV = cv != null ? cv.id : 2;

        if (ten.isEmpty() || email.isEmpty() || tk.isEmpty()) {
            lblErr.setText("Điền đủ Họ, Tên, Email, Tài khoản.");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            lblErr.setText("Email không hợp lệ.");
            return;
        }
        if (editing == null) {
            if (mk.length() < 6) {
                lblErr.setText("Mật khẩu tối thiểu 6 ký tự.");
                return;
            }
        } else if (!mk.isEmpty() && mk.length() < 6) {
            lblErr.setText("Mật khẩu mới tối thiểu 6 ký tự.");
            return;
        }
        if (!mk.isEmpty() && !mk.equals(xn)) {
            lblErr.setText("Xác nhận mật khẩu không khớp.");
            return;
        }
        if (editing == null && mk.isEmpty()) {
            lblErr.setText("Nhập mật khẩu cho tài khoản mới.");
            return;
        }

        int excludeId = editing != null ? editing.getId() : -1;
        if (userDAO.existsTaiKhoanForOther(tk, excludeId)) {
            lblErr.setText("Tài khoản đã được dùng.");
            return;
        }
        if (userDAO.existsEmailForOther(email, excludeId)) {
            lblErr.setText("Email đã được dùng.");
            return;
        }

        if (editing != null && editing.isAdmin() && cv != null && !cv.isAdminRole()) {
            if (userDAO.countActiveAdmins() <= 1) {
                lblErr.setText("Không thể đổi vai trò của Quản trị viên duy nhất.");
                return;
            }
        }

        btnSave.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                User u = new User();
                u.setHo(ho.isEmpty() ? null : ho);
                u.setTenDem(tenDem.isEmpty() ? null : tenDem);
                u.setTen(ten);
                u.setEmail(email);
                u.setSdt(sdt.isEmpty() ? null : sdt);
                u.setTaiKhoan(tk);
                u.setGioitinh(rdNam.isSelected());
                u.setIdCV(idCV);
                u.setTrangThai(true);
                if (editing == null) {
                    u.setMatKhau(mk);
                    return userDAO.dangKy(u);
                }
                u.setId(editing.getId());
                return userDAO.capNhat(u, mk.isEmpty() ? null : mk);
            }

            @Override protected void done() {
                btnSave.setEnabled(true);
                try {
                    if (Boolean.TRUE.equals(get())) {
                        if (onSaved != null) onSaved.run();
                        JOptionPane.showMessageDialog(NhanVienDialog.this,
                                editing == null ? "Đã tạo tài khoản." : "Đã cập nhật.",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else
                        lblErr.setText("Lưu thất bại. Kiểm tra kết nối CSDL.");
                } catch (Exception ex) {
                    lblErr.setText(ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định.");
                }
            }
        };
        w.execute();
    }
}
