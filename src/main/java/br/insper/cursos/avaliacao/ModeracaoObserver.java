package br.insper.cursos.avaliacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ModeracaoObserver {

    private static final Logger log = LoggerFactory.getLogger(ModeracaoObserver.class);

    @EventListener
    public void aoReceberEvento(AvaliacaoEvento evento) {
        if (evento.getTipoOperacao() != TipoOperacao.CREATE) {
            return;
        }
        Integer nota = evento.getAvaliacao().getNota();
        if (nota != null && (nota == 1 || nota == 2)) {
            log.warn("[MODERACAO] Avaliacao negativa recebida: id={}, autor={}, nota={}",
                    evento.getAvaliacao().getId(),
                    evento.getAvaliacao().getAutor(),
                    nota);
        }
    }
}
