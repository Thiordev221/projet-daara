package sn.thiordev221.daara.model.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.thiordev221.daara.model.models.Maitre;
import sn.thiordev221.exception.MaitreDejaExistantException;
import sn.thiordev221.exception.MaitreIntrouvableException;
import sn.thiordev221.exception.SuppressionImpossibleException;
import sn.thiordev221.util.HibernateUtil;


import java.util.List;
import java.util.Optional;

public class MaitreDao implements Dao<Maitre, String> {

    @Override
    public Maitre inserer(Maitre entity) {
        if (trouver(entity.getMatricule()).isPresent()) {
            throw new MaitreDejaExistantException(entity.getMatricule());
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        }
    }

    @Override
    public Optional<Maitre> trouver(String matricule) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Maitre.class, matricule));
        }
    }

    public Maitre trouverObligatoire(String matricule) {
        return trouver(matricule)
                .orElseThrow(() -> new MaitreIntrouvableException(matricule));
    }

    @Override
    public List<Maitre> listerTous() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Maitre m order by m.nomComplet", Maitre.class).list();
        }
    }

    @Override
    public Optional<Maitre> modifier(Maitre entity) {
        trouverObligatoire(entity.getMatricule());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Maitre merged = session.merge(entity);
            tx.commit();
            return Optional.of(merged);
        }
    }

    @Override
    public boolean supprimer(String matricule) {
        Maitre maitre = trouverObligatoire(matricule);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Règle métier : Vérifier si le maître encadre au moins une classe
            Long count = session.createQuery("select count(c) from Classe c where c.maitre.matricule = :m", Long.class)
                    .setParameter("m", matricule)
                    .uniqueResult();

            if (count > 0) {
                throw new SuppressionImpossibleException("Ce maître encadre au moins une classe et ne peut pas être supprimé.");
            }

            Transaction tx = session.beginTransaction();
            session.remove(session.contains(maitre) ? maitre : session.merge(maitre));
            tx.commit();
            return true;
        }
    }

    // Recherche par critère
    public List<Maitre> rechercherParNom(String txt) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Maitre m where lower(m.nomComplet) like lower(:txt) order by m.nomComplet", Maitre.class)
                    .setParameter("txt", "%" + txt + "%")
                    .list();
        }
    }
}