package br.insper.cursos.curso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CursoControllerTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("cursos_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void limparBase() {
        cursoRepository.deleteAll();
    }

    private Curso salvarCurso(String nome, boolean deletado) {
        Curso curso = new Curso();
        curso.setNome(nome);
        curso.setDescricao("descricao de " + nome);
        curso.setCargaHoraria(40);
        curso.setProfessor("Eduardo");
        curso.setDeletado(deletado);
        return cursoRepository.save(curso);
    }

    @Test
    public void testPostCursos() throws Exception {
        Curso novo = new Curso();
        novo.setNome("Projeto de Software");
        novo.setDescricao("Docker, CI/CD e deploy");
        novo.setCargaHoraria(80);
        novo.setProfessor("Eduardo");

        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Projeto de Software"))
                .andExpect(jsonPath("$.deletado").value(false));

        Assertions.assertEquals(1, cursoRepository.count());
    }

    @Test
    public void testGetCursosSemFiltroEComFiltro() throws Exception {
        salvarCurso("Java Basico", false);
        salvarCurso("Java Avancado", false);
        salvarCurso("Python", false);
        salvarCurso("Javascript Deletado", true);

        // sem filtro: nao retorna o curso deletado
        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // com filtro startWith
        mockMvc.perform(get("/cursos").param("nome", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testDeleteCursoEhLogico() throws Exception {
        Curso curso = salvarCurso("Curso para deletar", false);

        mockMvc.perform(delete("/cursos/" + curso.getId()))
                .andExpect(status().isNoContent());

        // sumiu da listagem
        mockMvc.perform(get("/cursos"))
                .andExpect(jsonPath("$.length()").value(0));

        // mas continua no banco com deletado = true
        Optional<Curso> noBanco = cursoRepository.findById(curso.getId());
        Assertions.assertTrue(noBanco.isPresent());
        Assertions.assertTrue(noBanco.get().getDeletado());
    }
}