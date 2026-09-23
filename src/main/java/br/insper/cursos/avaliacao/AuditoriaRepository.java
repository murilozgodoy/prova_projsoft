package br.insper.cursos.avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AuditoriaRepository extends JpaRepository <AuditoriaEvento, Long>{
}
