package sn.thiordev221.daara.model.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.thiordev221.daara.model.models.Classe;
import sn.thiordev221.exception.ClasseDejaExistanteException;
import sn.thiordev221.exception.ClasseIntrouvableException;
import sn.thiordev221.exception.SuppressionImpossibleException;
import sn.thiordev221.util.HibernateUtil;


import java.util.List;
import java.util.Optional;

public class ClasseDao implements Dao<Classe, String> {

    @Override
    public Classe inserer(Classe entity) {
        if (trouver(entity.getCode()).isPresent()) {
            throw new ClasseDejaExistanteException(entity.getCode());
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        }
    }

    @Override
    public Optional<Classe> trouver(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Classe.class, code));
        }
    }

    public Classe trouverObligatoire(String code) {
        return trouver(code)
                .orElseThrow(() -> new ClasseIntrouvableException(code));
    }

    @Override
    public List<Classe> listerTous() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Classe c order by c.libelle", Classe.class).list();
        }
    }

    @Override
    public Optional<Classe> modifier(Classe entity) {
        trouverObligatoire(entity.getCode());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Classe merged = session.merge(entity);
            tx.commit();
            return Optional.of(merged);
        }
    }

    @Override
    public boolean supprimer(String code) {
        Classe classe = trouverObligatoire(code);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Règle métier : Vérifier si la classe contient des talibés
            Long count = session.createQuery("select count(t) from Talibe t where t.classe.code = :code", Long.class)
                    .setParameter("code", code)
                    .uniqueResult();

            if (count > 0) {
                throw new SuppressionImpossibleException("Cette classe contient des talibés et ne peut pas être supprimée.");
            }

            Transaction tx = session.beginTransaction();
            session.remove(session.contains(classe) ? classe : session.merge(classe));
            tx.commit();
            return true;
        }
    }

    // Recherche par critère
    public List<Classe> rechercherParLibelle(String txt) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Classe c where lower(c.libelle) like lower(:txt) order by c.libelle", Classe.class)
                    .setParameter("txt", "%" + txt + "%")
                    .list();
        }
    }
}
