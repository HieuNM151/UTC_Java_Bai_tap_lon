package com.quanlybanhang.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Chạy file này để xem font nào hiển thị icon đẹp trên máy bạn.
 * Sau khi biết font nào OK, báo lại để fix MainForm.
 */
public class FontTest extends JFrame {

    public FontTest() {
        setTitle("Font Icon Test");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel();
        root.setBackground(Color.WHITE);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Danh sách font thử nghiệm
        String[] fonts = {
            "Segoe UI Emoji",
            "Segoe UI Symbol",
            "Arial Unicode MS",
            "Dialog",
            "Serif",
            "SansSerif",
            "Noto Emoji",
            "Noto Color Emoji",
        };

        // Test strings
        String icons = "  \u2302 \u2605 \u25A0 \u25CF \u2764 \u2713 \u2715 \u2630";
        String emoji  = "  \uD83D\uDED2 \uD83D\uDCE6 \uD83D\uDC65 \uD83E\uDDFE \uD83D\uDCCA";

        JLabel header = new JLabel("=== Ket qua test font icon ===");
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setAlignmentX(LEFT_ALIGNMENT);
        root.add(header);
        root.add(Box.createVerticalStrut(10));

        // Kiểm tra từng font
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.Set<String> installed = new java.util.HashSet<>(
            java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));

        for (String fontName : fonts) {
            boolean exists = installed.contains(fontName);
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);

            // Tên font + trạng thái
            JLabel nameLabel = new JLabel(String.format("%-25s [%s]  →", fontName, exists ? "CO" : "KHONG"));
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            nameLabel.setForeground(exists ? new Color(15,150,50) : Color.LIGHT_GRAY);

            // Hiển thị icon với font đó
            JLabel iconsLabel = new JLabel(icons);
            iconsLabel.setFont(new Font(fontName, Font.PLAIN, 16));

            JLabel emojiLabel = new JLabel(emoji);
            emojiLabel.setFont(new Font(fontName, Font.PLAIN, 16));

            row.add(nameLabel);
            row.add(iconsLabel);
            row.add(new JLabel("  |  emoji:"));
            row.add(emojiLabel);
            root.add(row);
            root.add(Box.createVerticalStrut(4));
        }

        root.add(Box.createVerticalStrut(16));

        // In ra console danh sách tất cả font có chữ "emoji/symbol/icon"
        JLabel console = new JLabel("=== Font co tu 'emoji/symbol' tren may ban ===");
        console.setFont(new Font("Segoe UI", Font.BOLD, 13));
        console.setAlignmentX(LEFT_ALIGNMENT);
        root.add(console);

        StringBuilder found = new StringBuilder("<html>");
        for (String f : ge.getAvailableFontFamilyNames()) {
            String fl = f.toLowerCase();
            if (fl.contains("emoji") || fl.contains("symbol") || fl.contains("icon") || fl.contains("noto")) {
                found.append("&nbsp;&nbsp;• ").append(f).append("<br>");
                System.out.println("FOUND: " + f);
            }
        }
        found.append("</html>");

        JLabel foundLabel = new JLabel(found.length() > 14 ? found.toString() : "<html><i>Khong tim thay font emoji/symbol nao</i></html>");
        foundLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        foundLabel.setForeground(new Color(99, 102, 241));
        foundLabel.setAlignmentX(LEFT_ALIGNMENT);
        root.add(foundLabel);

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        setContentPane(scroll);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FontTest().setVisible(true));
    }
}
