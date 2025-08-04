package ServiceTest;

import Dados.DadosUsuario;
import Model.Gerente;
import Model.Loja;
import Model.Usuario;
import Model.Vendedor;
import Service.ServiceUsuario;
import exception.ValidacaoException;
import exception.autenticacao.UsuarioInvalidoException;
import exception.persistencia.PersistenciaException;
import exception.usuario.CPFInvalidoException;
import exception.usuario.EmailInvalidoException;
import exception.usuario.ValidacaoUsuarioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUsuarioIntegracaoTest {

    private static final String FILE_TEST_PATH = "usuarios_integracao.json";
    private ServiceUsuario serviceUsuario;
    private DadosUsuario dadosUsuario;
    private File testFile;

    private Loja lojaComUsuarios;
    private Loja lojaSemUsuarios;

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash de senha.", e);
        }
    }

    @BeforeEach
    void setUp() throws ValidacaoUsuarioException {
        this.testFile = new File(FILE_TEST_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }

        this.serviceUsuario = new ServiceUsuario(FILE_TEST_PATH);
        this.dadosUsuario = new DadosUsuario(FILE_TEST_PATH);

        // Usuários com senhas que passam na validação.
        Usuario gerente = new Gerente("Gerente Teste", "gerente@example.com", "SenhaTeste123", "11111111111");
        Usuario vendedor1 = new Vendedor("Vendedor Um", "vendedor1@example.com", "SenhaTeste123", "22222222222");
        Usuario vendedor2 = new Vendedor("Vendedor Dois", "vendedor2@example.com", "OutraSenha456", "33333333333");

        serviceUsuario.addUsuario(gerente);
        serviceUsuario.addUsuario(vendedor1);
        serviceUsuario.addUsuario(vendedor2);


        lojaComUsuarios = new Loja("Loja Teste", "Endereço", "franquiaId1");
        lojaComUsuarios.addUsuarioID(gerente.getId());
        lojaComUsuarios.addUsuarioID(vendedor1.getId());

        lojaSemUsuarios = new Loja("Outra Loja", "Outro Endereço", "franquiaId2");
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    // --- Testes de Autenticação ---
    @Test
    void testAutenticarUsuario_deveFuncionarComDadosDoArquivo() throws UsuarioInvalidoException {
        Usuario usuarioAutenticado = serviceUsuario.autenticarUsuario("gerente@example.com", "SenhaTeste123");
        assertNotNull(usuarioAutenticado);
        assertEquals("gerente@example.com", usuarioAutenticado.getEmail());
    }

    @Test
    void testAutenticarUsuario_comSenhaIncorreta_deveLancarExcecao() {
        assertThrows(UsuarioInvalidoException.class, () -> serviceUsuario.autenticarUsuario("gerente@example.com", "senhaErrada"));
    }

    @Test
    void testAutenticarUsuario_comEmailInexistente_deveLancarExcecao() {
        assertThrows(UsuarioInvalidoException.class, () -> serviceUsuario.autenticarUsuario("email_nao_existe@example.com", "SenhaTeste123"));
    }

    // --- Testes de Adição de Usuário ---
    @Test
    void testAddUsuario_deveSalvarNoArquivoCorretamente() throws ValidacaoUsuarioException, PersistenciaException {
        Usuario novoUsuario = new Vendedor("Ana", "ana@test.com", "SenhaNova123", "44444444444");


        serviceUsuario.addUsuario(novoUsuario);

        DadosUsuario dadosParaVerificar = new DadosUsuario(FILE_TEST_PATH);

        Usuario usuarioSalvo = dadosParaVerificar.getUsuariosMap().get(novoUsuario.getId());

        assertNotNull(usuarioSalvo); // Garante que o usuário foi encontrado
        assertEquals("Ana", usuarioSalvo.getNome()); // Verifica o nome
        assertEquals("ana@test.com", usuarioSalvo.getEmail()); // Verifica o e-mail
    }

    @Test
    void testAddUsuario_comEmailDuplicado_deveLancarExcecao() {
        Usuario usuarioDuplicado = new Vendedor("Novo Nome", "gerente@example.com", "SenhaNova123", "55555555555");
        assertThrows(ValidacaoUsuarioException.class, () -> serviceUsuario.addUsuario(usuarioDuplicado));
    }

    @Test
    void testAddUsuario_comCpfDuplicado_deveLancarExcecao() {
        Usuario usuarioDuplicado = new Vendedor("Novo Nome", "novo_email@example.com", "SenhaNova123", "11111111111");
        assertThrows(CPFInvalidoException.class, () -> serviceUsuario.addUsuario(usuarioDuplicado));
    }

    // --- Testes de Remoção de Usuário ---
    @Test
    void testRemoveUsuario_deveRemoverDoArquivoCorretamente() throws ValidacaoUsuarioException, PersistenciaException {
        Usuario vendedor1 = new Vendedor("Vendedor DoisTres", "vendedor22@example.com", "SenhaTeste123", "22211222322");
        serviceUsuario.addUsuario(vendedor1);
        serviceUsuario.removeUsuario(vendedor1);

        DadosUsuario dadosParaVerificar = new DadosUsuario(FILE_TEST_PATH);
        Optional<Usuario> usuarioRemovido = dadosParaVerificar.buscarPorId(vendedor1.getId());
        assertFalse(usuarioRemovido.isPresent());
        assertEquals(3, serviceUsuario.getUsuarios().size());
    }

    @Test
    void testRemoveUsuario_comUsuarioInexistente_deveLancarExcecao() {
        Usuario usuarioInexistente = new Vendedor("nao_existe", "inexistente@test.com", "SenhaTeste123", "00000000000");
        assertThrows(PersistenciaException.class, () -> serviceUsuario.removeUsuario(usuarioInexistente));
    }

    // --- Testes de Atualização de Usuário ---
    @Test
    void testAtualizarUsuario_deveAtualizarNoArquivoCorretamente() throws PersistenciaException, ValidacaoUsuarioException {
        List<Usuario> usuarios = serviceUsuario.getUsuarios();
        Usuario usuarioAtualizado = usuarios.get(1);
        String nome = usuarioAtualizado.getNome() + "_atualizado";
        usuarioAtualizado.setNome(usuarioAtualizado.getNome() + "_atualizado");

        serviceUsuario.atualizarUsuario(usuarioAtualizado);

        // Cria uma NOVA instância para verificar os dados no arquivo
        DadosUsuario dadosParaVerificar = new DadosUsuario(FILE_TEST_PATH);

        // Busca o usuário atualizado a partir da nova instância
        Usuario usuarioVerificado = dadosParaVerificar.getUsuariosMap().get(usuarioAtualizado.getId());
        // Verifica se os dados foram realmente atualizados
        assertNotNull(usuarioVerificado);
        assertEquals(nome, usuarioVerificado.getNome());
    }

    // --- Testes de Leitura de Usuários ---
    @Test
    void testGetUsuarios_deveRetornarTodosOsUsuarios() {
        List<Usuario> usuarios = serviceUsuario.getUsuarios();
        assertEquals(3, usuarios.size());
    }

    @Test
    void testListarGerentes_deveRetornarApenasGerentes() {
        List<Gerente> gerentes = serviceUsuario.listarGerentes();
        assertEquals(1, gerentes.size());
        assertEquals("gerente@example.com", gerentes.get(0).getEmail());
    }

    @Test
    void testGetUsuarioById_deveRetornarUsuarioCorreto() throws ValidacaoUsuarioException {
        Usuario vendedor = new Vendedor("Vendedor 78", "vendedor78@example.com", "SenhaTeste123", "27822222222");
        serviceUsuario.addUsuario(vendedor);
        Usuario usuario = serviceUsuario.getUsuarioById(vendedor.getId());
        assertNotNull(usuario);
        assertEquals(vendedor.getNome(), usuario.getNome());
    }

    @Test
    void testGetUsuarioById_comIdInexistente_deveRetornarNulo() {
        assertNull(serviceUsuario.getUsuarioById("id_nao_existe"));
    }

    @Test
    void testGetUsuariosPorLoja_deveRetornarUsuariosCorretos() {
        List<Usuario> usuarios = serviceUsuario.getUsuariosPorLoja(lojaComUsuarios);
        assertEquals(2, usuarios.size());
    }

    @Test
    void testGetUsuariosPorLoja_comLojaSemUsuarios_deveRetornarListaVazia() {
        List<Usuario> usuarios = serviceUsuario.getUsuariosPorLoja(lojaSemUsuarios);
        assertTrue(usuarios.isEmpty());
    }

//    @Test
//    void testRebaixarGerenteParaVendedor_deveMudarOTipoDoUsuario() throws PersistenciaException, ValidacaoUsuarioException {
//        Gerente gerenteOriginal = (Gerente) serviceUsuario.getUsuarioById("11111111111");
//        assertNotNull(gerenteOriginal);
//
//        serviceUsuario.rebaixarGerenteParaVendedor(gerenteOriginal);
//
//        Usuario usuarioVerificado = dadosUsuario.buscarPorId(gerenteOriginal.getId()).orElse(null);
//
//        assertNotNull(usuarioVerificado);
//        assertTrue(usuarioVerificado instanceof Vendedor);
//        assertFalse(usuarioVerificado instanceof Gerente);
//    }
}