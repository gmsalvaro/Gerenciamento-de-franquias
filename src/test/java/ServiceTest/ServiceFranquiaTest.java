// package para os testes
package ServiceTest;

import Service.*;
import model.Franquia;
import model.Gerente;
import model.Loja;
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
import java.util.Optional;

@DisplayName("Testes para a classe ServiceFranquia")
class ServiceFranquiaTest {

    private static final String FILE_USUARIO = "usuarios_test_sf.json";
    private static final String FILE_LOJA = "lojas_test_sf.json";
    private static final String FILE_PRODUTOS = "produtos_test_sf.json";
    private static final String FILE_PEDIDOS = "pedidos_test_sf.json";
    private static final String FILE_FRANQUIAS = "franquias_test_sf.json";
    private static final String FILE_CLIENTE = "clientes_test_sf.json";

    private ServiceManager serviceManager;
    private ServiceFranquia serviceFranquia;
    private ServiceLoja serviceLoja;
    private ServiceUsuario serviceUsuario;
    private ServiceCliente serviceCliente;

    @BeforeEach
    void setup() throws PersistenciaException, IOException {
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
        Files.deleteIfExists(Paths.get(FILE_CLIENTE));

        this.serviceManager = new ServiceManager(FILE_USUARIO, FILE_LOJA, FILE_PRODUTOS, FILE_PEDIDOS, FILE_FRANQUIAS, FILE_CLIENTE);

        this.serviceFranquia = serviceManager.getServiceFranquia();
        this.serviceLoja = serviceManager.getServiceLoja();
        this.serviceUsuario = serviceManager.getServiceUsuario();
        this.serviceCliente = serviceManager.getServiceCliente();
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
    @DisplayName("Deve adicionar uma franquia com sucesso")
    void testAdicionarFranquiaComSucesso() throws PersistenciaException {
        Franquia franquia = new Franquia("Franquia Teste", "Endereco Teste", "1234567890");
        Assertions.assertDoesNotThrow(() -> serviceFranquia.adicionar(franquia));
        Assertions.assertEquals(1, serviceFranquia.listarTodos().size());
    }

    @Test
    @DisplayName("Não deve adicionar uma franquia com nome duplicado")
    void testAdicionarFranquiaComNomeDuplicado() throws PersistenciaException {
        Franquia franquia1 = new Franquia("Franquia Teste", "Endereco 1", "1234567890");
        Franquia franquia2 = new Franquia("Franquia Teste", "Endereco 2", "0987654321");

        serviceFranquia.adicionar(franquia1);

        Assertions.assertThrows(LojaInvalidaException.class, () -> serviceFranquia.adicionar(franquia2));
        Assertions.assertEquals(1, serviceFranquia.listarTodos().size());
    }

    @Test
    @DisplayName("Não deve adicionar uma franquia com endereço duplicado")
    void testAdicionarFranquiaComEnderecoDuplicado() throws PersistenciaException {
        Franquia franquia1 = new Franquia("Franquia 1", "Endereco Duplicado", "1234567890");
        Franquia franquia2 = new Franquia("Franquia 2", "Endereco Duplicado", "0987654321");

        serviceFranquia.adicionar(franquia1);

        Assertions.assertThrows(LojaInvalidaException.class, () -> serviceFranquia.adicionar(franquia2));
        Assertions.assertEquals(1, serviceFranquia.listarTodos().size());
    }

    @Test
    @DisplayName("Deve remover uma franquia e todas as lojas associadas com sucesso")
    void testRemoverFranquiaComSucesso() throws PersistenciaException, ValidacaoException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia para remover", "Endereço da franquia", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente para remover", "gerente.rem@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);

        Loja loja1 = new Loja("Loja 1","Teste",  franquia.getId());
        Loja loja2 = new Loja("Loja 2","Teste01", franquia.getId());
        serviceLoja.adicionar(loja1, franquia, gerente);
        serviceLoja.adicionar(loja2, franquia, gerente);

        Assertions.assertEquals(2, serviceLoja.listarTodos().size());
        Assertions.assertEquals(1, serviceFranquia.listarTodos().size());

        serviceFranquia.remover(franquia, serviceManager);
        Assertions.assertTrue(serviceFranquia.listarTodos().isEmpty());
        Assertions.assertTrue(serviceLoja.listarTodos().isEmpty());
        Assertions.assertTrue(serviceUsuario.getUsuarios().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover uma franquia inexistente")
    void testRemoverFranquiaInexistente() {
        Franquia franquia = new Franquia("Franquia Inexistente", "Endereço qualquer", "1234567890");
        Assertions.assertThrows(PersistenciaException.class, () -> serviceFranquia.remover(franquia, serviceManager));
    }

    @Test
    @DisplayName("Deve atualizar uma franquia com sucesso")
    void testAtualizarFranquiaComSucesso() throws PersistenciaException, ValidacaoException {
        Franquia franquia = new Franquia("Franquia Velha", "Endereço Velho", "1234567890");
        serviceFranquia.adicionar(franquia);

        Franquia franquiaAtualizada = new Franquia("Franquia Nova", "Endereço Novo", "0987654321");
        franquiaAtualizada.setId(franquia.getId());

        serviceFranquia.atualizar(franquiaAtualizada);

        Franquia resultado = serviceFranquia.buscarPorId(franquia.getId());
        Assertions.assertEquals("Franquia Nova", resultado.getNome());
        Assertions.assertEquals("Endereço Novo", resultado.getEndereco());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com nome duplicado")
    void testAtualizarFranquiaComNomeDuplicado() throws PersistenciaException {
        Franquia franquia1 = new Franquia("Franquia 1", "End 1", "1237567890");
        Franquia franquia2 = new Franquia("Franquia 2", "End 2", "123456789");
        serviceFranquia.adicionar(franquia1);
        serviceFranquia.adicionar(franquia2);

        Franquia franquiaAtualizada = new Franquia("Franquia 1", "End 3", "1234567890");
        franquiaAtualizada.setId(franquia2.getId());

        Assertions.assertThrows(ValidacaoException.class, () -> serviceFranquia.atualizar(franquiaAtualizada));
    }

    @Test
    @DisplayName("Deve buscar uma franquia por ID com sucesso")
    void testBuscarPorId() throws PersistenciaException {
        Franquia franquia = new Franquia("Franquia Buscavel", "Endereço", "1234567890");
        serviceFranquia.adicionar(franquia);

        Franquia resultado = serviceFranquia.buscarPorId(franquia.getId());
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(franquia.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve retornar a franquia do gerente se ele estiver associado a uma loja")
    void testGetFranquiaDoGerente() throws PersistenciaException, ValidacaoUsuarioException {
        Franquia franquia = new Franquia("Franquia com gerente", "Endereco Franquia", "1234567890");
        serviceFranquia.adicionar(franquia);
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);
        Loja loja = new Loja("Loja do Gerente", "Endereço Loja", franquia.getId());
        serviceLoja.adicionar(loja, franquia, gerente);

        Optional<Franquia> franquiaEncontrada = serviceFranquia.getFranquiaDoGerente(gerente, serviceLoja);

        Assertions.assertTrue(franquiaEncontrada.isPresent());
        Assertions.assertEquals(franquia.getId(), franquiaEncontrada.get().getId());
    }
}
