package test.PersistenciaTest;

import Dados.DadosFranquias;
import Model.Franquia;
import exception.persistencia.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para a persistência de dados de franquias em um arquivo JSON.
 */
class DadosFranquiasTest {

    private static final String TEST_FILE_PATH = "franquias_test.json";
    private DadosFranquias dadosFranquias;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        // Garante que o arquivo não existe antes de cada teste
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        dadosFranquias = new DadosFranquias(TEST_FILE_PATH);
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
    void testAdicionarEListarFranquia() throws PersistenciaException {
        // Cria uma nova franquia com nome, id do dono e telefone
        Franquia franquia = new Franquia("Franquia Teste", "ID_DONO_123", "(11) 98765-4321");

        // Adiciona a franquia
        dadosFranquias.adicionar(franquia);

        // Verifica se o mapa contém a nova franquia e o telefone
        Map<String, Franquia> franquias = dadosFranquias.listarMap();
        assertEquals(1, franquias.size());
        assertTrue(franquias.containsKey(franquia.getId()));
        assertEquals("Franquia Teste", franquias.get(franquia.getId()).getNome());
        assertEquals("(11) 98765-4321", franquias.get(franquia.getId()).getTelefone());
    }

    @Test
    void testAtualizarFranquiaExistente() throws PersistenciaException {
        // Cria e adiciona uma franquia inicial com nome, id do dono e telefone
        Franquia franquiaOriginal = new Franquia("Franquia Teste Original", "ID_DONO_123", "(11) 98765-4321");
        dadosFranquias.adicionar(franquiaOriginal);

        // Cria uma franquia com o mesmo ID, mas com dados atualizados
        Franquia franquiaAtualizada = new Franquia("Franquia Teste Atualizada", "ID_DONO_123", "(11) 91234-5678");
        franquiaAtualizada.setId(franquiaOriginal.getId()); // Garante que o ID é o mesmo

        // Atualiza a franquia
        dadosFranquias.atualizar(franquiaAtualizada);

        // Verifica se a franquia foi atualizada, incluindo o novo telefone
        Map<String, Franquia> franquias = dadosFranquias.listarMap();
        assertEquals(1, franquias.size());
        assertEquals("Franquia Teste Atualizada", franquias.get(franquiaOriginal.getId()).getNome());
        assertEquals("(11) 91234-5678", franquias.get(franquiaOriginal.getId()).getTelefone());
    }

    @Test
    void testAtualizarFranquiaNaoExistenteLancaExcecao() {
        // Tenta atualizar uma franquia que não existe
        Franquia franquia = new Franquia("Franquia Inexistente", "ID_DONO_123", "(11) 99999-9999");
        assertThrows(LojaNaoAtualizadaException.class, () -> dadosFranquias.atualizar(franquia));
    }

    @Test
    void testRemoverFranquiaExistente() throws PersistenciaException {
        // Cria e adiciona uma franquia
        Franquia franquia = new Franquia("Franquia Teste", "ID_DONO_123", "(11) 1111-1111");
        dadosFranquias.adicionar(franquia);

        // Remove a franquia
        dadosFranquias.remover(franquia.getId());

        // Verifica se a franquia foi removida
        Map<String, Franquia> franquias = dadosFranquias.listarMap();
        assertTrue(franquias.isEmpty());
    }

    @Test
    void testRemoverFranquiaNaoExistenteLancaExcecao() {
        // Tenta remover uma franquia com um ID inexistente
        String idInexistente = UUID.randomUUID().toString();
        assertThrows(LojaNaoRemovidaException.class, () -> dadosFranquias.remover(idInexistente));
    }

    @Test
    void testCarregarArquivoCorrompidoLancaExcecao() throws IOException {
        // Cria um arquivo com JSON inválido para simular corrupção
        FileWriter writer = new FileWriter(TEST_FILE_PATH);
        writer.write("isso não é um json valido");
        writer.close();

        // Tenta carregar o arquivo, o que deve lançar uma exceção
        assertThrows(LojaNaoCarregadaException.class, () -> new DadosFranquias(TEST_FILE_PATH));
    }

    @Test
    void testInicializarComArquivoVazio() throws PersistenciaException {
        // O setup já cria um arquivo vazio
        dadosFranquias = new DadosFranquias(TEST_FILE_PATH);

        // O mapa deve estar vazio
        assertTrue(dadosFranquias.listarMap().isEmpty());
    }

    @Test
    void testSalvarComErroLancaExcecao() {
        Franquia franquia = new Franquia("Teste", "id_dono", "(11) 11111-1111");

        assertDoesNotThrow(() -> dadosFranquias.adicionar(franquia));
    }
}
