package sn.thiordev221.view;

import sn.thiordev221.controller.TalibeController;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TalibeView extends JPanel {

    private JTextField txtMatricule, txtPrenom, txtNom, txtDateNaissance, txtNomTuteur, txtTelTuteur, txtRecherche;
    private JComboBox<Classe> comboClasse;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnReinitialiser, btnExporter;
    private JTable tableTalibes;
    private DefaultTableModel tableModel;

    private final TalibeController controller;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TalibeView() {
        this.controller = new TalibeController(this);
        setLayout(new BorderLayout(10, 10));

        initFormulairePanel();
        initTablePanel();
        initActions();

        controller.chargerTousLesTalibes();
        controller.chargerClassesPourFormulaire();
    }

    private void initFormulairePanel() {
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Inscription / Gestion Talibé"));

        txtMatricule = new JTextField();
        txtPrenom = new JTextField();
        txtNom = new JTextField();
        txtDateNaissance = new JTextField();
        txtDateNaissance.setToolTipText("Format: JJ/MM/AAAA");
        txtNomTuteur = new JTextField();
        txtTelTuteur = new JTextField();
        comboClasse = new JComboBox<>();

        formPanel.add(new JLabel(" Matricule :"));
        formPanel.add(txtMatricule);
        formPanel.add(new JLabel(" Prénom :"));
        formPanel.add(txtPrenom);
        formPanel.add(new JLabel(" Nom :"));
        formPanel.add(txtNom);
        formPanel.add(new JLabel(" Date Naissance :"));
        formPanel.add(txtDateNaissance);
        formPanel.add(new JLabel(" Nom du Tuteur :"));
        formPanel.add(txtNomTuteur);
        formPanel.add(new JLabel(" Tél Tuteur :"));
        formPanel.add(txtTelTuteur);
        formPanel.add(new JLabel(" Halqa (Classe) :"));
        formPanel.add(comboClasse);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAjouter = new JButton("Inscrire");
        btnModifier = new JButton("Modifier");
        btnModifier.setEnabled(false);
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setEnabled(false);

        btnPanel.add(btnAjouter);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);

        // SOLUTION ICI : Contenir le formulaire dans une largeur fixe et stricte
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(380, 0)); // 380px de large, la hauteur s'adapte
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
    }

    private void initTablePanel() {
        JPanel topTablePanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher (Prénom / Nom) :"));
        txtRecherche = new JTextField(15);
        btnReinitialiser = new JButton("Réinitialiser");
        searchPanel.add(txtRecherche);
        searchPanel.add(btnReinitialiser);

        btnExporter = new JButton("Exporter en CSV");

        topTablePanel.add(searchPanel, BorderLayout.CENTER);
        topTablePanel.add(btnExporter, BorderLayout.EAST);

        String[] colonnes = {"Matricule", "Prénom", "Nom", "Né(e) le", "Tuteur", "Téléphone", "Classe"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTalibes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableTalibes);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(topTablePanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER); // Prend tout l'espace restant à droite
    }

    private void initActions() {
        btnAjouter.addActionListener(e -> {
            LocalDate date = extraireDate();
            controller.ajouterTalibe(txtMatricule.getText(), txtPrenom.getText(), txtNom.getText(),
                    date, txtNomTuteur.getText(), txtTelTuteur.getText(), (Classe) comboClasse.getSelectedItem());
        });

        btnModifier.addActionListener(e -> {
            LocalDate date = extraireDate();
            controller.modifierTalibe(txtMatricule.getText(), txtPrenom.getText(), txtNom.getText(),
                    date, txtNomTuteur.getText(), txtTelTuteur.getText(), (Classe) comboClasse.getSelectedItem());
        });

        btnSupprimer.addActionListener(e -> {
            controller.supprimerTalibe(txtMatricule.getText());
        });

        txtRecherche.addCaretListener(e -> {
            controller.rechercherTalibes(txtRecherche.getText());
        });

        btnReinitialiser.addActionListener(e -> {
            txtRecherche.setText("");
            controller.chargerTousLesTalibes();
        });

        btnExporter.addActionListener(e -> {
            ExportUtil.exporterTableEnCSV(tableTalibes, this);
        });

        tableTalibes.getSelectionModel().addListSelectionListener(e -> {
            int row = tableTalibes.getSelectedRow();
            if (row != -1) {
                txtMatricule.setText(tableModel.getValueAt(row, 0).toString());
                txtPrenom.setText(tableModel.getValueAt(row, 1).toString());
                txtNom.setText(tableModel.getValueAt(row, 2).toString());
                txtDateNaissance.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                txtNomTuteur.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
                txtTelTuteur.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");

                String libelleClasseChaine = tableModel.getValueAt(row, 6).toString();
                for (int i = 0; i < comboClasse.getItemCount(); i++) {
                    Classe c = comboClasse.getItemAt(i);
                    if (libelleClasseChaine.equals(c.getLibelle())) {
                        comboClasse.setSelectedIndex(i);
                        break;
                    }
                }

                txtMatricule.setEditable(false);
                btnAjouter.setEnabled(false);
                btnModifier.setEnabled(true);
                btnSupprimer.setEnabled(true);
            }
        });
    }

    private LocalDate extraireDate() {
        String texteDate = txtDateNaissance.getText().trim();
        if (texteDate.isEmpty()) return null;
        try {
            return LocalDate.parse(texteDate, formatter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Le format de la date doit être JJ/MM/AAAA (ex: 15/05/2006).", "Format de date invalide", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void afficherTalibesDansTableau(List<Talibe> talibes) {
        tableModel.setRowCount(0);
        for (Talibe t : talibes) {
            Object[] ligne = {
                    t.getMatricule(),
                    t.getPrenom(),
                    t.getNom(),
                    t.getDateNaissance() != null ? t.getDateNaissance().format(formatter) : "",
                    t.getNomTuteur(),
                    t.getTelephoneTuteur(),
                    t.getClasse() != null ? t.getClasse().getLibelle() : "Aucune"
            };
            tableModel.addRow(ligne);
        }
    }

    public void remplirListeClasses(List<Classe> classes) {
        comboClasse.removeAllItems();
        for (Classe c : classes) {
            comboClasse.addItem(c);
        }
    }

    public void reinitialiserFormulaire() {
        txtMatricule.setText("");
        txtPrenom.setText("");
        txtNom.setText("");
        txtDateNaissance.setText("");
        txtNomTuteur.setText("");
        txtTelTuteur.setText("");
        if (comboClasse.getItemCount() > 0) comboClasse.setSelectedIndex(0);
        txtMatricule.setEditable(true);
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
        tableTalibes.clearSelection();
    }
}