package br.insper.cursos.avaliacao;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditoriaObserver {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaObserver(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @EventListener
    public void aoReceberEvento(AvaliacaoEvento evento) {
        AuditoriaEvento registro = new AuditoriaEvento();
        registro.setTimestamp(LocalDateTime.now());
        registro.setTipoOperacao(evento.getTipoOperacao());
        auditoriaRepository.save(registro);
    }
}
