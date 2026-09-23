package br.insper.cursos.curso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public List<Curso> listar(String nome) {
        if (nome == null || nome.isBlank()) {
            return cursoRepository.findByDeletadoFalse();
        }
        return cursoRepository.findByNomeStartingWithIgnoreCaseAndDeletadoFalse(nome);
    }

    public Curso cadastrar(Curso curso) {
        curso.setId(null);
        curso.setDeletado(false);
        return cursoRepository.save(curso);
    }

    public void deletar(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso nao encontrado"));

        curso.setDeletado(true);
        cursoRepository.save(curso);
    }
}