package sn.thiordev221.controller;

import sn.thiordev221.daara.model.dao.ProgressionDao;
import sn.thiordev221.daara.model.dao.TalibeDao;
import sn.thiordev221.daara.model.models.Progression;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.exception.ProgressionInvalideException;
import sn.thiordev221.view.ProgressionView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class ProgressionController {

    private final ProgressionDao progressionDao;
    private final TalibeDao talibeDao;
    private final ProgressionView view;

    public ProgressionController(ProgressionView view) {
        this.progressionDao = new ProgressionDao();
        this.talibeDao = new TalibeDao();
        this.view = view;
    }

    /**
     * Charge toutes les progressions sans filtre.
     */
    public void chargerToutesLesProgressions() {
        List<Progression> liste = progressionDao.listerTous();
        view.afficherProgressionsDansTableau(liste);
    }

    /**
     * Charge la liste des talibés pour remplir les JComboBox (Formulaire et Filtre).
     */
    public void chargerTalibesPourFormulaire() {
        List<Talibe> talibes = talibeDao.listerTous();
        view.remplirListesTalibes(talibes);
    }

    /**
     * Ajoute un suivi d'évaluation avec validation des règles métier.
     */
    public void ajouterProgression(Talibe talibe, String sourate, String nombreVersetsStr, LocalDate date, String appreciation) {
        // Validation des champs obligatoires d'UI
        if (talibe == null || sourate.trim().isEmpty() || nombreVersetsStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Le talibé, la sourate et le nombre de versets son obligatoires !", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int nbVersets;
            try {
                nbVersets = Integer.parseInt(nombreVersetsStr.trim());
            } catch (NumberFormatException e) {
                throw new ProgressionInvalideException("Le nombre de versets doit être un entier valide.");
            }

            // Règle métier de l'énoncé : sourate non vide et versets >= 0
            if (nbVersets < 0) {
                throw new ProgressionInvalideException("Le nombre de versets mémorisés ne peut pas être négatif !");
            }

            Progression p = new Progression(null, talibe, sourate.trim(), nbVersets, date, appreciation.trim());
            progressionDao.inserer(p);

            JOptionPane.showMessageDialog(view, "Progression enregistrée !");
            view.reinitialiserFormulaire();
            chargerToutesLesProgressions();

        } catch (ProgressionInvalideException ex) {
            // Le contrôleur attrape l'exception métier et l'affiche proprement à l'écran
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Règle Métier Violée", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filtre le tableau pour n'afficher que les évaluations du talibé sélectionné.
     */
    public void filtrerParTalibe(Talibe talibe) {
        if (talibe == null) {
            chargerToutesLesProgressions();
        } else {
            // Utilisation d'une requête personnalisée HQL (ex: "from Progression p where p.talibe = :talibe")
            List<Progression> filtrées = progressionDao.rechercherParTalibe(talibe);
            view.afficherProgressionsDansTableau(filtrées);
        }
    }

    /**
     * Supprime un enregistrement d'évaluation.
     */
    public void supprimerProgression(Long id) {
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Sélectionnez une ligne dans le tableau.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choix = JOptionPane.showConfirmDialog(view, "Supprimer cette évaluation ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            progressionDao.supprimer(id);
            JOptionPane.showMessageDialog(view, "Évaluation supprimée.");
            view.reinitialiserFormulaire();
            chargerToutesLesProgressions();
        }
    }
}
