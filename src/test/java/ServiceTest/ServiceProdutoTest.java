package ServiceTest;

import exception.produto.ProdutoException;
import model.Franquia;
import model.Gerente;
import model.Loja;
import model.Produto;
import service.ServiceLoja;
import service.ServiceManager;
import service.ServiceProduto;
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
import java.util.List;

@DisplayName("Testes para a classe ServiceProduto")
class ServiceProdutoTest {

    private static final String FILE_USUARIO = "usuarios_test_sp.json";
    private static final String FILE_LOJA = "lojas_test_sp.json";
    private static final String FILE_PRODUTOS = "produtos_test_sp.json";
    private static final String FILE_PEDIDOS = "pedidos_test_sp.json";
    private static final String FILE_FRANQUIAS = "franquias_test_sp.json";
    private static final String FILE_CLIENTES = "clientes_test_sp.json";

    private ServiceManager serviceManager;
    private ServiceProduto serviceProduto;
    private ServiceLoja serviceLoja;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
        Files.deleteIfExists(Paths.get(FILE_CLIENTES));

        this.serviceManager = new ServiceManager(FILE_USUARIO, FILE_LOJA, FILE_PRODUTOS, FILE_PEDIDOS, FILE_FRANQUIAS, FILE_CLIENTES);

        this.serviceProduto = serviceManager.getServiceProduto();
        this.serviceLoja = serviceManager.getServiceLoja();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
        Files.deleteIfExists(Paths.get(FILE_CLIENTES));
    }

    @Test
    @DisplayName("Deve adicionar um produto com sucesso e atualizar a loja")
    void testAdicionarProdutoComSucesso() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "123456789");
        serviceManager.getServiceFranquia().adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceManager.getServiceUsuario().adicionar(gerente);
        Loja loja = new Loja("Loja Teste", franquia.getId(), "Endereco Loja");
        serviceLoja.adicionar(loja, franquia, gerente);

        Produto produto = new Produto("Produto A", new BigDecimal("10.50"), 100, "Descrição A");

        Assertions.assertDoesNotThrow(() -> serviceProduto.adicionar(produto, loja, serviceLoja));

        List<Produto> produtosDaLoja = serviceProduto.listarPorLoja(loja.getId());
        Assertions.assertEquals(1, produtosDaLoja.size());
        Assertions.assertEquals("Produto A", produtosDaLoja.get(0).getNome());

        Loja lojaAtualizada = serviceLoja.getLojaById(loja.getId());
        Assertions.assertTrue(lojaAtualizada.getIdProdutos().contains(produto.getId()));
    }

    @Test
    @DisplayName("Não deve adicionar um produto com nome duplicado na mesma loja")
    void testAdicionarProdutoComNomeDuplicado() throws PersistenciaException, ValidacaoUsuarioException, ProdutoException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "123456789");
        serviceManager.getServiceFranquia().adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceManager.getServiceUsuario().adicionar(gerente);
        Loja loja = new Loja("Loja Teste", franquia.getId(), "Endereco Loja");
        serviceLoja.adicionar(loja, franquia, gerente);

        Produto produto1 = new Produto("Produto Duplicado", new BigDecimal("10.50"), 10, "Descrição 1");
        Produto produto2 = new Produto("Produto Duplicado", new BigDecimal("20.00"), 5, "Descrição 2");

        serviceProduto.adicionar(produto1, loja, serviceLoja);

        Assertions.assertThrows(ProdutoException.class, () -> serviceProduto.adicionar(produto2, loja, serviceLoja));
        Assertions.assertEquals(1, serviceProduto.listarPorLoja(loja.getId()).size());
    }

    @Test
    @DisplayName("Deve remover um produto e atualizar a loja")
    void testRemoverProdutoComSucesso() throws PersistenciaException, ValidacaoUsuarioException,ProdutoException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "123456789");
        serviceManager.getServiceFranquia().adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceManager.getServiceUsuario().adicionar(gerente);
        Loja loja = new Loja("Loja Teste","Endereco Loja", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerente);

        Produto produto = new Produto("Produto para remover", new BigDecimal("5.00"), 50, "Descrição");
        serviceProduto.adicionar(produto, loja, serviceLoja);

        Assertions.assertEquals(1, serviceProduto.listarTodos().size());
        Assertions.assertTrue(serviceLoja.getLojaById(loja.getId()).getIdProdutos().contains(produto.getId()));

        Assertions.assertDoesNotThrow(() -> serviceProduto.remover(produto, loja, serviceLoja));

        Assertions.assertTrue(serviceProduto.listarTodos().isEmpty());
        Loja lojaAtualizada = serviceLoja.getLojaById(loja.getId());
        Assertions.assertFalse(lojaAtualizada.getIdProdutos().contains(produto.getId()));
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void testAtualizarProdutoComSucesso() throws PersistenciaException, ValidacaoUsuarioException,ProdutoException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "123456789");
        serviceManager.getServiceFranquia().adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceManager.getServiceUsuario().adicionar(gerente);
        Loja loja = new Loja("Loja Teste", "Endereco Loja", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerente);

        Produto produto = new Produto("Produto Antigo", new BigDecimal("10.00"), 10, "Descrição Antiga");
        serviceProduto.adicionar(produto, loja, serviceLoja);

        Produto produtoAtualizado = new Produto("Produto Novo", new BigDecimal("15.50"), 20, "Descrição Nova");
        produtoAtualizado.setId(produto.getId());

        Assertions.assertDoesNotThrow(() -> serviceProduto.atualizarProduto(produtoAtualizado));

        Produto resultado = serviceProduto.getProduto(produto.getId());
        Assertions.assertEquals("Produto Novo", resultado.getNome());
        Assertions.assertEquals("Descrição Nova", resultado.getDescricao());
        Assertions.assertEquals(new BigDecimal("15.50"), resultado.getPreco());
        Assertions.assertEquals(20, resultado.getEstoque());
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void testListarTodos() throws PersistenciaException, ValidacaoUsuarioException, ProdutoException{
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "123456789");
        serviceManager.getServiceFranquia().adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceManager.getServiceUsuario().adicionar(gerente);
        Loja loja1 = new Loja("Loja 1", "Endereco Loja 1", franquia.getId());
        Loja loja2 = new Loja("Loja 2", "Endereco Loja 2", franquia.getId());
        serviceLoja.adicionar(loja1, franquia, gerente);
        serviceLoja.adicionar(loja2, franquia, gerente);

        Produto p1 = new Produto("Produto A", new BigDecimal("10.00"), 10, "Desc A");
        Produto p2 = new Produto("Produto B", new BigDecimal("20.00"), 20, "Desc B");
        Produto p3 = new Produto("Produto C", new BigDecimal("30.00"), 30, "Desc C");

        serviceProduto.adicionar(p1, loja1, serviceLoja);
        serviceProduto.adicionar(p2, loja1, serviceLoja);
        serviceProduto.adicionar(p3, loja2, serviceLoja);

        List<Produto> todosProdutos = serviceProduto.listarTodos();
        Assertions.assertEquals(3, todosProdutos.size());
    }

    @Test
    @DisplayName("Deve retornar um produto por ID")
    void testGetProdutoPorId() throws PersistenciaException, ValidacaoUsuarioException ,ProdutoException{
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "123456789");
        serviceManager.getServiceFranquia().adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceManager.getServiceUsuario().adicionar(gerente);
        Loja loja = new Loja("Loja Teste", "Endereco Loja", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerente);

        Produto produto = new Produto("Produto ID", new BigDecimal("50.00"), 5, "Descrição");
        serviceProduto.adicionar(produto, loja, serviceLoja);

        Produto produtoEncontrado = serviceProduto.getProduto(produto.getId());
        Assertions.assertNotNull(produtoEncontrado);
        Assertions.assertEquals(produto.getId(), produtoEncontrado.getId());
    }
}
