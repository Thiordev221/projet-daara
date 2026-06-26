package sn.thiordev221.view;

import sn.thiordev221.controller.MaitreController;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MaitreView extends JPanel {

    private JTextField txtMatricule, txtNomComplet, txtTelephone, txtRecherche;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnReinitialiser, btnExporter;
    private JTable tableMaitres;
    private DefaultTableModel tableModel;

    private final MaitreController controller;

    public MaitreView() {
        this.controller = new MaitreController(this);
        setLayout(new BorderLayout(10, 10));

        initFormulairePanel();
        initTablePanel();
        initActions();

        // Chargement initial des maîtres depuis la base de données
        controller.chargerTousLesMaitres();
    }

    private void initFormulairePanel() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestion du Maître (Serigne)"));

        txtMatricule = new JTextField();
        txtNomComplet = new JTextField();
        txtTelephone = new JTextField();

        formPanel.add(new JLabel(" Matricule :"));
        formPanel.add(txtMatricule);
        formPanel.add(new JLabel(" Nom Complet :"));
        formPanel.add(txtNomComplet);
        formPanel.add(new JLabel(" Téléphone :"));
        formPanel.add(txtTelephone);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnModifier.setEnabled(false);
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setEnabled(false);

        btnPanel.add(btnAjouter);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);

        // CONFIGURATION FIXE : On bloque la largeur à 350px pour stopper le débordement
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
    }

    private void initTablePanel() {
        JPanel topTablePanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher par nom :"));
        txtRecherche = new JTextField(15);
        btnReinitialiser = new JButton("Réinitialiser");
        searchPanel.add(txtRecherche);
        searchPanel.add(btnReinitialiser);



        btnExporter = new JButton("Exporter en CSV");

        topTablePanel.add(searchPanel, BorderLayout.CENTER);
        topTablePanel.add(btnExporter, BorderLayout.EAST);

        String[] colonnes = {"Matricule", "Nom Complet", "Téléphone"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableMaitres = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableMaitres);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(topTablePanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void initActions() {
        btnAjouter.addActionListener(e -> {
            controller.ajouterMaitre(txtMatricule.getText(), txtNomComplet.getText(), txtTelephone.getText());
        });

        btnModifier.addActionListener(e -> {
            controller.modifierMaitre(txtMatricule.getText(), txtNomComplet.getText(), txtTelephone.getText());
        });

        btnSupprimer.addActionListener(e -> {
            controller.supprimerMaitre(txtMatricule.getText());
        });

        txtRecherche.addCaretListener(e -> {
            controller.rechercherMaitres(txtRecherche.getText());
        });

        btnReinitialiser.addActionListener(e -> {
            txtRecherche.setText("");
            controller.chargerTousLesMaitres();
        });

        btnExporter.addActionListener(e -> {
            ExportUtil.exporterTableEnCSV(tableMaitres, this);
        });

        tableMaitres.getSelectionModel().addListSelectionListener(e -> {
            int row = tableMaitres.getSelectedRow();
            if (row != -1) {
                txtMatricule.setText(tableModel.getValueAt(row, 0).toString());
                txtNomComplet.setText(tableModel.getValueAt(row, 1).toString());
                txtTelephone.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");

                txtMatricule.setEditable(false); // La clé primaire saisie n'est pas modifiable
                btnAjouter.setEnabled(false);
                btnModifier.setEnabled(true);
                btnSupprimer.setEnabled(true);
            }
        });
    }

    public void afficherMaitresDansTableau(List<Maitre> maitres) {
        tableModel.setRowCount(0);
        for (Maitre m : maitres) {
            Object[] ligne = { m.getMatricule(), m.getNomComplet(), m.getTelephone() };
            tableModel.addRow(ligne);
        }
    }

    public void reinitialiserFormulaire() {
        txtMatricule.setText("");
        txtNomComplet.setText("");
        txtTelephone.setText("");
        txtMatricule.setEditable(true);
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
        tableMaitres.clearSelection();
    }
}