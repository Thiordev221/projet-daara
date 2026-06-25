package sn.thiordev221.daara.model.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {
    T inserer(T entity);
    Optional<T> trouver(ID id);     // RECHERCHE UNIQUE par clé
    List<T> listerTous();
    Optional<T>  modifier(T entity);
    boolean      supprimer(ID id);
}
