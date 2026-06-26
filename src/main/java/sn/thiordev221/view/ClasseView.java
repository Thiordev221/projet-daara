package sn.thiordev221.view;

import sn.thiordev221.controller.ClasseController;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.daara.model.models.Niveau;
import sn.thiordev221.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClasseView extends JPanel {

    private JTextField txtCode, txtLibelle, txtRecherche;
    private JComboBox<String> comboNiveau; // Enum Niveau (Débutant, Intermédiaire, Avancé)
    private JComboBox<Maitre> comboMaitre; // Liste déroulante des maîtres
    private JButton btnAjouter, btnModifier, btnSupprimer, btnReinitialiser, btnExporter;
    private JTable tableClasses;
    private DefaultTableModel tableModel;

    private final ClasseController controller;

    public ClasseView() {
        this.controller = new ClasseController(this);
        setLayout(new BorderLayout(10, 10));

        initFormulairePanel();
        initTablePanel();
        initActions();

        // Chargement initial des données de l'onglet
        controller.chargerToutesLesClasses();
        controller.chargerMaitresPourFormulaire();
    }

    private void initFormulairePanel() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestion de la Classe (Halqa)"));

        txtCode = new JTextField();
        txtLibelle = new JTextField();

        // Remplissage statique des valeurs de l'énoncé pour le niveau
        comboNiveau = new JComboBox<>(new String[]{"DEBUTANT", "INTERMEDIAIRE", "AVANCE"});
        comboMaitre = new JComboBox<>();

        formPanel.add(new JLabel(" Code Classe :"));
        formPanel.add(txtCode);
        formPanel.add(new JLabel(" Libellé :"));
        formPanel.add(txtLibelle);
        formPanel.add(new JLabel(" Niveau :"));
        formPanel.add(comboNiveau);
        formPanel.add(new JLabel(" Maître Responsable :"));
        formPanel.add(comboMaitre);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnModifier.setEnabled(false);
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setEnabled(false);

        btnPanel.add(btnAjouter);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);

        // CONFIGURATION FIXE : Résout l'étirement exagéré constaté sur l'UI
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
    }

    private void initTablePanel() {
        JPanel topTablePanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher par libellé :"));
        txtRecherche = new JTextField(15);
        btnReinitialiser = new JButton("Réinitialiser");
        searchPanel.add(txtRecherche);
        searchPanel.add(btnReinitialiser);

        btnExporter = new JButton("Exporter en CSV");

        topTablePanel.add(searchPanel, BorderLayout.CENTER);
        topTablePanel.add(btnExporter, BorderLayout.EAST);

        String[] colonnes = {"Code", "Libellé", "Niveau", "Maître Encadrant"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableClasses = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableClasses);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(topTablePanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void initActions() {
        btnAjouter.addActionListener(e -> {
            controller.ajouterClasse(txtCode.getText(), txtLibelle.getText(),
                    Niveau.valueOf(comboNiveau.getSelectedItem().toString()), (Maitre) comboMaitre.getSelectedItem());
        });

        btnModifier.addActionListener(e -> {
            controller.modifierClasse(txtCode.getText(), txtLibelle.getText(),
                    Niveau.valueOf(comboNiveau.getSelectedItem().toString()), (Maitre) comboMaitre.getSelectedItem());
        });

        btnSupprimer.addActionListener(e -> {
            controller.supprimerClasse(txtCode.getText());
        });

        txtRecherche.addCaretListener(e -> {
            controller.rechercherClasses(txtRecherche.getText());
        });

        btnReinitialiser.addActionListener(e -> {
            txtRecherche.setText("");
            controller.chargerToutesLesClasses();
        });

        btnExporter.addActionListener(e -> {
            ExportUtil.exporterTableEnCSV(tableClasses, this);
        });

        tableClasses.getSelectionModel().addListSelectionListener(e -> {
            int row = tableClasses.getSelectedRow();
            if (row != -1) {
                txtCode.setText(tableModel.getValueAt(row, 0).toString());
                txtLibelle.setText(tableModel.getValueAt(row, 1).toString());
                comboNiveau.setSelectedItem(tableModel.getValueAt(row, 2).toString());

                // Resélectionner le bon maître dans le JComboBox basé sur le nom affiché
                String nomMaitreChaine = tableModel.getValueAt(row, 3).toString();
                for (int i = 0; i < comboMaitre.getItemCount(); i++) {
                    Maitre m = comboMaitre.getItemAt(i);
                    if (nomMaitreChaine.equals(m.getNomComplet())) {
                        comboMaitre.setSelectedIndex(i);
                        break;
                    }
                }

                txtCode.setEditable(false); // Le code de classe saisi n'est pas modifiable
                btnAjouter.setEnabled(false);
                btnModifier.setEnabled(true);
                btnSupprimer.setEnabled(true);
            }
        });
    }

    public void afficherClassesDansTableau(List<Classe> classes) {
        tableModel.setRowCount(0);
        for (Classe c : classes) {
            Object[] ligne = {
                    c.getCode(),
                    c.getLibelle(),
                    c.getNiveau() != null ? c.getNiveau().toString() : "",
                    c.getMaitre() != null ? c.getMaitre().getNomComplet() : "Aucun"
            };
            tableModel.addRow(ligne);
        }
    }

    public void remplirListeMaitres(List<Maitre> maitres) {
        comboMaitre.removeAllItems();
        for (Maitre m : maitres) {
            comboMaitre.addItem(m);
        }
    }

    public void reinitialiserFormulaire() {
        txtCode.setText("");
        txtLibelle.setText("");
        if (comboNiveau.getItemCount() > 0) comboNiveau.setSelectedIndex(0);
        if (comboMaitre.getItemCount() > 0) comboMaitre.setSelectedIndex(0);
        txtCode.setEditable(true);
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
        tableClasses.clearSelection();
    }
}