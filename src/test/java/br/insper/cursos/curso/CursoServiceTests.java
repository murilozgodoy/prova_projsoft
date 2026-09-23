package br.insper.cursos.curso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CursoServiceTests {

    @InjectMocks
    private CursoService cursoService;

    @Mock
    private CursoRepository cursoRepository;

    private Curso criarCurso(Long id, String nome) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNome(nome);
        curso.setDescricao("descricao");
        curso.setCargaHoraria(40);
        curso.setProfessor("Eduardo");
        curso.setDeletado(false);
        return curso;
    }

    @Test
    public void testListarQuandoNomeEhNulo() {
        Mockito.when(cursoRepository.findByDeletadoFalse())
                .thenReturn(List.of(criarCurso(1L, "Java")));

        List<Curso> retorno = cursoService.listar(null);

        Assertions.assertEquals(1, retorno.size());
        Assertions.assertEquals("Java", retorno.get(0).getNome());
        Mockito.verify(cursoRepository, Mockito.times(1)).findByDeletadoFalse();
    }

    @Test
    public void testListarQuandoNomeEhVazio() {
        Mockito.when(cursoRepository.findByDeletadoFalse())
                .thenReturn(List.of(criarCurso(1L, "Java"), criarCurso(2L, "Python")));

        List<Curso> retorno = cursoService.listar("   ");

        Assertions.assertEquals(2, retorno.size());
        Mockito.verify(cursoRepository, Mockito.times(1)).findByDeletadoFalse();
    }

    @Test
    public void testListarComFiltroPorNome() {
        Mockito.when(cursoRepository.findByNomeStartingWithIgnoreCaseAndDeletadoFalse("Ja"))
                .thenReturn(List.of(criarCurso(1L, "Java")));

        List<Curso> retorno = cursoService.listar("Ja");

        Assertions.assertEquals(1, retorno.size());
        Assertions.assertEquals("Java", retorno.get(0).getNome());
        Mockito.verify(cursoRepository, Mockito.never()).findByDeletadoFalse();
    }

    @Test
    public void testCadastrarCurso() {
        Curso curso = criarCurso(99L, "Docker");
        curso.setDeletado(true);

        Mockito.when(cursoRepository.save(Mockito.any(Curso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Curso retorno = cursoService.cadastrar(curso);

        Assertions.assertNull(retorno.getId());
        Assertions.assertFalse(retorno.getDeletado());
        Assertions.assertEquals("Docker", retorno.getNome());
    }

    @Test
    public void testDeletarCursoComSucesso() {
        Curso curso = criarCurso(1L, "Java");
        Mockito.when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        cursoService.deletar(1L);

        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        Mockito.verify(cursoRepository, Mockito.times(1)).save(captor.capture());
        Assertions.assertTrue(captor.getValue().getDeletado());
    }

    @Test
    public void testDeletarCursoQuandoNaoExiste() {
        Mockito.when(cursoRepository.findById(50L)).thenReturn(Optional.empty());

        ResponseStatusException excecao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> cursoService.deletar(50L));

        Assertions.assertEquals(404, excecao.getStatusCode().value());
        Mockito.verify(cursoRepository, Mockito.never()).save(Mockito.any());
    }
}