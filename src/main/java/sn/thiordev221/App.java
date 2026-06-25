package sn.thiordev221;

import org.hibernate.SessionFactory;
import sn.thiordev221.daara.model.dao.ClasseDao;
import sn.thiordev221.daara.model.dao.MaitreDao;
import sn.thiordev221.daara.model.dao.TalibeDao;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.daara.model.models.Niveau;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.util.HibernateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
//
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== DÉBUT DU TEST CRUD TALIBÉ ===");

        // 1. Initialisation des DAOs
        MaitreDao maitreDao = new MaitreDao();
        ClasseDao classeDao = new ClasseDao();
        TalibeDao talibeDao = new TalibeDao();

        try {
            // ==========================================
            // PRE-REQUIS : Création du Maître et de la Classe
            // ==========================================
            System.out.println("\n--- 0. Préparation de l'environnement ---");

            Maitre serigne = new Maitre("M001", "Serigne Fallou Mbacké", "771234567");
            maitreDao.inserer(serigne);
            System.out.println("Maître inséré avec succès.");

            Classe halqa = new Classe("CL-DEB", "Halqa des Débutants", Niveau.DEBUTANT, serigne);
            classeDao.inserer(halqa);
            System.out.println("Classe insérée avec succès.");

            // ==========================================
            // CRÉER (CREATE)
            // ==========================================
            System.out.println("\n--- 1. Test de l'Insertion (CREATE) ---");
            Talibe nouveauTalibe = new Talibe(
                    "T0001",
                    "Abdoulaye",
                    "Thior",
                    LocalDate.of(2006, 5, 15),
                    "Moussa Thior",
                    "779876543",
                    halqa
            );

            talibeDao.inserer(nouveauTalibe);
            System.out.println("Talibé " + nouveauTalibe.getPrenom() + " inséré avec succès !");

            // ==========================================
            // LIRE (READ - Unique)
            // ==========================================
            System.out.println("\n--- 2. Test de la Recherche unique (READ) ---");
            Optional<Talibe> talibeTrouve = talibeDao.trouver("T0001");

            if (talibeTrouve.isPresent()) {
                Talibe t = talibeTrouve.get();
                System.out.println("Talibé trouvé ! Nom complet : " + t.getPrenom() + " " + t.getNom());
                System.out.println("Classe associée : " + t.getClasse().getLibelle());
            } else {
                System.out.println("Erreur : Le talibé n'a pas été trouvé.");
            }

            // ==========================================
            // METTRE À JOUR (UPDATE)
            // ==========================================
            System.out.println("\n--- 3. Test de la Modification (UPDATE) ---");
            if (talibeTrouve.isPresent()) {
                Talibe tAModifier = talibeTrouve.get();
                tAModifier.setTelephoneTuteur("760001122"); // Changement de numéro du tuteur
                tAModifier.setNomTuteur("Modou Thior");     // Changement de nom du tuteur

                talibeDao.modifier(tAModifier);
                System.out.println("Données du talibé mises à jour avec succès.");
            }

            // ==========================================
            // LIRE TOUS / RECHERCHER (READ - Liste)
            // ==========================================
            System.out.println("\n--- 4. Test du Listage et Recherche par critère ---");
            System.out.println("Liste complète des talibés :");
            List<Talibe> tousLesTalibes = talibeDao.listerTous();
            for (Talibe t : tousLesTalibes) {
                System.out.println(" - [" + t.getMatricule() + "] " + t.getPrenom() + " " + t.getNom() + " (Tuteur: " + t.getNomTuteur() + " - " + t.getTelephoneTuteur() + ")");
            }

            System.out.println("\nRecherche par critère contenant 'Abdou' :");
            List<Talibe> resultatsRecherche = talibeDao.rechercherParNom("Abdou");
            for (Talibe t : resultatsRecherche) {
                System.out.println(" - Trouvé : " + t.getPrenom() + " " + t.getNom());
            }

            // ==========================================
            // SUPPRIMER (DELETE)
            // ==========================================
            System.out.println("\n--- 5. Test de la Suppression (DELETE) ---");
            boolean supprime = talibeDao.supprimer("T0001");
            if (supprime) {
                System.out.println("Talibé T0001 supprimé avec succès de la base de données.");
            }

            // Vérification après suppression
            Optional<Talibe> verif = talibeDao.trouver("T0001");
            System.out.println("Le talibé existe-t-il encore après suppression ? " + (verif.isPresent() ? "Oui" : "Non"));

        } catch (Exception e) {
            System.err.println("Une exception est survenue pendant l'exécution du CRUD :");
            e.printStackTrace();
        } finally {
            // Toujours fermer proprement la SessionFactory à la fin de l'application
            System.out.println("\nFermeture d'Hibernate...");
            HibernateUtil.shutdown();
            System.out.println("=== FIN DU TEST ===");
        }
    }

}
