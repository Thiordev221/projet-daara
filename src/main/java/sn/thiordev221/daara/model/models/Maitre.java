package sn.thiordev221.daara.model.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maitres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maitre {

    @Id
    @Column(length = 50, unique = true)
    private String matricule; // Clé primaire saisie

    @Column(name = "nom_complet", nullable = false)
    private String nomComplet;

    @Column(length = 20)
    private String telephone;
}
