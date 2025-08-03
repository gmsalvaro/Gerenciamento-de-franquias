package Dados;

import Model.Loja;
import exception.persistencia.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para a persistência de dados de lojas em um arquivo JSON.
 */
class DadosLojasTest {

    private static final String TEST_FILE_PATH = "lojas_test.json";
    private DadosLojas dadosLojas;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        // Garante que o arquivo não existe antes de cada teste
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        dadosLojas = new DadosLojas(TEST_FILE_PATH);
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
    void testAdicionarEListarLoja() throws PersistenciaException {
        // Cria uma nova loja com os campos atualizados
        Loja loja = new Loja("Loja Teste", "Rua das Flores, 123", "ID_FRANQUIA_456");

        // Adiciona a loja
        dadosLojas.adicionar(loja);

        // Verifica se o mapa contém a nova loja e os novos campos
        Map<String, Loja> lojas = dadosLojas.getLojasMap();
        assertEquals(1, lojas.size());
        assertTrue(lojas.containsKey(loja.getId()));
        assertEquals("Loja Teste", lojas.get(loja.getId()).getNome());
        assertEquals("Rua das Flores, 123", lojas.get(loja.getId()).getEndereco());
        assertEquals("ID_FRANQUIA_456", lojas.get(loja.getId()).getFranquiaId());
    }

    @Test
    void testBuscarLojaPorId() throws PersistenciaException {
        // Cria e adiciona uma loja
        Loja loja = new Loja("Loja Teste", "Rua das Flores, 123", "ID_FRANQUIA_456");
        dadosLojas.adicionar(loja);

        // Busca a loja por ID
        Optional<Loja> lojaEncontrada = dadosLojas.buscarPorId(loja.getId());
        assertTrue(lojaEncontrada.isPresent());
        assertEquals(loja.getId(), lojaEncontrada.get().getId());

        // Busca por um ID inexistente
        Optional<Loja> lojaNaoEncontrada = dadosLojas.buscarPorId(UUID.randomUUID().toString());
        assertFalse(lojaNaoEncontrada.isPresent());
    }

    @Test
    void testAtualizarLojaExistente() throws PersistenciaException {
        // Cria e adiciona uma loja inicial
        Loja lojaOriginal = new Loja("Loja Teste Original", "Rua das Rosas, 10", "ID_FRANQUIA_456");
        dadosLojas.adicionar(lojaOriginal);

        // Cria uma loja com o mesmo ID, mas com dados atualizados
        Loja lojaAtualizada = new Loja("Loja Teste Atualizada", "Avenida dos Sonhos, 500", "ID_FRANQUIA_456");
        lojaAtualizada.setId(lojaOriginal.getId());

        // Atualiza a loja
        dadosLojas.atualizar(lojaAtualizada);

        // Verifica se a loja foi atualizada
        Map<String, Loja> lojas = dadosLojas.getLojasMap();
        assertEquals(1, lojas.size());
        assertEquals("Loja Teste Atualizada", lojas.get(lojaOriginal.getId()).getNome());
        assertEquals("Avenida dos Sonhos, 500", lojas.get(lojaOriginal.getId()).getEndereco());
    }

    @Test
    void testAtualizarLojaNaoExistenteLancaExcecao() {
        // Tenta atualizar uma loja que não existe
        Loja loja = new Loja("Loja Inexistente", "Rua qualquer", "ID_FRANQUIA_123");
        assertThrows(LojaNaoAtualizadaException.class, () -> dadosLojas.atualizar(loja));
    }

    @Test
    void testRemoverLojaExistente() throws PersistenciaException {
        // Cria e adiciona uma loja
        Loja loja = new Loja("Loja Teste", "Rua Teste", "ID_FRANQUIA_456");
        dadosLojas.adicionar(loja);

        // Remove a loja
        dadosLojas.remover(loja.getId());

        // Verifica se a loja foi removida
        Map<String, Loja> lojas = dadosLojas.getLojasMap();
        assertTrue(lojas.isEmpty());
    }

    @Test
    void testRemoverLojaNaoExistenteLancaExcecao() {
        // Tenta remover uma loja com um ID inexistente
        String idInexistente = UUID.randomUUID().toString();
        assertThrows(LojaNaoRemovidaException.class, () -> dadosLojas.remover(idInexistente));
    }

    @Test
    void testCarregarArquivoCorrompidoLancaExcecao() throws IOException {
        // Cria um arquivo com JSON inválido para simular corrupção
        FileWriter writer = new FileWriter(TEST_FILE_PATH);
        writer.write("isso não é um json valido");
        writer.close();

        // Tenta carregar o arquivo, o que deve lançar uma exceção
        assertThrows(LojaNaoCarregadaException.class, () -> new DadosLojas(TEST_FILE_PATH));
    }

    @Test
    void testInicializarComArquivoVazio() throws PersistenciaException {
        // O setup já cria um arquivo vazio
        dadosLojas = new DadosLojas(TEST_FILE_PATH);

        // O mapa deve estar vazio
        assertTrue(dadosLojas.getLojasMap().isEmpty());
    }

    @Test
    void testManipulacaoDeListasDeId() throws PersistenciaException {
        // Cria e adiciona uma loja
        Loja loja = new Loja("Loja com Listas", "Rua do Bairro, 10", "ID_FRANQUIA_111");
        dadosLojas.adicionar(loja);

        // Adiciona IDs
        String userId = UUID.randomUUID().toString();
        String pedidoId = UUID.randomUUID().toString();
        String produtoId = UUID.randomUUID().toString();

        loja.addUsuarioID(userId);
        loja.adicionarIdPedido(pedidoId);
        loja.adicionarIdProduto(produtoId);
        dadosLojas.atualizar(loja);

        // Busca a loja atualizada e verifica as listas
        Loja lojaAtualizada = dadosLojas.buscarPorId(loja.getId()).get();
        assertEquals(1, lojaAtualizada.getIdsUsuarios().size());
        assertTrue(lojaAtualizada.getIdsUsuarios().contains(userId));
        assertEquals(1, lojaAtualizada.getIdPedidos().size());
        assertTrue(lojaAtualizada.getIdPedidos().contains(pedidoId));
        assertEquals(1, lojaAtualizada.getIdProdutos().size());
        assertTrue(lojaAtualizada.getIdProdutos().contains(produtoId));

        // Remove IDs e verifica novamente
        lojaAtualizada.removeUsuario(userId);
        lojaAtualizada.removerIdPedido(pedidoId);
        lojaAtualizada.removerIdProduto(produtoId);
        dadosLojas.atualizar(lojaAtualizada);

        Loja lojaAposRemocao = dadosLojas.buscarPorId(loja.getId()).get();
        assertTrue(lojaAposRemocao.getIdsUsuarios().isEmpty());
        assertTrue(lojaAposRemocao.getIdPedidos().isEmpty());
        assertTrue(lojaAposRemocao.getIdProdutos().isEmpty());
    }
}
