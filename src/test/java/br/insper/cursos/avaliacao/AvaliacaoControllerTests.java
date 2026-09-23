package br.insper.cursos.avaliacao;

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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AvaliacaoControllerTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("avaliacoes_test")
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
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void limparBase() {
        avaliacaoRepository.deleteAll();
        auditoriaRepository.deleteAll();
    }

    @Test
    public void testPostAvaliacao() throws Exception {
        Avaliacao nova = new Avaliacao();
        nova.setAutor("Murilo");
        nova.setConteudo("Curso excelente, aprendi bastante");
        nova.setNota(5);

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nova)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.autor").value("Murilo"))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.dataAvaliacao").isNotEmpty());

        Assertions.assertEquals(1, avaliacaoRepository.count());
        List<AuditoriaEvento> auditoria = auditoriaRepository.findAll();
        Assertions.assertEquals(1, auditoria.size());
        Assertions.assertEquals(TipoOperacao.CREATE, auditoria.get(0).getTipoOperacao());
        Assertions.assertNotNull(auditoria.get(0).getTimestamp());
    }
}
