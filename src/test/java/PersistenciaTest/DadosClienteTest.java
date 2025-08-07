package PersistenciaTest;

import repository.DadosCliente;
import model.Cliente;
import exception.persistencia.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe DadosCliente")
class DadosClienteTest {

    private static final String TEMP_FILE_PATH = "clientes_test.json";
    private DadosCliente dadosCliente;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosCliente = new DadosCliente(TEMP_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    @DisplayName("Teste de inicialização: arquivo não existe, deve ser criado vazio")
    void testInicializacaoArquivoNaoExistente() {
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosCliente.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar um novo cliente e salvar no arquivo")
    void testAdicionarCliente() throws PersistenciaException {
        Cliente novoCliente = new Cliente("Cliente Teste", "11122233344");
        dadosCliente.adicionar(novoCliente);

        Optional<Cliente> clienteAdicionado = dadosCliente.buscarPorId(novoCliente.getId());
        assertTrue(clienteAdicionado.isPresent());
        assertEquals(novoCliente.getNome(), clienteAdicionado.get().getNome());

        try {
            String conteudoArquivo = Files.readString(Paths.get(TEMP_FILE_PATH));
            assertTrue(conteudoArquivo.contains(novoCliente.getId()));
            assertTrue(conteudoArquivo.contains("11122233344"));
        } catch (IOException e) {
            fail("Erro ao ler o arquivo de teste: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar o cliente correto")
    void testBuscarPorIdClienteExistente() throws PersistenciaException {
        Cliente cliente1 = new Cliente("Cliente 1", "12345678901");
        dadosCliente.adicionar(cliente1);

        Optional<Cliente> resultado = dadosCliente.buscarPorId(cliente1.getId());
        assertTrue(resultado.isPresent());
        assertEquals(cliente1.getId(), resultado.get().getId());
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar Optional vazio para ID inexistente")
    void testBuscarPorIdClienteInexistente() {
        Optional<Cliente> resultado = dadosCliente.buscarPorId("id-inexistente");
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Teste de atualizar: deve atualizar os dados de um cliente existente")
    void testAtualizarClienteExistente() throws PersistenciaException {
        Cliente clienteExistente = new Cliente("Nome Antigo", "11111111111");
        dadosCliente.adicionar(clienteExistente);

        Cliente clienteAtualizado = new Cliente("Nome Novo", "22222222222");
        clienteAtualizado.setId(clienteExistente.getId()); // Mantém o mesmo ID

        dadosCliente.atualizar(clienteAtualizado);

        Optional<Cliente> resultado = dadosCliente.buscarPorId(clienteExistente.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Nome Novo", resultado.get().getNome());
        assertEquals("22222222222", resultado.get().getCpf());
    }

    @Test
    @DisplayName("Teste de atualizar: deve lançar exceção para cliente inexistente")
    void testAtualizarClienteInexistente() {
        Cliente clienteInexistente = new Cliente("Cliente Inexistente", "00000000000");
        // Assumindo que a implementação de DadosCliente lança exceção, seguindo o padrão de DadosLojas.
        Assertions.assertThrows(LojaNaoAtualizadaException.class, () -> dadosCliente.atualizar(clienteInexistente));
    }

    @Test
    @DisplayName("Teste de remover: deve remover um cliente existente")
    void testRemoverClienteExistente() throws PersistenciaException {
        Cliente clienteParaRemover = new Cliente("Cliente para remover", "99988877766");
        dadosCliente.adicionar(clienteParaRemover);

        assertTrue(dadosCliente.buscarPorId(clienteParaRemover.getId()).isPresent());
        dadosCliente.remover(clienteParaRemover.getId());
        assertFalse(dadosCliente.buscarPorId(clienteParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: deve lançar exceção para ID inexistente")
    void testRemoverClienteInexistente() {
        // Assumindo que a implementação de DadosCliente lança exceção, seguindo o padrão de DadosLojas.
        Assertions.assertThrows(LojaNaoRemovidaException.class, () -> dadosCliente.remover("id-nao-existe"));
    }

    @Test
    @DisplayName("Teste de listarTodas: deve retornar uma lista com todos os clientes")
    void testListarTodas() throws PersistenciaException {
        Cliente c1 = new Cliente("C1", "111");
        Cliente c2 = new Cliente("C2", "222");
        dadosCliente.adicionar(c1);
        dadosCliente.adicionar(c2);

        List<Cliente> todosClientes = dadosCliente.listarTodas();

        assertEquals(2, todosClientes.size());
        assertTrue(todosClientes.stream().anyMatch(c -> c.getId().equals(c1.getId())));
        assertTrue(todosClientes.stream().anyMatch(c -> c.getId().equals(c2.getId())));
    }
}