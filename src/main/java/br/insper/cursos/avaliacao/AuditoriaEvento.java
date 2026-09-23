package br.insper.cursos.avaliacao;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_evento")
@Data
public class AuditoriaEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOperacao tipoOperacao;
}
