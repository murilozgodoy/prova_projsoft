package br.insper.cursos.avaliacao;

import org.springframework.context.ApplicationEvent;

public class AvaliacaoEvento extends ApplicationEvent {

    private final Avaliacao avaliacao;
    private final TipoOperacao tipoOperacao;

    public AvaliacaoEvento(Object source, Avaliacao avaliacao, TipoOperacao tipoOperacao) {
        super(source);
        this.avaliacao = avaliacao;
        this.tipoOperacao = tipoOperacao;
    }

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }
}
