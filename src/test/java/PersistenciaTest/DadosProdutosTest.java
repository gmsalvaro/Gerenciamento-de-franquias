package PersistenciaTest;

import Dados.DadosProdutos;
import Model.Produto;
import exception.persistencia.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
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
        // Inicializa uma nova instância de DadosProdutos antes de cada teste
        // e garante que o arquivo de teste esteja limpo.
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosProdutos = new DadosProdutos(TEMP_FILE_PATH);
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
        // Verificamos se o arquivo foi criado e se a lista de produtos está vazia.
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosProdutos.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar um novo produto e salvar no arquivo")
    void testAdicionarProduto() throws PersistenciaException {
        // Cria um novo produto e o adiciona
        Produto novoProduto = new Produto("Produto Teste", BigDecimal.valueOf(99.99), 10, "Descrição de teste.");
        dadosProdutos.adicionar(novoProduto);

        // Verifica se o produto existe na lista e no mapa
        Optional<Produto> produtoAdicionado = dadosProdutos.buscarPorId(novoProduto.getId());
        assertTrue(produtoAdicionado.isPresent());
        assertEquals(novoProduto.getNome(), produtoAdicionado.get().getNome());

        // Verifica se o produto foi persistido corretamente no arquivo
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

//    @Test
//    @DisplayName("Teste de atualizar: deve atualizar os dados de um produto existente")
//    void testAtualizarProdutoExistente() throws PersistenciaException {
//        Produto produtoExistente = new Produto("Produto Antigo", BigDecimal.valueOf(20.00), 20, "Descrição antiga");
//        dadosProdutos.adicionar(produtoExistente);
//
//        // Cria uma nova instância com o mesmo ID, mas com dados atualizados
//        Produto produtoAtualizado = new Produto("Produto Novo", BigDecimal.valueOf(25.50), 15, "Descrição nova");
//        produtoAtualizado.setIdLoja("loja-123");
//
//        // Use o ID do produto existente
//        produtoAtualizado.setId(produtoExistente.getId());
//
//        dadosProdutos.atualizar(produtoAtualizado);
//
//        Optional<Produto> resultado = dadosProdutos.buscarPorId(produtoExistente.getId());
//        assertTrue(resultado.isPresent());
//        assertEquals("Produto Novo", resultado.get().getNome());
//        assertEquals(15, resultado.get().getEstoque());
//        assertEquals("loja-123", resultado.get().getIdLoja());
//    }

    @Test
    @DisplayName("Teste de atualizar: deve lançar exceção para produto inexistente")
    void testAtualizarProdutoInexistente() {
        Produto produtoInexistente = new Produto("Produto Inexistente", BigDecimal.ZERO, 0, "Desc");

        // Tenta atualizar um produto que não foi adicionado
        Assertions.assertThrows(LojaNaoAtualizadaException.class, () -> dadosProdutos.atualizar(produtoInexistente));
    }

    @Test
    @DisplayName("Teste de remover: deve remover um produto existente")
    void testRemoverProdutoExistente() throws PersistenciaException {
        Produto produtoParaRemover = new Produto("Produto para remover", BigDecimal.valueOf(1.00), 1, "Descrição");
        dadosProdutos.adicionar(produtoParaRemover);

        // Verifica que o produto existe antes da remoção
        assertTrue(dadosProdutos.buscarPorId(produtoParaRemover.getId()).isPresent());

        dadosProdutos.remover(produtoParaRemover.getId());

        // Verifica que o produto não existe mais
        assertFalse(dadosProdutos.buscarPorId(produtoParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: deve lançar exceção para ID inexistente")
    void testRemoverProdutoInexistente() {
        // Tenta remover um ID que não existe
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

        // Adiciona um novo produto
        Produto p2 = new Produto("P2", BigDecimal.ONE, 1, "D2");
        dadosProdutos.adicionar(p2);

        // Verifica se o mapa retornado contém os produtos
        Assertions.assertTrue(dadosProdutos.listarMap().containsKey(p1.getId()));
        Assertions.assertTrue(dadosProdutos.listarMap().containsKey(p2.getId()));
    }
}
