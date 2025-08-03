package test.PersistenciaTest;

import Dados.DadosFranquias;
import Dados.DadosPedidos;
import Model.Franquia;
import Model.Pedido;
import exception.persistencia.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DadosPedidosTest {

    private static final String TEST_FILE_PATH = "pedidos_test.json";
    private DadosPedidos dadosPedidos;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        // Garante que o arquivo não existe antes de cada teste
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        dadosPedidos = new DadosPedidos(TEST_FILE_PATH);
    }

    @AfterEach
    void cleanup() {
        // Limpa o arquivo de teste após a execução de cada teste
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testAdicionarEListarPedido() throws PersistenciaException {
        // Cria um novo pedido com os campos atualizados
        Map<String, Integer> produtos = new HashMap<>();
        produtos.put("PRODUTO_1", 2);
        produtos.put("PRODUTO_2", 1);
        Pedido pedido = new Pedido("ID_LOJA_123", produtos, new Date(), "Em Processamento");

        // Adiciona o pedido
        dadosPedidos.adicionar(pedido);

        // Verifica se o mapa contém o novo pedido
        Map<String, Pedido> pedidos = dadosPedidos.getPedidosMap();
        assertEquals(1, pedidos.size());
        assertTrue(pedidos.containsKey(pedido.getId()));
        assertEquals("Em Processamento", pedidos.get(pedido.getId()).getStatus());
        assertEquals(2, pedidos.get(pedido.getId()).getProdutosNoPedido().get("PRODUTO_1"));
    }

    @Test
    void testBuscarPedidoPorId() throws PersistenciaException {
        // Cria e adiciona um pedido
        Map<String, Integer> produtos = new HashMap<>();
        produtos.put("PRODUTO_1", 2);
        Pedido pedido = new Pedido("ID_LOJA_123", produtos, new Date(), "Em Processamento");
        dadosPedidos.adicionar(pedido);

        // Busca o pedido por ID
        Optional<Pedido> pedidoEncontrado = dadosPedidos.buscarPorId(pedido.getId());
        assertTrue(pedidoEncontrado.isPresent());
        assertEquals(pedido.getId(), pedidoEncontrado.get().getId());

        // Busca por um ID inexistente
        Optional<Pedido> pedidoNaoEncontrado = dadosPedidos.buscarPorId(UUID.randomUUID().toString());
        assertFalse(pedidoNaoEncontrado.isPresent());
    }

    @Test
    void testAtualizarPedidoExistente() throws PersistenciaException {
        // Cria e adiciona um pedido inicial
        Map<String, Integer> produtosOriginais = new HashMap<>();
        produtosOriginais.put("PRODUTO_1", 2);
        Pedido pedidoOriginal = new Pedido("ID_LOJA_123", produtosOriginais, new Date(), "Em Processamento");
        dadosPedidos.adicionar(pedidoOriginal);

        // Cria um pedido com o mesmo ID, mas com dados atualizados
        Map<String, Integer> produtosAtualizados = new HashMap<>();
        produtosAtualizados.put("PRODUTO_1", 3);
        produtosAtualizados.put("PRODUTO_3", 1);
        Pedido pedidoAtualizado = new Pedido("ID_LOJA_123", produtosAtualizados, new Date(), "Entregue");
        pedidoAtualizado.setId(pedidoOriginal.getId());

        // Atualiza o pedido
        dadosPedidos.atualizar(pedidoAtualizado);

        // Verifica se o pedido foi atualizado
        Map<String, Pedido> pedidos = dadosPedidos.getPedidosMap();
        assertEquals(1, pedidos.size());
        assertEquals("Entregue", pedidos.get(pedidoOriginal.getId()).getStatus());
        assertEquals(3, pedidos.get(pedidoOriginal.getId()).getProdutosNoPedido().get("PRODUTO_1"));
        assertTrue(pedidos.get(pedidoOriginal.getId()).getProdutosNoPedido().containsKey("PRODUTO_3"));
    }

    @Test
    void testAtualizarPedidoNaoExistenteNaoLancaExcecao() {
        // Tenta atualizar um pedido que não existe, o método não deve lançar exceção
        Map<String, Integer> produtos = new HashMap<>();
        produtos.put("PRODUTO_999", 1);
        Pedido pedido = new Pedido("ID_LOJA_999", produtos, new Date(), "Novo");
        assertDoesNotThrow(() -> dadosPedidos.atualizar(pedido));

        // Verifica se o mapa continua vazio
        assertTrue(dadosPedidos.getPedidosMap().isEmpty());
    }

    @Test
    void testRemoverPedidoExistente() throws PersistenciaException {
        // Cria e adiciona um pedido
        Map<String, Integer> produtos = new HashMap<>();
        produtos.put("PRODUTO_1", 1);
        Pedido pedido = new Pedido("ID_LOJA_123", produtos, new Date(), "Em Processamento");
        dadosPedidos.adicionar(pedido);

        // Remove o pedido
        dadosPedidos.remover(pedido.getId());

        // Verifica se o pedido foi removido
        Map<String, Pedido> pedidos = dadosPedidos.getPedidosMap();
        assertTrue(pedidos.isEmpty());
    }

    @Test
    void testRemoverPedidoNaoExistenteNaoLancaExcecao() {
        // Tenta remover um pedido com um ID inexistente, o método não deve lançar exceção
        String idInexistente = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> dadosPedidos.remover(idInexistente));

        // Verifica se o mapa continua vazio
        assertTrue(dadosPedidos.getPedidosMap().isEmpty());
    }

    @Test
    void testCarregarArquivoCorrompidoLancaExcecao() throws IOException {
        // Cria um arquivo com JSON inválido para simular corrupção
        FileWriter writer = new FileWriter(TEST_FILE_PATH);
        writer.write("isso não é um json valido");
        writer.close();

        // Tenta carregar o arquivo, o que deve lançar uma exceção
        assertThrows(LojaNaoCarregadaException.class, () -> new DadosPedidos(TEST_FILE_PATH));
    }

    @Test
    void testInicializarComArquivoVazio() throws PersistenciaException {
        // O setup já cria um arquivo vazio
        dadosPedidos = new DadosPedidos(TEST_FILE_PATH);

        // O mapa deve estar vazio
        assertTrue(dadosPedidos.getPedidosMap().isEmpty());
    }
}
