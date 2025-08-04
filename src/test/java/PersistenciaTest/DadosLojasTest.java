package PersistenciaTest;

import Dados.DadosLojas;
import Model.Loja;
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

@DisplayName("Testes para a classe DadosLojas")
class DadosLojasTest {

    private static final String TEMP_FILE_PATH = "lojas_test.json";
    private DadosLojas dadosLojas;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        // Inicializa uma nova instância de DadosLojas antes de cada teste
        // e garante que o arquivo de teste esteja limpo.
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosLojas = new DadosLojas(TEMP_FILE_PATH);
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
        // Apenas verificamos se o arquivo foi criado e se a lista de lojas está vazia.
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosLojas.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar uma nova loja e salvar no arquivo")
    void testAdicionarLoja() throws PersistenciaException {
        // Cria uma nova loja e a adiciona
        Loja novaLoja = new Loja("Loja Teste", "Rua B, 456", "franquia-id-1");
        dadosLojas.adicionar(novaLoja);

        // Verifica se a loja existe na lista e no mapa
        Optional<Loja> lojaAdicionada = dadosLojas.buscarPorId(novaLoja.getId());
        assertTrue(lojaAdicionada.isPresent());
        assertEquals(novaLoja.getNome(), lojaAdicionada.get().getNome());

        // Verifica se a loja foi persistida corretamente no arquivo
        List<String> linhas = null;
        try {
            linhas = Files.readAllLines(Paths.get(TEMP_FILE_PATH));
        } catch (IOException e) {
            fail("Erro ao ler o arquivo de teste: " + e.getMessage());
        }

        String conteudoArquivo = String.join("\n", linhas);
        assertTrue(conteudoArquivo.contains(novaLoja.getNome()));
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar a loja correta")
    void testBuscarPorIdLojaExistente() throws PersistenciaException {
        Loja loja1 = new Loja("Loja 1", "End 1", "franquia-id-1");
        dadosLojas.adicionar(loja1);

        Optional<Loja> resultado = dadosLojas.buscarPorId(loja1.getId());
        assertTrue(resultado.isPresent());
        assertEquals(loja1.getId(), resultado.get().getId());
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar Optional vazio para ID inexistente")
    void testBuscarPorIdLojaInexistente() {
        Optional<Loja> resultado = dadosLojas.buscarPorId("id-inexistente");
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Teste de atualizar: deve atualizar os dados de uma loja existente")
    void testAtualizarLojaExistente() throws PersistenciaException {
        Loja lojaExistente = new Loja("Antigo Nome", "End Antigo", "franquia-id-1");
        dadosLojas.adicionar(lojaExistente);

        // Cria uma nova instância com o mesmo ID, mas com dados atualizados
        Loja lojaAtualizada = new Loja("Novo Nome", "End Novo", "franquia-id-2");
        lojaAtualizada.setId(lojaExistente.getId()); // Mantém o mesmo ID

        dadosLojas.atualizar(lojaAtualizada);

        Optional<Loja> resultado = dadosLojas.buscarPorId(lojaExistente.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Novo Nome", resultado.get().getNome());
        assertEquals("End Novo", resultado.get().getEndereco());
    }

    @Test
    @DisplayName("Teste de atualizar: deve lançar exceção para loja inexistente")
    void testAtualizarLojaInexistente() {
        Loja lojaInexistente = new Loja("Loja Inexistente", "End", "franquia-id-1");

        // Tenta atualizar uma loja que não foi adicionada
        Assertions.assertThrows(LojaNaoAtualizadaException.class, () -> dadosLojas.atualizar(lojaInexistente));
    }

    @Test
    @DisplayName("Teste de remover: deve remover uma loja existente")
    void testRemoverLojaExistente() throws PersistenciaException {
        Loja lojaParaRemover = new Loja("Loja para remover", "End", "franquia-id-1");
        dadosLojas.adicionar(lojaParaRemover);

        // Verifica que a loja existe antes da remoção
        assertTrue(dadosLojas.buscarPorId(lojaParaRemover.getId()).isPresent());

        dadosLojas.remover(lojaParaRemover.getId());

        // Verifica que a loja não existe mais
        assertFalse(dadosLojas.buscarPorId(lojaParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: deve lançar exceção para ID inexistente")
    void testRemoverLojaInexistente() {
        // Tenta remover um ID que não existe
        Assertions.assertThrows(LojaNaoRemovidaException.class, () -> dadosLojas.remover("id-nao-existe"));
    }

    @Test
    @DisplayName("Teste de listarTodas: deve retornar uma lista com todas as lojas")
    void testListarTodas() throws PersistenciaException {
        Loja l1 = new Loja("L1", "E1", "f1");
        Loja l2 = new Loja("L2", "E2", "f1");
        dadosLojas.adicionar(l1);
        dadosLojas.adicionar(l2);

        List<Loja> todasLojas = dadosLojas.listarTodas();

        assertEquals(2, todasLojas.size());
        assertTrue(todasLojas.stream().anyMatch(l -> l.getId().equals(l1.getId())));
        assertTrue(todasLojas.stream().anyMatch(l -> l.getId().equals(l2.getId())));
    }
}
