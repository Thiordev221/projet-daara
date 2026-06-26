package sn.thiordev221.view;

import sn.thiordev221.controller.ProgressionController;
import sn.thiordev221.daara.model.models.Progression;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProgressionView extends JPanel {

    private JComboBox<Talibe> comboTalibeForm;
    private JComboBox<Object> comboTalibeFiltre;
    private JTextField txtSourate, txtVersets, txtDate, txtAppreciation;
    private JButton btnAjouter, btnSupprimer, btnReinitialiserFiltre, btnExporter;
    private JTable tableProgressions;
    private DefaultTableModel tableModel;

    private Long idSelectionne = null;
    private final ProgressionController controller;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProgressionView() {
        this.controller = new ProgressionController(this);
        setLayout(new BorderLayout(10, 10));

        initFormulairePanel();
        initTablePanel();
        initActions();

        controller.chargerToutesLesProgressions();
        controller.chargerTalibesPourFormulaire();
    }

    private void initFormulairePanel() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nouvelle Évaluation (Suivi)"));

        comboTalibeForm = new JComboBox<>();
        txtSourate = new JTextField();
        txtVersets = new JTextField();
        txtDate = new JTextField();
        txtDate.setText(LocalDate.now().format(formatter));
        txtAppreciation = new JTextField();

        formPanel.add(new JLabel(" Sélectionner Talibé :"));
        formPanel.add(comboTalibeForm);
        formPanel.add(new JLabel(" Sourate évaluée :"));
        formPanel.add(txtSourate);
        formPanel.add(new JLabel(" Nombre de versets :"));
        formPanel.add(txtVersets);
        formPanel.add(new JLabel(" Date (JJ/MM/AAAA) :"));
        formPanel.add(txtDate);
        formPanel.add(new JLabel(" Appréciation / Note :"));
        formPanel.add(txtAppreciation);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAjouter = new JButton("Enregistrer");
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setEnabled(false);

        btnPanel.add(btnAjouter);
        btnPanel.add(btnSupprimer);

        // SOLUTION ICI : On fige la largeur pour bloquer l'étirement des JComboBox/JTextField
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(350, 0)); // Largeur figée à 350px
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
    }

    private void initTablePanel() {
        JPanel topTablePanel = new JPanel(new BorderLayout(5, 5));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboTalibeFiltre = new JComboBox<>();
        btnReinitialiserFiltre = new JButton("Afficher Tous");
        filterPanel.add(new JLabel("Filtrer par élève :"));
        filterPanel.add(comboTalibeFiltre);
        filterPanel.add(btnReinitialiserFiltre);

        btnExporter = new JButton("Exporter en CSV");

        topTablePanel.add(filterPanel, BorderLayout.CENTER);
        topTablePanel.add(btnExporter, BorderLayout.EAST);

        String[] colonnes = {"ID", "Talibé", "Sourate", "Versets", "Date Éval", "Appréciation"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableProgressions = new JTable(tableModel);

        tableProgressions.getColumnModel().getColumn(0).setMinWidth(0);
        tableProgressions.getColumnModel().getColumn(0).setMaxWidth(0);
        tableProgressions.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(tableProgressions);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(topTablePanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void initActions() {
        btnAjouter.addActionListener(e -> {
            LocalDate date = null;
            try {
                date = LocalDate.parse(txtDate.getText().trim(), formatter);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide (JJ/MM/AAAA).", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.ajouterProgression((Talibe) comboTalibeForm.getSelectedItem(), txtSourate.getText(),
                    txtVersets.getText(), date, txtAppreciation.getText());
        });

        btnSupprimer.addActionListener(e -> {
            controller.supprimerProgression(idSelectionne);
        });

        comboTalibeFiltre.addActionListener(e -> {
            Object item = comboTalibeFiltre.getSelectedItem();
            if (item instanceof Talibe) {
                controller.filtrerParTalibe((Talibe) item);
            }
        });

        btnReinitialiserFiltre.addActionListener(e -> {
            comboTalibeFiltre.setSelectedIndex(0);
            controller.chargerToutesLesProgressions();
        });

        btnExporter.addActionListener(e -> {
            ExportUtil.exporterTableEnCSV(tableProgressions, this);
        });

        tableProgressions.getSelectionModel().addListSelectionListener(e -> {
            int row = tableProgressions.getSelectedRow();
            if (row != -1) {
                idSelectionne = Long.parseLong(tableModel.getValueAt(row, 0).toString());
                btnSupprimer.setEnabled(true);
            }
        });
    }

    public void afficherProgressionsDansTableau(List<Progression> progressions) {
        tableModel.setRowCount(0);
        for (Progression p : progressions) {
            Object[] ligne = {
                    p.getId(),
                    p.getTalibe() != null ? (p.getTalibe().getPrenom() + " " + p.getTalibe().getNom()) : "Inconnu",
                    p.getSourate(),
                    p.getNombreVersets(),
                    p.getDateEvaluation() != null ? p.getDateEvaluation().format(formatter) : "",
                    p.getAppreciation()
            };
            tableModel.addRow(ligne);
        }
    }

    public void remplirListesTalibes(List<Talibe> talibes) {
        comboTalibeForm.removeAllItems();
        for (Talibe t : talibes) {
            comboTalibeForm.addItem(t);
        }

        comboTalibeFiltre.removeAllItems();
        comboTalibeFiltre.addItem("-- Tous les Talibés --");
        for (Talibe t : talibes) {
            comboTalibeFiltre.addItem(t);
        }
    }

    public void reinitialiserFormulaire() {
        txtSourate.setText("");
        txtVersets.setText("");
        txtDate.setText(LocalDate.now().format(formatter));
        txtAppreciation.setText("");
        if (comboTalibeForm.getItemCount() > 0) comboTalibeForm.setSelectedIndex(0);
        btnSupprimer.setEnabled(false);
        idSelectionne = null;
        tableProgressions.clearSelection();
    }
}