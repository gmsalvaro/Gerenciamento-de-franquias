//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package ServiceTest;

import model.Franquia;
import model.Gerente;
import model.Loja;
import model.Vendedor;
import service.ServiceFranquia;
import service.ServiceLoja;
import service.ServiceManager;
import service.ServiceUsuario;
import exception.ValidacaoException;
import exception.persistencia.LojaInvalidaException;
import exception.persistencia.PersistenciaException;
import exception.usuario.ValidacaoUsuarioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@DisplayName("Testes para a classe ServiceLoja")
class ServiceLojaTest {

    private static final String FILE_USUARIO = "usuarios_test_sl.json";
    private static final String FILE_LOJA = "lojas_test_sl.json";
    private static final String FILE_PRODUTOS = "produtos_test_sl.json";
    private static final String FILE_PEDIDOS = "pedidos_test_sl.json";
    private static final String FILE_FRANQUIAS = "franquias_test_sl.json";
    private static final String FILE_CLIENTE = "clientes_test_sl.json";

    private ServiceManager serviceManager;
    private ServiceLoja serviceLoja;
    private ServiceFranquia serviceFranquia;
    private ServiceUsuario serviceUsuario;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
        Files.deleteIfExists(Paths.get(FILE_CLIENTE));

        this.serviceManager = new ServiceManager(FILE_USUARIO, FILE_LOJA, FILE_PRODUTOS, FILE_PEDIDOS, FILE_FRANQUIAS, FILE_CLIENTE);

