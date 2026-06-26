package sn.thiordev221.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportUtil {

    /**
     * Exporte les données d'un JTable au format CSV.
     * @param table Le JTable contenant les données affichées.
     * @param parentComponent Le composant parent pour centrer la boîte de dialogue de sauvegarde.
     */
    public static void exporterTableEnCSV(JTable table, JComponent parentComponent) {
        // 1. Validation : Vérifier si le tableau contient des données
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parentComponent, "Le tableau est vide. Rien à exporter !", "Export impossible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Ouvrir une boîte de dialogue pour choisir l'emplacement du fichier (JFileChooser)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le fichier CSV");

        // Nom de fichier proposé par défaut
        fileChooser.setSelectedFile(new File("export_daara.csv"));

        int choixUtilisateur = fileChooser.showSaveDialog(parentComponent);

        if (choixUtilisateur == JFileChooser.APPROVE_OPTION) {
            File fichierSelectionne = fileChooser.getSelectedFile();

            // S'assurer que le fichier se termine bien par l'extension .csv
            String chemin = fichierSelectionne.getAbsolutePath();
            if (!chemin.toLowerCase().endsWith(".csv")) {
                fichierSelectionne = new File(chemin + ".csv");
            }

            // 3. Écriture des données dans le fichier
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichierSelectionne))) {
                TableModel model = table.getModel();
                int nbColonnes = model.getColumnCount();
                int nbLignes = model.getRowCount();

                // a. Écriture des entêtes (Noms des colonnes)
                for (int i = 0; i < nbColonnes; i++) {
                    writer.write(model.getColumnName(i));
                    if (i < nbColonnes - 1) {
                        writer.write(";"); // Utilisation du point-virgule, le standard d'Excel en Europe/Afrique
                    }
                }
                writer.newLine();

                // b. Écriture des lignes de données
                for (int ligne = 0; ligne < nbLignes; ligne++) {
                    for (int col = 0; col < nbColonnes; col++) {
                        Object valeur = model.getValueAt(ligne, col);

                        if (valeur != null) {
                            // On remplace les points-virgules présents dans le texte pour ne pas casser le format CSV
                            String texteClean = valeur.toString().replace(";", ",");
                            writer.write(texteClean);
                        } else {
                            writer.write(""); // Case vide si la valeur en base est nulle
                        }

                        if (col < nbColonnes - 1) {
                            writer.write(";");
                        }
                    }
                    writer.newLine();
                }

                // Message de succès de l'opération
                JOptionPane.showMessageDialog(parentComponent, "Le fichier a été exporté avec succès !\nEmplacement : " + fichierSelectionne.getAbsolutePath(), "Export Réussi", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, "Une erreur est survenue lors de la création du fichier :\n" + e.getMessage(), "Erreur d'écriture", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
