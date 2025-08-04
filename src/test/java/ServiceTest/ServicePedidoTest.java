// package para os testes
package ServiceTest;

import Model.FormaDePagamento;
import Model.Franquia;
import Model.Gerente;
import Model.Loja;
import Model.Pedido;
import Model.StatusPedido;
import Model.Vendedor;
import Service.*;
import exception.ValidacaoException;
import exception.persistencia.PersistenciaException;
import exception.usuario.ValidacaoUsuarioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("Testes para a classe ServicePedido")
class ServicePedidoTest {

    private static final String FILE_USUARIO = "usuarios_test_ped.json";
    private static final String FILE_LOJA = "lojas_test_ped.json";
    private static final String FILE_PRODUTOS = "produtos_test_ped.json";
    private static final String FILE_PEDIDOS = "pedidos_test_ped.json";
    private static final String FILE_FRANQUIAS = "franquias_test_ped.json";

    private ServiceManager serviceManager;
    private ServicePedido servicePedido;
    private ServiceLoja serviceLoja;
    private ServiceUsuario serviceUsuario;
    private ServiceFranquia serviceFranquia;
    private serviceEstoque serviceEstoque;

    @BeforeEach
    void setup() throws PersistenciaException, IOException, ValidacaoException, ValidacaoUsuarioException {
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));

        this.serviceManager = new ServiceManager(FILE_USUARIO, FILE_LOJA, FILE_PRODUTOS, FILE_PEDIDOS, FILE_FRANQUIAS);

        this.servicePedido = serviceManager.getServicePedido();
        this.serviceLoja = serviceManager.getServiceLoja();
        this.serviceUsuario = serviceManager.getServiceUsuario();
        this.serviceFranquia = serviceManager.getServiceFranquia();

        Franquia franquia = new Franquia("Franquia Estoque", "Endereco Estoque", "123456789");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Estoque", "gerente.estoque@teste.com", "Senha1233", "12345678901");
        serviceUsuario.adicionar(gerente);
        Loja loja = new Loja("Loja Estoque", "Endereco Estoque", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerente);

        this.serviceEstoque = new serviceEstoque(loja, serviceManager);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
    }

    @Test
    @DisplayName("Deve adicionar um pedido com sucesso")
    void testAdicionarPedidoComSucesso() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Loja loja = serviceLoja.listarTodos().get(0);
        Vendedor vendedor = new Vendedor("Vendedor", "vendedor@test.com", "Senha1233", "11122233344");
        serviceUsuario.adicionar(vendedor);
        serviceLoja.atualizar(loja);

        Map<String, Integer> produtosNoPedido = new HashMap<>();
        produtosNoPedido.put("idProduto1", 2);
        Pedido pedido = new Pedido(loja.getId(), produtosNoPedido, new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);

        Assertions.assertDoesNotThrow(() -> servicePedido.adicionar(pedido));
        Assertions.assertEquals(1, servicePedido.listarTodos().size());
        Assertions.assertEquals(pedido.getId(), servicePedido.getPedidoById(pedido.getId()).getId());
    }

    @Test
    @DisplayName("Não deve adicionar um pedido com ID duplicado")
    void testAdicionarPedidoComIdDuplicado() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Loja loja = serviceLoja.listarTodos().get(0);
        Vendedor vendedor = new Vendedor("Vendedor", "vendedor@test.com", "Senha123", "11122233344");
        serviceUsuario.adicionar(vendedor);
        serviceLoja.atualizar(loja);

        Map<String, Integer> produtosNoPedido = new HashMap<>();
        produtosNoPedido.put("idProduto1", 2);
        Pedido pedido1 = new Pedido(loja.getId(), produtosNoPedido, new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        Pedido pedido2 = new Pedido(loja.getId(), produtosNoPedido, new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        pedido2.setId(pedido1.getId());

        servicePedido.adicionar(pedido1);
        Assertions.assertThrows(PersistenciaException.class, () -> servicePedido.adicionar(pedido2));
    }

    @Test
    @DisplayName("Deve remover um pedido com sucesso")
    void testRemoverPedidoComSucesso() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Loja loja = serviceLoja.listarTodos().get(0);
        Vendedor vendedor = new Vendedor("Vendedor", "vendedor@test.com", "Senha123", "11122233344");
        serviceUsuario.adicionar(vendedor);
        serviceLoja.atualizar(loja);

        Map<String, Integer> produtosNoPedido = new HashMap<>();
        produtosNoPedido.put("idProduto1", 2);
        Pedido pedido = new Pedido(loja.getId(), produtosNoPedido, new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        servicePedido.adicionar(pedido);

        Assertions.assertDoesNotThrow(() -> servicePedido.remover(pedido));
        Assertions.assertTrue(servicePedido.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("Deve listar pedidos por ID de loja")
    void testListarPorIdLoja() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Franquia franquia2 = new Franquia("Franquia2", "End2", "123456789");
        serviceFranquia.adicionar(franquia2);
        Gerente gerente2 = new Gerente("Gerente2", "gerente2@test.com", "Senha123", "99988877766");
        serviceUsuario.adicionar(gerente2);

        Loja loja1 = serviceLoja.listarTodos().get(0);
        Loja loja2 = new Loja("Loja 2", "End 2", franquia2.getId());
        serviceLoja.adicionar(loja2, franquia2, gerente2);

        Vendedor vendedor = new Vendedor("Vendedor", "vendedor@test.com", "Senha123", "11122233344");
        serviceUsuario.adicionar(vendedor);

        Pedido pedido1 = new Pedido(loja1.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        Pedido pedido2 = new Pedido(loja1.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        Pedido pedido3 = new Pedido(loja2.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        servicePedido.adicionar(pedido1);
        servicePedido.adicionar(pedido2);
        servicePedido.adicionar(pedido3);

        List<Pedido> pedidosLoja1 = servicePedido.listarPorIDLoja(loja1.getId());
        Assertions.assertEquals(2, pedidosLoja1.size());

        List<Pedido> pedidosLoja2 = servicePedido.listarPorIDLoja(loja2.getId());
        Assertions.assertEquals(1, pedidosLoja2.size());
    }

    @Test
    @DisplayName("Deve listar pedidos por ID de vendedor")
    void testListarPorVendedor() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Loja loja = serviceLoja.listarTodos().get(0);
        Vendedor vendedor1 = new Vendedor("Vendedor 1", "vendedor1@test.com", "Senha123", "11111111111");
        Vendedor vendedor2 = new Vendedor("Vendedor 2", "vendedor2@test.com", "Senha123", "22222222222");
        serviceUsuario.adicionar(vendedor1);
        serviceUsuario.adicionar(vendedor2);

        Pedido pedido1 = new Pedido(loja.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor1.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        Pedido pedido2 = new Pedido(loja.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor1.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        Pedido pedido3 = new Pedido(loja.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor2.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        servicePedido.adicionar(pedido1);
        servicePedido.adicionar(pedido2);
        servicePedido.adicionar(pedido3);

        List<Pedido> pedidosVendedor1 = servicePedido.listarPorVendedor(vendedor1.getId());
        Assertions.assertEquals(2, pedidosVendedor1.size());

        List<Pedido> pedidosVendedor2 = servicePedido.listarPorVendedor(vendedor2.getId());
        Assertions.assertEquals(1, pedidosVendedor2.size());
    }

    @Test
    @DisplayName("Deve listar pedidos por vendedor, incluindo e excluindo concluídos")
    void testListarPorVendedorComConcluidos() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Loja loja = serviceLoja.listarTodos().get(0);
        Vendedor vendedor = new Vendedor("Vendedor", "vendedor@test.com", "Senha123", "11122233344");
        serviceUsuario.adicionar(vendedor);

        Pedido pedidoPendente = new Pedido(loja.getId(), new HashMap<>(), new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        Pedido pedidoConcluido = new Pedido(loja.getId(), new HashMap<>(), new Date(), StatusPedido.CONCLUIDO, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        servicePedido.adicionar(pedidoPendente);
        servicePedido.adicionar(pedidoConcluido);

        List<Pedido> pedidosIncompletos = servicePedido.listarPorVendedor(vendedor.getId(), false);
        Assertions.assertEquals(1, pedidosIncompletos.size());
        Assertions.assertEquals(StatusPedido.PENDENTE, pedidosIncompletos.get(0).getStatus());

        List<Pedido> todosOsPedidos = servicePedido.listarPorVendedor(vendedor.getId(), true);
        Assertions.assertEquals(2, todosOsPedidos.size());
    }

    @Test
    @DisplayName("Deve atualizar o status de um pedido com sucesso")
    void testAtualizarStatusPedido() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Loja loja = serviceLoja.listarTodos().get(0);
        Vendedor vendedor = new Vendedor("Vendedor", "vendedor@test.com", "Senha123", "11122233344");
        serviceUsuario.adicionar(vendedor);

        Map<String, Integer> produtosNoPedido = new HashMap<>();
        produtosNoPedido.put("idProduto1", 2);
        Pedido pedido = new Pedido(loja.getId(), produtosNoPedido, new Date(), StatusPedido.PENDENTE, vendedor.getId(), BigDecimal.TEN, FormaDePagamento.CARTAO_CREDITO);
        servicePedido.adicionar(pedido);

        Assertions.assertEquals(StatusPedido.PENDENTE, servicePedido.getPedidoById(pedido.getId()).getStatus());

        servicePedido.atualizarStatus(pedido, StatusPedido.ENTREGUE);

        Assertions.assertEquals(StatusPedido.ENTREGUE, servicePedido.getPedidoById(pedido.getId()).getStatus());
    }
}
