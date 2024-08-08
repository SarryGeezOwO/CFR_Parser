package com.SarryTools.GUIEditor;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    public Application() {
        init();
    }

    private void init() {
        setTitle("CFR Visual Editor");
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 550));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(new ContentPanel());
    }

    public static void main(String[] args) {
        FlatJetBrainsMonoFont.install();
        UIManager.put("defaultFont", new Font(FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 14));

        FlatLaf.registerCustomDefaultsSource("Configurations.FlatLaf");
        FlatArcDarkIJTheme.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }

}
