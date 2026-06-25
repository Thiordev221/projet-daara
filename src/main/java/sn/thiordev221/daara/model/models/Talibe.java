package sn.thiordev221.daara.model.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "talibes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Talibe {

    @Id
    @Column (length = 50, unique = true)
    private String matricule; // Clé primaire saisie

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "nom_tuteur")
    private String nomTuteur;

    @Column(name = "telephone_tuteur", length = 20)
    private String telephoneTuteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classe_code", nullable = false)
    private Classe classe; // Classe d'affectation obligatoire
}
