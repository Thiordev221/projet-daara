package sn.thiordev221.controller;

import sn.thiordev221.daara.model.dao.ClasseDao;
import sn.thiordev221.daara.model.dao.MaitreDao;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.daara.model.models.Niveau;
import sn.thiordev221.exception.ClasseDejaExistanteException;
import sn.thiordev221.exception.SuppressionImpossibleException;
import sn.thiordev221.view.ClasseView;

import javax.swing.*;
import java.util.List;

public class ClasseController {

    private final ClasseDao classeDao;
    private final MaitreDao maitreDao;
    private final ClasseView view;

    public ClasseController(ClasseView view) {
        this.classeDao = new ClasseDao();
        this.maitreDao = new MaitreDao();
        this.view = view;
    }

    /**
     * Charge toutes les classes pour le tableau.
     */
    public void chargerToutesLesClasses() {
        List<Classe> liste = classeDao.listerTous();
        view.afficherClassesDansTableau(liste);
    }

    /**
     * Charge les maîtres disponibles pour remplir le JComboBox du formulaire.
     */
    public void chargerMaitresPourFormulaire() {
        List<Maitre> maitres = maitreDao.listerTous();
        view.remplirListeMaitres(maitres);
    }

    /**
     * Insertion d'une nouvelle classe.
     */
    public void ajouterClasse(String code, String libelle, Niveau niveau, Maitre maitre) {
        if (code.trim().isEmpty() || libelle.trim().isEmpty() || niveau == null || maitre == null) {
            JOptionPane.showMessageDialog(view, "Tous les champs (y compris le maître encadrant) sont obligatoires !", "Champs vides", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Classe nouvelle = new Classe(code.trim(), libelle.trim(), niveau, maitre);
            classeDao.inserer(nouvelle);

            JOptionPane.showMessageDialog(view, "Classe créée avec succès !");
            view.reinitialiserFormulaire();
            chargerToutesLesClasses();

        } catch (ClasseDejaExistanteException ex) {
            JOptionPane.showMessageDialog(view, "Le code classe '" + code + "' existe déjà !", "Doublon", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modification d'une classe.
     */
    public void modifierClasse(String code, String libelle, Niveau niveau, Maitre maitre) {
        if (libelle.trim().isEmpty() || niveau == null || maitre == null) {
            JOptionPane.showMessageDialog(view, "Le libellé, le niveau et le maître sont obligatoires !", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Classe classe = new Classe(code, libelle.trim(), niveau, maitre);
        classeDao.modifier(classe);

        JOptionPane.showMessageDialog(view, "Classe mise à jour !");
        view.reinitialiserFormulaire();
        chargerToutesLesClasses();
    }

    /**
     * Suppression d'une classe avec contrôle des talibés.
     */
    public void supprimerClasse(String code) {
        if (code == null || code.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Sélectionnez une classe dans le tableau.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choix = JOptionPane.showConfirmDialog(view, "Supprimer la classe " + code + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            try {
                classeDao.supprimer(code);
                JOptionPane.showMessageDialog(view, "Classe supprimée.");
                view.reinitialiserFormulaire();
                chargerToutesLesClasses();
            } catch (SuppressionImpossibleException ex) {
                // Règle métier : Bloqué si la classe contient des talibés !
                JOptionPane.showMessageDialog(view, ex.getMessage(), "Erreur de suppression", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Recherche par libellé.
     */
    public void rechercherClasses(String texte) {
        if (texte.trim().isEmpty()) {
            chargerToutesLesClasses();
        } else {
            List<Classe> resultats = classeDao.rechercherParLibelle(texte.trim());
            view.afficherClassesDansTableau(resultats);
        }
    }
}