        this.serviceLoja = serviceManager.getServiceLoja();
        this.serviceFranquia = serviceManager.getServiceFranquia();
        this.serviceUsuario = serviceManager.getServiceUsuario();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpa todos os arquivos de teste após cada execução
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
        Files.deleteIfExists(Paths.get(FILE_CLIENTE));
    }

    @Test
    @DisplayName("Deve adicionar uma loja com sucesso")
    void testAdicionarLojaComSucesso() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereço Teste", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);

        Loja loja = new Loja("Loja Teste", "Endereço Loja", franquia.getId());
        Assertions.assertDoesNotThrow(() -> serviceLoja.adicionar(loja, franquia, gerente));
        Assertions.assertEquals(1, serviceLoja.listarTodos().size());
        Assertions.assertEquals(1, serviceFranquia.buscarPorId(franquia.getId()).getIdLojas().size());
    }

    @Test
    @DisplayName("Não deve adicionar uma loja com nome duplicado")
    void testAdicionarLojaComNomeDuplicado() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereço Teste", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);

        Loja loja1 = new Loja("Loja Duplicada", "Endereço 1", franquia.getId());
        Loja loja2 = new Loja("Loja Duplicada", "Endereço 2", franquia.getId());

        serviceLoja.adicionar(loja1, franquia, gerente);

        Assertions.assertThrows(LojaInvalidaException.class, () -> serviceLoja.adicionar(loja2, franquia, gerente));
        Assertions.assertEquals(1, serviceLoja.listarTodos().size());
    }

    @Test
    @DisplayName("Deve remover uma loja com sucesso, atualizando a franquia e removendo usuários associados")
    void testRemoverLojaComSucesso() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia para remover", "Endereço da franquia", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente para remover", "gerente.rem@teste.com", "Senha123", "12345678901");
        Vendedor vendedor = new Vendedor("Vendedor para remover", "vend.rem@teste.com", "Senha123", "12345678902");
        serviceUsuario.adicionar(gerente);
        serviceUsuario.adicionar(vendedor);

        Loja lojaParaRemover = new Loja("Loja para Remover", "Endereço para Remover", franquia.getId());
        serviceLoja.adicionar(lojaParaRemover, franquia, gerente);
        lojaParaRemover.addUsuarioID(vendedor.getId());
        serviceLoja.atualizar(lojaParaRemover);

        Assertions.assertEquals(1, serviceFranquia.buscarPorId(franquia.getId()).getIdLojas().size());
        Assertions.assertEquals(2, serviceUsuario.getUsuarios().size());

        serviceLoja.remover(lojaParaRemover, serviceManager);

        Assertions.assertTrue(serviceLoja.listarTodos().isEmpty());
        Assertions.assertTrue(serviceFranquia.buscarPorId(franquia.getId()).getIdLojas().isEmpty());
        Assertions.assertTrue(serviceUsuario.getUsuarios().isEmpty());
    }

    @Test
    @DisplayName("Deve listar lojas por ID de franquia")
    void testListarPorFranquia() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia1 = new Franquia("Franquia 1", "End 1", "1234567890");
        Franquia franquia2 = new Franquia("Franquia 2", "End 2", "0987654321");
        serviceFranquia.adicionar(franquia1);
        serviceFranquia.adicionar(franquia2);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);

        Loja loja1 = new Loja("Loja 1", "End Loja 1", franquia1.getId());
        Loja loja2 = new Loja("Loja 2", "End Loja 2", franquia1.getId());
        Loja loja3 = new Loja("Loja 3", "End Loja 3", franquia2.getId());

        serviceLoja.adicionar(loja1, franquia1, gerente);
        serviceLoja.adicionar(loja2, franquia1, gerente);
        serviceLoja.adicionar(loja3, franquia2, gerente);

        List<Loja> lojasDaFranquia1 = serviceLoja.listarPorFranquia(franquia1.getId());
        Assertions.assertEquals(2, lojasDaFranquia1.size());
        Assertions.assertTrue(lojasDaFranquia1.stream().anyMatch(l -> l.getNome().equals("Loja 1")));
        Assertions.assertTrue(lojasDaFranquia1.stream().anyMatch(l -> l.getNome().equals("Loja 2")));
    }

    @Test
    @DisplayName("Deve atualizar uma loja com sucesso")
    void testAtualizarLoja() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereço Teste", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);

        Loja loja = new Loja("Loja Antiga", "Endereço Antigo", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerente);

        Loja lojaAtualizada = new Loja("Loja Nova", "Endereço Novo", franquia.getId());
        lojaAtualizada.setId(loja.getId());

        Assertions.assertDoesNotThrow(() -> serviceLoja.atualizar(lojaAtualizada));
        Loja resultado = serviceLoja.getLojaById(loja.getId());
        Assertions.assertEquals("Loja Nova", resultado.getNome());
        Assertions.assertEquals("Endereço Novo", resultado.getEndereco());
    }

    @Test
    @DisplayName("Deve designar um novo gerente para uma loja, removendo o antigo")
    void testDesignarGerenteParaLoja() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereço Teste", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerenteAntigo = new Gerente("Gerente Antigo", "antigo@gerente.com", "Senha123", "12345678901");
        Gerente gerenteNovo = new Gerente("Gerente Novo", "novo@gerente.com", "Senha123", "12345678902");
        serviceUsuario.adicionar(gerenteAntigo);
        serviceUsuario.adicionar(gerenteNovo);

        Loja loja = new Loja("Loja com gerente", "Endereço Loja", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerenteAntigo);

        Assertions.assertTrue(serviceLoja.lojaTemGerente(loja, serviceManager));
        Assertions.assertTrue(loja.getIdsUsuarios().contains(gerenteAntigo.getId()));

        Assertions.assertDoesNotThrow(() -> serviceLoja.designarGerenteParaLoja(gerenteNovo, loja, serviceUsuario));

        Loja lojaAtualizada = serviceLoja.getLojaById(loja.getId());
        Assertions.assertTrue(serviceLoja.lojaTemGerente(lojaAtualizada, serviceManager));
        Assertions.assertTrue(lojaAtualizada.getIdsUsuarios().contains(gerenteNovo.getId()));
        Assertions.assertFalse(lojaAtualizada.getIdsUsuarios().contains(gerenteAntigo.getId()));
    }
}
