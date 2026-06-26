package sn.thiordev221.daara.model.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.thiordev221.daara.model.models.Progression;
import sn.thiordev221.daara.model.models.Talibe;
import sn.thiordev221.exception.ProgressionIntrouvableException;
import sn.thiordev221.exception.ProgressionInvalideException;
import sn.thiordev221.util.HibernateUtil;


import java.util.List;
import java.util.Optional;

public class ProgressionDao implements Dao<Progression, Long> {

    private void validerProgression(Progression p) {
        if (p.getTalibe() == null || p.getSourate() == null || p.getSourate().trim().isEmpty() || p.getNombreVersets() < 0) {
            throw new ProgressionInvalideException("Données de progression invalides (Le nombre de versets doit être >= 0 et la sourate renseignée).");
        }
    }

    @Override
    public Progression inserer(Progression entity) {
        validerProgression(entity);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        }
    }

    @Override
    public Optional<Progression> trouver(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Progression.class, id));
        }
    }

    public Progression trouverObligatoire(Long id) {
        return trouver(id)
                .orElseThrow(() -> new ProgressionIntrouvableException(id.toString()));
    }

    @Override
    public List<Progression> listerTous() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Progression p order by p.dateEvaluation desc", Progression.class).list();
        }
    }

    @Override
    public Optional<Progression> modifier(Progression entity) {
        if (entity.getId() == null) {
            throw new ProgressionInvalideException("Impossible de modifier une progression sans ID.");
        }
        trouverObligatoire(entity.getId());
        validerProgression(entity);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Progression merged = session.merge(entity);
            tx.commit();
            return Optional.of(merged);
        }
    }

    @Override
    public boolean supprimer(Long id) {
        Progression progression = trouverObligatoire(id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(session.contains(progression) ? progression : session.merge(progression));
            tx.commit();
            return true;
        }
    }

    // Recherche par critère (filtrer par talibé)
    public List<Progression> listerParTalibe(String matriculeTalibe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Progression p where p.talibe.matricule = :m order by p.dateEvaluation desc", Progression.class)
                    .setParameter("m", matriculeTalibe)
                    .list();
        }
    }

    public List<Progression> rechercherParTalibe(Talibe talibe){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Progression p where p.talibe = :m order by p.dateEvaluation desc", Progression.class)
                    .setParameter("m", talibe)
                    .list();
        }
    }
}
