package sn.thiordev221.daara.model.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.exception.TalibeDejaExistantException;
import sn.thiordev221.exception.TalibeIntrouvableException;
import sn.thiordev221.util.HibernateUtil;


import java.util.List;
import java.util.Optional;

public class TalibeDao implements Dao<Talibe, String> {

    @Override
    public Talibe inserer(Talibe entity) {
        if (trouver(entity.getMatricule()).isPresent()) {
            throw new TalibeDejaExistantException(entity.getMatricule());
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        }
    }

    @Override
    public Optional<Talibe> trouver(String matricule) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Talibe.class, matricule));
        }
    }

    public Talibe trouverObligatoire(String matricule) {
        return trouver(matricule)
                .orElseThrow(() -> new TalibeIntrouvableException(matricule));
    }

    @Override
    public List<Talibe> listerTous() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Talibe t order by t.nom, t.prenom", Talibe.class).list();
        }
    }

    @Override
    public Optional<Talibe> modifier(Talibe entity) {
        trouverObligatoire(entity.getMatricule());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Talibe merged = session.merge(entity);
            tx.commit();
            return Optional.of(merged);
        }
    }

    @Override
    public boolean supprimer(String matricule) {
        Talibe talibe = trouverObligatoire(matricule);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Règle métier : Supprimer un talibé supprime aussi ses progressions (cascade logicielle)
            session.createMutationQuery("delete from Progression p where p.talibe.matricule = :m")
                    .setParameter("m", matricule)
                    .executeUpdate();

            session.remove(session.contains(talibe) ? talibe : session.merge(talibe));
            tx.commit();
            return true;
        }
    }

    // Recherches par critères
    public List<Talibe> rechercherParNom(String txt) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Talibe t where lower(t.nom) like lower(:txt) or lower(t.prenom) like lower(:txt) order by t.nom", Talibe.class)
                    .setParameter("txt", "%" + txt + "%")
                    .list();
        }
    }

    public List<Talibe> listerParClasse(String codeClasse) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Talibe t where t.classe.code = :code order by t.nom", Talibe.class)
                    .setParameter("code", codeClasse)
                    .list();
        }
    }
}