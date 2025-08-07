//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package ServiceTest;


import exception.usuario.NomeInvalidoException;
import model.*;
import service.ServiceLoja;
import service.ServiceManager;
import service.ServiceUsuario;
import exception.autenticacao.UsuarioInvalidoException;
import exception.persistencia.PersistenciaException;
import exception.usuario.CPFInvalidoException;
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
import java.util.Optional;

@DisplayName("Testes para a classe ServiceUsuario")
class ServiceUsuarioTest {

    private static final String FILE_USUARIO = "usuarios_test_su.json";
    private static final String FILE_LOJA = "lojas_test_su.json";
    private static final String FILE_PRODUTOS = "produtos_test_su.json";
    private static final String FILE_PEDIDOS = "pedidos_test_su.json";
    private static final String FILE_FRANQUIAS = "franquias_test_su.json";
    private static final String FILE_CLIENTES = "clientes_test_su.json";

    private ServiceManager serviceManager;
    private ServiceUsuario serviceUsuario;
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

        this.serviceUsuario = serviceManager.getServiceUsuario();
        this.serviceLoja = serviceManager.getServiceLoja();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpa todos os arquivos de teste após cada execução
        Files.deleteIfExists(Paths.get(FILE_USUARIO));
        Files.deleteIfExists(Paths.get(FILE_LOJA));
        Files.deleteIfExists(Paths.get(FILE_PRODUTOS));
        Files.deleteIfExists(Paths.get(FILE_PEDIDOS));
        Files.deleteIfExists(Paths.get(FILE_FRANQUIAS));
        Files.deleteIfExists(Paths.get(FILE_CLIENTES));
    }

    @Test
    @DisplayName("Deve adicionar um Vendedor e um Gerente com sucesso")
    void testAdicionarUsuarioComSucesso() throws ValidacaoUsuarioException {
        Vendedor vendedor = new Vendedor("Vendedor Teste", "vendedor@teste.com", "Senha123", "12345678901");
        Gerente gerente = new Gerente("Gerente Teste", "gerente@teste.com", "Senha123", "98765432109");

        Assertions.assertDoesNotThrow(() -> serviceUsuario.adicionar(vendedor));
        Assertions.assertDoesNotThrow(() -> serviceUsuario.adicionar(gerente));
        Assertions.assertEquals(2, serviceUsuario.getUsuarios().size());
    }

    @Test
    @DisplayName("Não deve adicionar usuário com nome, email ou CPF duplicados")
    void testAdicionarUsuarioComDadosDuplicados() throws ValidacaoUsuarioException {
        Vendedor vendedor1 = new Vendedor("Vendedor Teste", "vendedor@teste.com", "Senha123", "12345678901");
        Vendedor vendedor2 = new Vendedor("Vendedor Teste", "vendedor2@teste.com", "Senha123", "11111111111"); // Nome duplicado
        Vendedor vendedor3 = new Vendedor("Vendedor Teste tres", "vendedor@teste.com", "Senha123", "22222222222"); // Email duplicado
        Vendedor vendedor4 = new Vendedor("Vendedor Teste quatro", "vendedor4@teste.com", "Senha123", "12345678901"); // CPF duplicado

        serviceUsuario.adicionar(vendedor1);

        Assertions.assertThrows(NomeInvalidoException.class, () -> serviceUsuario.adicionar(vendedor2));
        Assertions.assertThrows(ValidacaoUsuarioException.class, () -> serviceUsuario.adicionar(vendedor3));
        Assertions.assertThrows(CPFInvalidoException.class, () -> serviceUsuario.adicionar(vendedor4));
        Assertions.assertEquals(1, serviceUsuario.getUsuarios().size());
    }

    @Test
    @DisplayName("Deve remover um usuário com sucesso")
    void testRemoverUsuarioComSucesso() throws ValidacaoUsuarioException, PersistenciaException {
        Vendedor vendedor = new Vendedor("Vendedor Teste", "vendedor@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(vendedor);
        Assertions.assertEquals(1, serviceUsuario.getUsuarios().size());

        Assertions.assertDoesNotThrow(() -> serviceUsuario.removeUsuario(vendedor));
        Assertions.assertTrue(serviceUsuario.getUsuarios().isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void testAtualizarUsuarioComSucesso() throws ValidacaoUsuarioException, UsuarioInvalidoException {
        Gerente gerente = new Gerente("Gerente Antigo", "gerente_antigo@teste.com", "Senha123", "12345678901");
        serviceUsuario.adicionar(gerente);

        Gerente gerenteAtualizado = new Gerente("Gerente Novo", "gerente_novo@teste.com", "NovaSenha123", "12345678901");
        gerenteAtualizado.setId(gerente.getId());

        serviceUsuario.atualizarUsuario(gerenteAtualizado);
        Optional<Usuario> usuarioRecuperado = serviceUsuario.buscarPorId(gerente.getId());

        Assertions.assertTrue(usuarioRecuperado.isPresent());
        Assertions.assertEquals("Gerente Novo", usuarioRecuperado.get().getNome());
        Assertions.assertEquals("gerente_novo@teste.com", usuarioRecuperado.get().getEmail());
    }

    @Test
    @DisplayName("Deve autenticar um usuário com credenciais válidas")
    void testAutenticarUsuarioComSucesso() throws ValidacaoUsuarioException, UsuarioInvalidoException {
        Vendedor vendedor = new Vendedor("Vendedor Autentica", "auth@teste.com", "Senha_secreta123", "11122233344");
        serviceUsuario.adicionar(vendedor);

        Usuario usuarioAutenticado = serviceUsuario.autenticarUsuario("auth@teste.com", "Senha_secreta123");
        Assertions.assertNotNull(usuarioAutenticado);
        Assertions.assertEquals(vendedor.getNome(), usuarioAutenticado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar autenticar com credenciais inválidas")
    void testAutenticarUsuarioComCredenciaisInvalidas() throws ValidacaoUsuarioException {
        Vendedor vendedor = new Vendedor("Vendedor Autentica", "auth@teste.com", "Senha_secreta123", "11122233344");
        serviceUsuario.adicionar(vendedor);

        Assertions.assertThrows(UsuarioInvalidoException.class, () -> serviceUsuario.autenticarUsuario("auth@teste.com", "senha_errada"));
        Assertions.assertThrows(UsuarioInvalidoException.class, () -> serviceUsuario.autenticarUsuario("email_errado@teste.com", "Senha_secreta123"));
    }

    @Test
    @DisplayName("Deve rebaixar um Gerente para Vendedor, mantendo os dados")
    void testRebaixarGerenteParaVendedor() throws ValidacaoUsuarioException, PersistenciaException {
        Gerente gerente = new Gerente("Gerente Rebaixar", "gerente@rebaixar.com", "Senha_gerente123", "99988877766");
        serviceUsuario.adicionar(gerente);

        serviceUsuario.rebaixarGerenteParaVendedor(gerente);

        Usuario usuarioRebaixado = serviceUsuario.getUsuarioById(gerente.getId());
        Assertions.assertTrue(usuarioRebaixado instanceof Vendedor);
        Assertions.assertEquals("Gerente Rebaixar", usuarioRebaixado.getNome());
    }

    @Test
    @DisplayName("Deve listar gerentes que não estão em nenhuma loja")
    void testListarGerentesDisponiveis() throws ValidacaoUsuarioException, PersistenciaException {
        Gerente gerenteDisponivel = new Gerente("Gerente Disponivel", "gerente1@teste.com", "Senha123", "11111111111");
        Gerente gerenteOcupado = new Gerente("Gerente Ocupado", "gerente2@teste.com", "Senha123", "22222222222");
        serviceUsuario.adicionar(gerenteDisponivel);
        serviceUsuario.adicionar(gerenteOcupado);

        Franquia franquia = new Franquia("Franquia Teste", "Endereco Franquia", "123456789");
        Loja loja = new Loja("Loja do Gerente", "Endereco Loja", "123456789");
        loja.addUsuarioID(gerenteOcupado.getId());
        serviceLoja.adicionar(loja, franquia, gerenteOcupado);

        List<Gerente> gerentesDisponiveis = serviceUsuario.listarGerentesDisponiveis(serviceLoja);

        Assertions.assertEquals(1, gerentesDisponiveis.size());
        Assertions.assertEquals("Gerente Disponivel", gerentesDisponiveis.get(0).getNome());
    }
}
