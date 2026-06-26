package sn.thiordev221;


import sn.thiordev221.view.MainFrame;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Configuration du style visuel de l'application (Look and Feel système)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si le style du système échoue, Swing garde son style par défaut (Metal)
            System.out.println("Impossible d'appliquer le style système visuel.");
        }

        // Lancement de l'interface graphique dans le thread approprié
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            System.out.println("=== Application Daara lancée avec succès ===");
        });
    }
}