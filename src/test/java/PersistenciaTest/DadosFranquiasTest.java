package PersistenciaTest;
import Dados.DadosFranquias;
import Model.Franquia;
import exception.persistencia.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe DadosFranquias")
class DadosFranquiasTest {

    private static final String TEMP_FILE_PATH = "franquias_test.json";
    private DadosFranquias dadosFranquias;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        // Inicializa uma nova instância de DadosFranquias antes de cada teste
        // e garante que o arquivo de teste esteja limpo.
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosFranquias = new DadosFranquias(TEMP_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpa o arquivo de teste após cada execução
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    @DisplayName("Teste de inicialização: arquivo não existe, deve ser criado vazio")
    void testInicializacaoArquivoNaoExistente() {
        // O setup já lida com a criação do arquivo e instanciação da classe.
        // Apenas verificamos se o arquivo foi criado e se a lista de franquias está vazia.
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosFranquias.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar uma nova franquia e salvar no arquivo")
    void testAdicionarFranquia() throws PersistenciaException {
        // Cria uma nova franquia e a adiciona
        Franquia novaFranquia = new Franquia("Franquia Teste", "Rua A, 123", "1111-1111");
        dadosFranquias.adicionar(novaFranquia);

        // Verifica se a franquia existe na lista e no mapa
        Optional<Franquia> franquiaAdicionada = dadosFranquias.buscarPorId(novaFranquia.getId());
        assertTrue(franquiaAdicionada.isPresent());
        assertEquals(novaFranquia.getNome(), franquiaAdicionada.get().getNome());

        // Verifica se a franquia foi persistida corretamente no arquivo
        List<String> linhas = null;
        try {
            linhas = Files.readAllLines(Paths.get(TEMP_FILE_PATH));
        } catch (IOException e) {
            fail("Erro ao ler o arquivo de teste: " + e.getMessage());
        }

        String conteudoArquivo = String.join("\n", linhas);
        assertTrue(conteudoArquivo.contains(novaFranquia.getNome()));
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar a franquia correta")
    void testBuscarPorIdFranquiaExistente() throws PersistenciaException {
        Franquia franquia1 = new Franquia("Franquia 1", "End 1", "111");
        dadosFranquias.adicionar(franquia1);

        Optional<Franquia> resultado = dadosFranquias.buscarPorId(franquia1.getId());
        assertTrue(resultado.isPresent());
        assertEquals(franquia1.getId(), resultado.get().getId());
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar Optional vazio para ID inexistente")
    void testBuscarPorIdFranquiaInexistente() {
        Optional<Franquia> resultado = dadosFranquias.buscarPorId("id-inexistente");
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Teste de atualizar: deve atualizar os dados de uma franquia existente")
    void testAtualizarFranquiaExistente() throws PersistenciaException {
        Franquia franquiaExistente = new Franquia("Antigo Nome", "End Antigo", "111");
        dadosFranquias.adicionar(franquiaExistente);

        // Cria uma nova instância com o mesmo ID, mas com dados atualizados
        Franquia franquiaAtualizada = new Franquia("Novo Nome", "End Novo", "222");
        franquiaAtualizada.setId(franquiaExistente.getId()); // Mantém o mesmo ID

        dadosFranquias.atualizar(franquiaAtualizada);

        Optional<Franquia> resultado = dadosFranquias.buscarPorId(franquiaExistente.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Novo Nome", resultado.get().getNome());
        assertEquals("End Novo", resultado.get().getEndereco());
    }

    @Test
    @DisplayName("Teste de atualizar: deve lançar exceção para franquia inexistente")
    void testAtualizarFranquiaInexistente() {
        Franquia franquiaInexistente = new Franquia("Franquia Inexistente", "End", "333");

        // Tenta atualizar uma franquia que não foi adicionada
        Assertions.assertThrows(LojaNaoAtualizadaException.class, () -> dadosFranquias.atualizar(franquiaInexistente));
    }

    @Test
    @DisplayName("Teste de remover: deve remover uma franquia existente")
    void testRemoverFranquiaExistente() throws PersistenciaException {
        Franquia franquiaParaRemover = new Franquia("Franquia para remover", "End", "444");
        dadosFranquias.adicionar(franquiaParaRemover);

        // Verifica que a franquia existe antes da remoção
        assertTrue(dadosFranquias.buscarPorId(franquiaParaRemover.getId()).isPresent());

        dadosFranquias.remover(franquiaParaRemover.getId());

        // Verifica que a franquia não existe mais
        assertFalse(dadosFranquias.buscarPorId(franquiaParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: deve lançar exceção para ID inexistente")
    void testRemoverFranquiaInexistente() {
        // Tenta remover um ID que não existe
        Assertions.assertThrows(LojaNaoRemovidaException.class, () -> dadosFranquias.remover("id-nao-existe"));
    }

    @Test
    @DisplayName("Teste de listarTodas: deve retornar uma lista com todas as franquias")
    void testListarTodas() throws PersistenciaException {
        Franquia f1 = new Franquia("F1", "E1", "1");
        Franquia f2 = new Franquia("F2", "E2", "2");
        dadosFranquias.adicionar(f1);
        dadosFranquias.adicionar(f2);

        List<Franquia> todasFranquias = dadosFranquias.listarTodas();

        assertEquals(2, todasFranquias.size());
        assertTrue(todasFranquias.stream().anyMatch(f -> f.getId().equals(f1.getId())));
        assertTrue(todasFranquias.stream().anyMatch(f -> f.getId().equals(f2.getId())));
    }
}
