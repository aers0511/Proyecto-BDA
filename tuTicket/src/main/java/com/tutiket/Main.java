package com.tutiket;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.tutiket.util.DataInitializer;
import com.tutiket.view.LoginFrame;
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