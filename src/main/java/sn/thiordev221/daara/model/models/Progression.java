package sn.thiordev221.daara.model.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "progressions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progression {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id; // Clé primaire auto-générée

    @ManyToOne(optional = false)
    @JoinColumn(name = "talibe_matricule", nullable = false)
    private Talibe talibe; // Talibé évalué obligatoire

    @Column(nullable = false)
    private String sourate;

    @Column(name = "nombre_versets", nullable = false)
    private int nombreVersets; // Doit être >= 0 (à valider dans le contrôleur/DAO)

    @Column(name = "date_evaluation")
    private LocalDate dateEvaluation;

    private String appreciation;
}
