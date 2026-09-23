package br.insper.cursos.avaliacao;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao")
@Data
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false, length = 2000)
    private String conteudo;

    @Column(nullable = false)
    private Integer nota;

    @Column
    private String professor;

    @Column(nullable = false)
    private LocalDateTime dataAvaliacao;
}