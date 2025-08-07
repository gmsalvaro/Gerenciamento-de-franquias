//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package PersistenciaTest;

import repository.DadosProdutos;
import model.Produto;
import exception.persistencia.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe DadosProdutos")
class DadosProdutosTest {

    private static final String TEMP_FILE_PATH = "produtos_test.json";
    private DadosProdutos dadosProdutos;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosProdutos = new DadosProdutos(TEMP_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    @DisplayName("Teste de inicialização: arquivo não existe, deve ser criado vazio")
    void testInicializacaoArquivoNaoExistente() {
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosProdutos.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar um novo produto e salvar no arquivo")
    void testAdicionarProduto() throws PersistenciaException {
        Produto novoProduto = new Produto("Produto Teste", BigDecimal.valueOf(99.99), 10, "Descrição de teste.");
        dadosProdutos.adicionar(novoProduto);
        Optional<Produto> produtoAdicionado = dadosProdutos.buscarPorId(novoProduto.getId());
        assertTrue(produtoAdicionado.isPresent());
        assertEquals(novoProduto.getNome(), produtoAdicionado.get().getNome());
        try {
            String conteudoArquivo = Files.readString(Paths.get(TEMP_FILE_PATH));
            assertTrue(conteudoArquivo.contains(novoProduto.getId()));
        } catch (IOException e) {
            fail("Erro ao ler o arquivo de teste: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar o produto correto")
    void testBuscarPorIdProdutoExistente() throws PersistenciaException {
        Produto produto1 = new Produto("Produto 1", BigDecimal.valueOf(10.00), 5, "Descrição 1");
        dadosProdutos.adicionar(produto1);

        Optional<Produto> resultado = dadosProdutos.buscarPorId(produto1.getId());
        assertTrue(resultado.isPresent());
        assertEquals(produto1.getId(), resultado.get().getId());
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar Optional vazio para ID inexistente")
    void testBuscarPorIdProdutoInexistente() {
        Optional<Produto> resultado = dadosProdutos.buscarPorId("id-inexistente");
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Teste de atualizar: deve atualizar os dados de um produto existente")
    void testAtualizarProdutoExistente() throws PersistenciaException {
        Produto produtoExistente = new Produto("Produto Antigo", BigDecimal.valueOf(20.00), 20, "Descrição antiga");
        dadosProdutos.adicionar(produtoExistente);

        Produto produtoAtualizado = new Produto("Produto Novo", BigDecimal.valueOf(25.50), 15, "Descrição nova");
        produtoAtualizado.setIdLoja("loja-123");

        produtoAtualizado.setId(produtoExistente.getId());

        dadosProdutos.atualizar(produtoAtualizado);

        Optional<Produto> resultado = dadosProdutos.buscarPorId(produtoExistente.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Produto Novo", resultado.get().getNome());
        assertEquals(15, resultado.get().getEstoque());
        assertEquals("loja-123", resultado.get().getIdLoja());
    }

    @Test
    @DisplayName("Teste de atualizar: deve lançar exceção para produto inexistente")
    void testAtualizarProdutoInexistente() {
        Produto produtoInexistente = new Produto("Produto Inexistente", BigDecimal.ZERO, 0, "Desc");
        Assertions.assertThrows(LojaNaoAtualizadaException.class, () -> dadosProdutos.atualizar(produtoInexistente));
    }

    @Test
    @DisplayName("Teste de remover: deve remover um produto existente")
    void testRemoverProdutoExistente() throws PersistenciaException {
        Produto produtoParaRemover = new Produto("Produto para remover", BigDecimal.valueOf(1.00), 1, "Descrição");
        dadosProdutos.adicionar(produtoParaRemover);

        assertTrue(dadosProdutos.buscarPorId(produtoParaRemover.getId()).isPresent());

        dadosProdutos.remover(produtoParaRemover.getId());

        assertFalse(dadosProdutos.buscarPorId(produtoParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: deve lançar exceção para ID inexistente")
    void testRemoverProdutoInexistente() {
        Assertions.assertThrows(LojaNaoRemovidaException.class, () -> dadosProdutos.remover("id-nao-existe"));
    }

    @Test
    @DisplayName("Teste de listarTodas: deve retornar uma lista com todos os produtos")
    void testListarTodas() throws PersistenciaException {
        Produto p1 = new Produto("P1", BigDecimal.TEN, 10, "D1");
        Produto p2 = new Produto("P2", BigDecimal.ONE, 1, "D2");
        dadosProdutos.adicionar(p1);
        dadosProdutos.adicionar(p2);

        List<Produto> todosProdutos = dadosProdutos.listarTodas();

        assertEquals(2, todosProdutos.size());
        assertTrue(todosProdutos.stream().anyMatch(p -> p.getId().equals(p1.getId())));
        assertTrue(todosProdutos.stream().anyMatch(p -> p.getId().equals(p2.getId())));
    }

    @Test
    @DisplayName("Teste de listarMap: deve retornar um mapa com todos os produtos")
    void testListarMap() throws PersistenciaException {
        Produto p1 = new Produto("P1", BigDecimal.TEN, 10, "D1");
        dadosProdutos.adicionar(p1);
        Produto p2 = new Produto("P2", BigDecimal.ONE, 1, "D2");
        dadosProdutos.adicionar(p2);

        Assertions.assertTrue(dadosProdutos.listarMap().containsKey(p1.getId()));
        Assertions.assertTrue(dadosProdutos.listarMap().containsKey(p2.getId()));
    }
}
