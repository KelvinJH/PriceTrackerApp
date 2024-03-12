package com.ecfx.service;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class NotificationService {

    public static void notify(String title, String message) {
        JFrame frame = new JFrame("Notification");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
