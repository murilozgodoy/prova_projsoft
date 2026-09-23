package br.insper.cursos.curso;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "curso")
@Data
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column
    private String descricao;

    @Column
    private Integer cargaHoraria;

    @Column
    private String professor;

    @Column(nullable = false)
    private Boolean deletado = false;
}