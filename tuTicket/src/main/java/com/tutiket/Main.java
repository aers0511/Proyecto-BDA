package com.tutiket;

import com.tutiket.util.DataInitializer;
import com.tutiket.view.LoginFrame;

import javax.swing.*;
import javax.xml.crypto.Data;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DataInitializer.cargarDatosIniciales();
                
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}