package sn.thiordev221.daara.model.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classe {

    @Id
    @Column(length = 50, unique = true)
    private String code; // Clé primaire saisie

    @Column(nullable = false)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Niveau niveau;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maitre_matricule", nullable = false)
    private Maitre maitre; // Maître obligatoire qui encadre la classe
}
