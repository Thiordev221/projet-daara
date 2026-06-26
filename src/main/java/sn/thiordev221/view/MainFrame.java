package sn.thiordev221.view;

import sn.thiordev221.util.HibernateUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    public MainFrame() {
        // 1. Propriétés de base de la fenêtre
        setTitle("Système de Gestion de la Daara - Licence 2 GL");
        setSize(1000, 650); // Une taille confortable pour afficher le formulaire et le tableau
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 2. Création du système d'onglets
        JTabbedPane tabbedPane = new JTabbedPane();

        // 3. Instanciation et ajout de l'onglet Maître
        MaitreView maitreView = new MaitreView();
        tabbedPane.addTab("Gestion des Maîtres", new ImageIcon(), maitreView, "Gérer les Serignes");
        ClasseView classeView = new ClasseView();
        tabbedPane.addTab("Gestion des Classes", classeView);
        // Dans le constructeur de MainFrame.java :
        TalibeView talibeView = new TalibeView();
        tabbedPane.addTab("Gestion des Talibés", talibeView);
        // Dans le constructeur de MainFrame.java :
        ProgressionView progressionView = new ProgressionView();
        tabbedPane.addTab("Suivi des Progressions", progressionView);
        // 4. Emplacements pour les prochains modules (vous les ajouterez au fur et à mesure)
        // tabbedPane.addTab("Gestion des Classes", new ClasseView());
        // tabbedPane.addTab("Gestion des Talibés", new TalibeView());
        // tabbedPane.addTab("Suivi des Progressions", new ProgressionView());

        // 5. Ajout du système d'onglets au centre de la fenêtre
        add(tabbedPane, BorderLayout.CENTER);

        // 6. Gestion propre de la fermeture (Fermer le pool de connexion Hibernate)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Fermeture de l'application... Déconnexion de PostgreSQL.");
                HibernateUtil.shutdown();
            }
        });
    }
}
