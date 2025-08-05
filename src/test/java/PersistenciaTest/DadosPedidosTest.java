package PersistenciaTest;
import repository.DadosPedidos;
import model.FormaDePagamento;
import model.Pedido;
import model.StatusPedido;
import exception.persistencia.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe DadosPedidos")
class DadosPedidosTest {

    private static final String TEMP_FILE_PATH = "pedidos_test.json";
    private DadosPedidos dadosPedidos;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosPedidos = new DadosPedidos(TEMP_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    @DisplayName("Teste de inicialização: arquivo não existe, deve ser criado vazio")
    void testInicializacaoArquivoNaoExistente() {
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosPedidos.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar um novo pedido e salvar no arquivo")
    void testAdicionarPedido() throws PersistenciaException {
        Pedido novoPedido = new Pedido("loja-1", new HashMap<>(), new Date(), StatusPedido.PENDENTE, "vendedor-1", BigDecimal.valueOf(100.00), FormaDePagamento.PIX);
        dadosPedidos.adicionar(novoPedido);
        Optional<Pedido> pedidoAdicionado = dadosPedidos.buscarPorId(novoPedido.getId());
        assertTrue(pedidoAdicionado.isPresent());
        assertEquals(novoPedido.getIdLoja(), pedidoAdicionado.get().getIdLoja());
        try {
            String conteudoArquivo = Files.readString(Paths.get(TEMP_FILE_PATH));
            assertTrue(conteudoArquivo.contains(novoPedido.getId()));
        } catch (IOException e) {
            fail("Erro ao ler o arquivo de teste: " + e.getMessage());
        }
    }

//    @Test
//    @DisplayName("Teste de buscarPorId: deve retornar o pedido correto")
//    void testBuscarPorIdPedidoExistente() throws PersistenciaException {
//        Pedido pedido1 = new Pedido("loja-1", new HashMap<>(), new Date(), StatusPedido.APROVADO, "vendedor-1", BigDecimal.valueOf(50.00), FormaDePagamento.CREDITO);
//        dadosPedidos.adicionar(pedido1);
//
//        Optional<Pedido> resultado = dadosPedidos.buscarPorId(pedido1.getId());
//        assertTrue(resultado.isPresent());
//        assertEquals(pedido1.getId(), resultado.get().getId());
//    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar Optional vazio para ID inexistente")
    void testBuscarPorIdPedidoInexistente() {
        Optional<Pedido> resultado = dadosPedidos.buscarPorId("id-inexistente");
        assertFalse(resultado.isPresent());
    }

//    @Test
//    @DisplayName("Teste de atualizar: deve atualizar os dados de um pedido existente")
//    void testAtualizarPedidoExistente() throws PersistenciaException {
//        Pedido pedidoExistente = new Pedido("loja-1", new HashMap<>(), new Date(), StatusPedido.PENDENTE, "vendedor-1", BigDecimal.valueOf(10.00), FormaDePagamento.PIX);
//        dadosPedidos.adicionar(pedidoExistente);
//
//        Pedido pedidoAtualizado = new Pedido();
//        pedidoAtualizado.setId(pedidoExistente.getId());
//        pedidoAtualizado.setStatus(StatusPedido.APROVADO);
//        pedidoAtualizado.setPrecoTotal(BigDecimal.valueOf(200.00));
//
//        dadosPedidos.atualizar(pedidoAtualizado);
//
//        Optional<Pedido> resultado = dadosPedidos.buscarPorId(pedidoExistente.getId());
//        assertTrue(resultado.isPresent());
//        assertEquals(StatusPedido.APROVADO, resultado.get().getStatus());
//        assertEquals(BigDecimal.valueOf(200.00), resultado.get().getPrecoTotal());
//    }

//    @Test
//    @DisplayName("Teste de atualizar: não deve lançar exceção para pedido inexistente e não alterar o estado")
//    void testAtualizarPedidoInexistente() {
//        Pedido pedidoInexistente = new Pedido();
//        pedidoInexistente.setId("id-nao-existe");
//
//        // O método 'atualizar' na sua classe DadosPedidos não lança exceção para IDs inexistentes,
//        // apenas imprime no System.err.
//        Assertions.assertDoesNotThrow(() -> dadosPedidos.atualizar(pedidoInexistente));
//        assertTrue(dadosPedidos.listarTodas().isEmpty());
//    }

    @Test
    @DisplayName("Teste de remover: deve remover um pedido existente")
    void testRemoverPedidoExistente() throws PersistenciaException {
        Pedido pedidoParaRemover = new Pedido("loja-1", new HashMap<>(), new Date(), StatusPedido.PENDENTE, "vendedor-1", BigDecimal.valueOf(50.00), FormaDePagamento.PIX);
        dadosPedidos.adicionar(pedidoParaRemover);
        assertTrue(dadosPedidos.buscarPorId(pedidoParaRemover.getId()).isPresent());

        dadosPedidos.remover(pedidoParaRemover.getId());

        assertFalse(dadosPedidos.buscarPorId(pedidoParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: não deve lançar exceção para ID inexistente")
    void testRemoverPedidoInexistente() {
        Assertions.assertDoesNotThrow(() -> dadosPedidos.remover("id-nao-existe"));
    }

    @Test
    @DisplayName("Teste de listarTodas: deve retornar uma lista com todos os pedidos")
    void testListarTodas() throws PersistenciaException {
        Pedido p1 = new Pedido("loja-1", new HashMap<>(), new Date(), StatusPedido.PENDENTE, "vendedor-1", BigDecimal.valueOf(10.00), FormaDePagamento.PIX);
        Pedido p2 = new Pedido("loja-2", new HashMap<>(), new Date(), StatusPedido.PENDENTE, "vendedor-2", BigDecimal.valueOf(20.00), FormaDePagamento.PIX);
        dadosPedidos.adicionar(p1);
        dadosPedidos.adicionar(p2);

        List<Pedido> todosPedidos = dadosPedidos.listarTodas();

        assertEquals(2, todosPedidos.size());
        assertTrue(todosPedidos.stream().anyMatch(p -> p.getId().equals(p1.getId())));
        assertTrue(todosPedidos.stream().anyMatch(p -> p.getId().equals(p2.getId())));
    }
}

