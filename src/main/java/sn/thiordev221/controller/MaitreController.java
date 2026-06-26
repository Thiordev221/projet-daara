package sn.thiordev221.controller;

import sn.thiordev221.daara.model.dao.MaitreDao;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.exception.MaitreDejaExistantException;
import sn.thiordev221.exception.SuppressionImpossibleException;
import sn.thiordev221.view.MaitreView;

import javax.swing.*;
import java.util.List;

public class MaitreController {

    private final MaitreDao maitreDao;
    private final MaitreView view;

    // Le constructeur lie le contrôleur à sa vue
    public MaitreController(MaitreView view) {
        this.maitreDao = new MaitreDao();
        this.view = view;
    }

    /**
     * Charge tous les maîtres depuis la base et demande à la vue de rafraîchir son tableau.
     */
    public void chargerTousLesMaitres() {
        List<Maitre> liste = maitreDao.listerTous();
        view.afficherMaitresDansTableau(liste);
    }

    /**
     * Action d'ajout d'un maître après validation des champs.
     */
    public void ajouterMaitre(String matricule, String nomComplet, String telephone) {
        // En L2, on valide toujours les saisies avant d'envoyer au DAO !
        if (matricule.trim().isEmpty() || nomComplet.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Le matricule et le nom complet sont obligatoires !", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Maitre nouveau = new Maitre(matricule.trim(), nomComplet.trim(), telephone.trim());
            maitreDao.inserer(nouveau);

            JOptionPane.showMessageDialog(view, "Maître ajouté avec succès !");
            view.reinitialiserFormulaire();
            chargerTousLesMaitres(); // On rafraîchit le tableau automatiquement

        } catch (MaitreDejaExistantException ex) {
            JOptionPane.showMessageDialog(view, "Erreur : Un maître avec le matricule " + matricule + " existe déjà !", "Doublon", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Action de modification d'un maître existant.
     */
    public void modifierMaitre(String matricule, String nomComplet, String telephone) {
        if (nomComplet.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Le nom complet ne peut pas être vide !", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Maitre maitre = new Maitre(matricule, nomComplet.trim(), telephone.trim());
        maitreDao.modifier(maitre);

        JOptionPane.showMessageDialog(view, "Informations modifiées avec succès !");
        view.reinitialiserFormulaire();
        chargerTousLesMaitres();
    }

    /**
     * Action de suppression avec capture de l'exception de contrainte d'intégrité.
     */
    public void supprimerMaitre(String matricule) {
        if (matricule == null || matricule.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Veuillez sélectionner un maître dans le tableau.", "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Demande de confirmation à l'utilisateur (Bonne pratique UX demandée dans l'énoncé)
        int reponse = JOptionPane.showConfirmDialog(view, "Voulez-vous vraiment supprimer le maître " + matricule + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            try {
                maitreDao.supprimer(matricule);
                JOptionPane.showMessageDialog(view, "Maître supprimé.");
                view.reinitialiserFormulaire();
                chargerTousLesMaitres();
            } catch (SuppressionImpossibleException ex) {
                // C'est ici qu'on gère la règle : Interdit de supprimer si le maître a des classes !
                JOptionPane.showMessageDialog(view, ex.getMessage(), "Suppression Impossible", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Action de filtrage en temps réel ou sur clic du bouton Rechercher.
     */
    public void rechercherMaitres(String texte) {
        if (texte.trim().isEmpty()) {
            chargerTousLesMaitres();
        } else {
            List<Maitre> resultats = maitreDao.rechercherParNom(texte.trim());
            view.afficherMaitresDansTableau(resultats);
        }
    }
}
