package br.insper.cursos.curso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByDeletadoFalse();

    List<Curso> findByNomeStartingWithIgnoreCaseAndDeletadoFalse(String nome);
}