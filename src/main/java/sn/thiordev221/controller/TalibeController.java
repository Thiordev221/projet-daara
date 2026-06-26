package sn.thiordev221.controller;

import sn.thiordev221.daara.model.dao.ClasseDao;
import sn.thiordev221.daara.model.dao.TalibeDao;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.exception.TalibeDejaExistantException;
import sn.thiordev221.view.TalibeView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class TalibeController {

    private final TalibeDao talibeDao;
    private final ClasseDao classeDao;
    private final TalibeView view;

    public TalibeController(TalibeView view) {
        this.talibeDao = new TalibeDao();
        this.classeDao = new ClasseDao();
        this.view = view;
    }

    /**
     * Charge tous les talibés et demande à la vue de rafraîchir son tableau.
     */
    public void chargerTousLesTalibes() {
        List<Talibe> liste = talibeDao.listerTous();
        view.afficherTalibesDansTableau(liste);
    }

    /**
     * Charge les classes de la Daara pour alimenter le JComboBox du formulaire.
     */
    public void chargerClassesPourFormulaire() {
        List<Classe> classes = classeDao.listerTous();
        view.remplirListeClasses(classes);
    }

    /**
     * Ajout d'un nouveau talibé.
     */
    public void ajouterTalibe(String matricule, String prenom, String nom, LocalDate dateNaissance, String nomTuteur, String telTuteur, Classe classe) {
        if (matricule.trim().isEmpty() || prenom.trim().isEmpty() || nom.trim().isEmpty() || classe == null) {
            JOptionPane.showMessageDialog(view, "Le matricule, le prénom, le nom et la classe sont obligatoires !", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Talibe nouveau = new Talibe(matricule.trim(), prenom.trim(), nom.trim(), dateNaissance, nomTuteur.trim(), telTuteur.trim(), classe);
            talibeDao.inserer(nouveau);

            JOptionPane.showMessageDialog(view, "Talibé inscrit avec succès !");
            view.reinitialiserFormulaire();
            chargerTousLesTalibes();

        } catch (TalibeDejaExistantException ex) {
            JOptionPane.showMessageDialog(view, "Le matricule '" + matricule + "' est déjà attribué à un autre talibé !", "Doublon", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modification d'un talibé existant.
     */
    public void modifierTalibe(String matricule, String prenom, String nom, LocalDate dateNaissance, String nomTuteur, String telTuteur, Classe classe) {
        if (prenom.trim().isEmpty() || nom.trim().isEmpty() || classe == null) {
            JOptionPane.showMessageDialog(view, "Le prénom, le nom et la classe ne peuvent pas être vides !", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Talibe talibe = new Talibe(matricule, prenom.trim(), nom.trim(), dateNaissance, nomTuteur.trim(), telTuteur.trim(), classe);
        talibeDao.modifier(talibe);

        JOptionPane.showMessageDialog(view, "Fiche du talibé mise à jour !");
        view.reinitialiserFormulaire();
        chargerTousLesTalibes();
    }

    /**
     * Suppression d'un talibé (en cascade via Hibernate).
     */
    public void supprimerTalibe(String matricule) {
        if (matricule == null || matricule.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Veuillez sélectionner un talibé dans le tableau.", "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choix = JOptionPane.showConfirmDialog(view, "Supprimer le talibé " + matricule + " ?\n(Attention : Cela supprimera également tout son historique de progressions)", "Confirmation de suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            talibeDao.supprimer(matricule);
            JOptionPane.showMessageDialog(view, "Talibé supprimé définitivement.");
            view.reinitialiserFormulaire();
            chargerTousLesTalibes();
        }
    }

    /**
     * Recherche de talibés par critère textuel (Prénom ou Nom).
     */
    public void rechercherTalibes(String texte) {
        if (texte.trim().isEmpty()) {
            chargerTousLesTalibes();
        } else {
            List<Talibe> resultats = talibeDao.rechercherParNom(texte.trim());
            view.afficherTalibesDansTableau(resultats);
        }
    }
}
