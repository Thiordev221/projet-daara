package sn.thiordev221.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.daara.model.models.Progression;
import sn.thiordev221.daara.model.models.Talibe;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Création de la configuration à partir du fichier hibernate.cfg.xml
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

            // Enregistrement explicite et obligatoire des entités du projet Daara
            configuration.addAnnotatedClass(Maitre.class);
            configuration.addAnnotatedClass(Classe.class);
            configuration.addAnnotatedClass(Talibe.class);
            configuration.addAnnotatedClass(Progression.class);

            // Construction de la SessionFactory unique
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("La création initiale de la SessionFactory a échoué : " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Permet de récupérer l'instance unique de la SessionFactory.
     * @return la SessionFactory Hibernate
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Permet de fermer proprement les connexions à la base de données
     * lors de la fermeture de l'application Swing.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            getSessionFactory().close();
        }
    }
}
