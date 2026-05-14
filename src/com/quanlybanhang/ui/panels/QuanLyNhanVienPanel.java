package com.quanlybanhang.ui.panels;

import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.entity.User;
import com.quanlybanhang.ui.dialogs.NhanVienDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * Danh sách tài khoản nhân viên (không hiển thị quản trị), thêm / sửa / vô hiệu hóa.
 */
public class QuanLyNhanVienPanel extends JPanel {

    private static final Color BG = new Color(248, 250, 252);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color LABEL = new Color(71, 85, 105);
    private static final Color TEXT_DARK = new Color(15, 23, 42);

    private final User sessionUser;
    private final UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel model;

    public QuanLyNhanVienPanel(User currentUser) {
        this.sessionUser = Objects.requireNonNull(currentUser, "currentUser");
        if (!currentUser.isAdmin()) {
            setLayout(new BorderLayout());
            add(new JLabel("Bạn không có quyền truy cập.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }
        setLayout(new BorderLayout(0, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        JLabel head = new JLabel("Quản lý nhân viên");
        head.setFont(new Font("Segoe UI", Font.BOLD, 20));
        head.setForeground(TEXT_DARK);
        add(head, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton btnAdd = btn("Thêm", ACCENT);
        JButton btnEdit = btn("Sửa", new Color(59, 130, 246));
        JButton btnDel = btn("Xóa", DANGER);
        JButton btnRef = btn("Làm mới", LABEL);
        btnAdd.addActionListener(e -> openDialog(null));
        btnEdit.addActionListener(e -> openDialog(getSelected()));
        btnDel.addActionListener(e -> deleteSelected());
        btnRef.addActionListener(e -> reload());
        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDel);
        actions.add(btnRef);
        card.add(actions, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Id", "Họ tên", "Tài khoản", "Email", "Chức vụ", "SĐT"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn c0 = table.getColumnModel().getColumn(0);
        c0.setMinWidth(0);
        c0.setMaxWidth(0);
        c0.setPreferredWidth(0);
        c0.setResizable(false);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openDialog(getSelected());
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        card.add(sp, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        reload();
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return b;
    }

    private User getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int id = (Integer) model.getValueAt(row, 0);
        return userDAO.getById(id);
    }

    private void openDialog(User edit) {
        Window w = SwingUtilities.getWindowAncestor(this);
        new NhanVienDialog(w, edit, this::reload, true).setVisible(true);
    }

    private void deleteSelected() {
        User sel = getSelected();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Chọn một nhân viên trong bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sel.getId() == sessionUser.getId()) {
            JOptionPane.showMessageDialog(this, "Không thể vô hiệu chính tài khoản đang đăng nhập.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sel.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Chỉ quản lý tài khoản nhân viên từ màn hình này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
                "Vô hiệu hóa tài khoản \"" + sel.getTaiKhoan() + "\"?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        if (userDAO.voHieuHoa(sel.getId())) {
            reload();
            JOptionPane.showMessageDialog(this, "Đã vô hiệu hóa tài khoản.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else
            JOptionPane.showMessageDialog(this, "Thao tác thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void reload() {
        model.setRowCount(0);
        for (User u : userDAO.getAll()) {
            if (u.isAdmin()) continue;
            String cv = u.getTenChucVu() != null ? u.getTenChucVu() : "";
            String sdt = u.getSdt() != null ? u.getSdt() : "";
            model.addRow(new Object[]{
                    u.getId(),
                    u.getHoTenDay(),
                    u.getTaiKhoan() != null ? u.getTaiKhoan() : "",
                    u.getEmail() != null ? u.getEmail() : "",
                    cv,
                    sdt
            });
        }
    }
}
