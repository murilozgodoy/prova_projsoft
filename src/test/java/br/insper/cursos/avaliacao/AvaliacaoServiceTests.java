package br.insper.cursos.avaliacao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AvaliacaoServiceTests {

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Avaliacao criarAvaliacao(Long id, String autor, Integer nota) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(id);
        avaliacao.setAutor(autor);
        avaliacao.setConteudo("conteudo de teste");
        avaliacao.setNota(nota);
        return avaliacao;
    }

    @Test
    public void testCriarComSucesso() {
        Avaliacao entrada = criarAvaliacao(99L, "Murilo", 5);

        Mockito.when(avaliacaoRepository.save(Mockito.any(Avaliacao.class)))
                .thenAnswer(invocation -> {
                    Avaliacao a = invocation.getArgument(0);
                    a.setId(1L);
                    return a;
                });

        Avaliacao retorno = avaliacaoService.criar(entrada);

        Assertions.assertEquals(1L, retorno.getId());
        Assertions.assertEquals("Murilo", retorno.getAutor());
        Assertions.assertEquals(5, retorno.getNota());
        Assertions.assertNotNull(retorno.getDataAvaliacao());

        ArgumentCaptor<AvaliacaoEvento> captor = ArgumentCaptor.forClass(AvaliacaoEvento.class);
        Mockito.verify(eventPublisher).publishEvent(captor.capture());
        Assertions.assertEquals(TipoOperacao.CREATE, captor.getValue().getTipoOperacao());
    }

    @Test
    public void testCriarComNotaNula() {
        Avaliacao entrada = criarAvaliacao(null, "Murilo", null);

        ResponseStatusException excecao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.criar(entrada));

        Assertions.assertEquals(400, excecao.getStatusCode().value());
        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    public void testCriarComNotaAbaixoDoLimite() {
        Avaliacao entrada = criarAvaliacao(null, "Murilo", 0);

        ResponseStatusException excecao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.criar(entrada));

        Assertions.assertEquals(400, excecao.getStatusCode().value());
        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testCriarComNotaAcimaDoLimite() {
        Avaliacao entrada = criarAvaliacao(null, "Murilo", 6);

        ResponseStatusException excecao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.criar(entrada));

        Assertions.assertEquals(400, excecao.getStatusCode().value());
        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testListar() {
        Mockito.when(avaliacaoRepository.findAll())
                .thenReturn(List.of(criarAvaliacao(1L, "Murilo", 5),
                        criarAvaliacao(2L, "Joao", 3)));

        List<Avaliacao> retorno = avaliacaoService.listar();

        Assertions.assertEquals(2, retorno.size());
        Mockito.verify(avaliacaoRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testBuscarPorIdComSucesso() {
        Avaliacao avaliacao = criarAvaliacao(1L, "Murilo", 4);
        Mockito.when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        Avaliacao retorno = avaliacaoService.buscarPorId(1L);

        Assertions.assertEquals(1L, retorno.getId());
        Assertions.assertEquals("Murilo", retorno.getAutor());
    }

    @Test
    public void testBuscarPorIdQuandoNaoExiste() {
        Mockito.when(avaliacaoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException excecao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.buscarPorId(99L));

        Assertions.assertEquals(404, excecao.getStatusCode().value());
    }

    @Test
    public void testDeletarComSucesso() {
        Avaliacao avaliacao = criarAvaliacao(1L, "Murilo", 5);
        Mockito.when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        avaliacaoService.deletar(1L);

        Mockito.verify(avaliacaoRepository, Mockito.times(1)).delete(avaliacao);

        ArgumentCaptor<AvaliacaoEvento> captor = ArgumentCaptor.forClass(AvaliacaoEvento.class);
        Mockito.verify(eventPublisher).publishEvent(captor.capture());
        Assertions.assertEquals(TipoOperacao.DELETE, captor.getValue().getTipoOperacao());
    }

    @Test
    public void testDeletarQuandoNaoExiste() {
        Mockito.when(avaliacaoRepository.findById(50L)).thenReturn(Optional.empty());

        ResponseStatusException excecao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.deletar(50L));

        Assertions.assertEquals(404, excecao.getStatusCode().value());
        Mockito.verify(avaliacaoRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishEvent(Mockito.any());
    }
}
