package com.quanlybanhang;

import com.quanlybanhang.ui.LoginForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // use default
        }

        // Fix font rendering on some systems
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            LoginForm loginFrame = new LoginForm();
            loginFrame.setVisible(true);
        });
    }
}
