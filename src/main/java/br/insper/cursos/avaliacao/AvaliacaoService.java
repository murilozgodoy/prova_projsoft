package br.insper.cursos.avaliacao;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.eventPublisher = eventPublisher;
    }

    public Avaliacao criar(Avaliacao avaliacao) {
        if (avaliacao.getNota() == null || avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota deve estar entre 1 e 5");
        }
        avaliacao.setId(null);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        eventPublisher.publishEvent(new AvaliacaoEvento(this, salva, TipoOperacao.CREATE));
        return salva;
    }

    public List<Avaliacao> listar() {
        return avaliacaoRepository.findAll();
    }

    public Avaliacao buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Avaliacao nao encontrada"));
    }

    public void deletar(Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Avaliacao nao encontrada"));
        avaliacaoRepository.delete(avaliacao);
        eventPublisher.publishEvent(new AvaliacaoEvento(this, avaliacao, TipoOperacao.DELETE));
    }
}
