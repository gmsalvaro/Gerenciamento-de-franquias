package PersistenciaTest;
import repository.DadosUsuario;
import model.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class Dono extends Usuario {
    public Dono() { super(); }
    @JsonCreator
    public Dono(@JsonProperty("nome") String nome, @JsonProperty("email") String email, @JsonProperty("senha") String senha, @JsonProperty("cpf") String cpf) {
        super(nome, email, senha, cpf, 0);
    }
}

class Gerente extends Usuario {
    private String idLojaGerenciada;
    public Gerente() { super(); }
    @JsonCreator
    public Gerente(@JsonProperty("nome") String nome, @JsonProperty("email") String email, @JsonProperty("senha") String senha, @JsonProperty("cpf") String cpf, @JsonProperty("idLojaGerenciada") String idLojaGerenciada) {
        super(nome, email, senha, cpf, 1);
        this.idLojaGerenciada = idLojaGerenciada;
    }
    public String getIdLojaGerenciada() { return idLojaGerenciada; }
    public void setIdLojaGerenciada(String idLojaGerenciada) { this.idLojaGerenciada = idLojaGerenciada; }
}

class Vendedor extends Usuario {
    private String idLojaAssociada;
    public Vendedor() { super(); }
    @JsonCreator
    public Vendedor(@JsonProperty("nome") String nome, @JsonProperty("email") String email, @JsonProperty("senha") String senha, @JsonProperty("cpf") String cpf, @JsonProperty("idLojaAssociada") String idLojaAssociada) {
        super(nome, email, senha, cpf, 2);
        this.idLojaAssociada = idLojaAssociada;
    }
    public String getIdLojaAssociada() { return idLojaAssociada; }
    public void setIdLojaAssociada(String idLojaAssociada) { this.idLojaAssociada = idLojaAssociada; }
}

@DisplayName("Testes para a classe DadosUsuario")
class DadosUsuarioTest {

    private static final String TEMP_FILE_PATH = "usuarios_test.json";
    private DadosUsuario dadosUsuario;

    @BeforeEach
    void setup() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
        dadosUsuario = new DadosUsuario(TEMP_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    @DisplayName("Teste de inicialização: arquivo não existe, deve ser criado vazio")
    void testInicializacaoArquivoNaoExistente() {
        assertTrue(Files.exists(Paths.get(TEMP_FILE_PATH)));
        assertTrue(dadosUsuario.listarTodas().isEmpty());
    }

    @Test
    @DisplayName("Teste de adicionar: deve adicionar um novo usuário e salvar no arquivo")
    void testAdicionarUsuario() {
        Usuario novoUsuario = new Dono("João", "joao@email.com", "senha123", "111.111.111-11");
        dadosUsuario.adicionar(novoUsuario);

        Optional<Usuario> usuarioAdicionado = dadosUsuario.buscarPorId(novoUsuario.getId());
        assertTrue(usuarioAdicionado.isPresent());
        assertEquals(novoUsuario.getNome(), usuarioAdicionado.get().getNome());

        try {
            String conteudoArquivo = Files.readString(Paths.get(TEMP_FILE_PATH));
            assertTrue(conteudoArquivo.contains(novoUsuario.getId()));
        } catch (IOException e) {
            fail("Erro ao ler o arquivo de teste: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar o usuário correto")
    void testBuscarPorIdUsuarioExistente() {
        Usuario usuario1 = new Gerente("Maria", "maria@email.com", "senha123", "222.222.222-22", "loja-x");
        dadosUsuario.adicionar(usuario1);

        Optional<Usuario> resultado = dadosUsuario.buscarPorId(usuario1.getId());
        assertTrue(resultado.isPresent());
        assertEquals(usuario1.getId(), resultado.get().getId());
    }

    @Test
    @DisplayName("Teste de buscarPorId: deve retornar Optional vazio para ID inexistente")
    void testBuscarPorIdUsuarioInexistente() {
        Optional<Usuario> resultado = dadosUsuario.buscarPorId("id-inexistente");
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Teste de atualizar: deve atualizar os dados de um usuário existente")
    void testAtualizarUsuarioExistente() {
        Usuario usuarioExistente = new Vendedor("Pedro", "pedro@email.com", "senha123", "333.333.333-33", "loja-y");
        dadosUsuario.adicionar(usuarioExistente);

        Usuario usuarioAtualizado = new Vendedor("Pedro Vendedor", "pedro_novo@email.com", "novasenha", "333.333.333-33", "loja-y");
        usuarioAtualizado.setId(usuarioExistente.getId());

        dadosUsuario.atualizar(usuarioAtualizado);

        Optional<Usuario> resultado = dadosUsuario.buscarPorId(usuarioExistente.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Pedro Vendedor", resultado.get().getNome());
        assertEquals("pedro_novo@email.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("Teste de atualizar: deve lidar com usuário inexistente sem lançar exceção")
    void testAtualizarUsuarioInexistente() {
        Usuario usuarioInexistente = new Dono("João", "joao@email.com", "senha", "111.111.111-11");

        Assertions.assertDoesNotThrow(() -> dadosUsuario.atualizar(usuarioInexistente));
    }

    @Test
    @DisplayName("Teste de remover: deve remover um usuário existente")
    void testRemoverUsuarioExistente() {
        Usuario usuarioParaRemover = new Dono("Lucas", "lucas@email.com", "senha123", "444.444.444-44");
        dadosUsuario.adicionar(usuarioParaRemover);

        assertTrue(dadosUsuario.buscarPorId(usuarioParaRemover.getId()).isPresent());

        dadosUsuario.remover(usuarioParaRemover.getId());

        assertFalse(dadosUsuario.buscarPorId(usuarioParaRemover.getId()).isPresent());
    }

    @Test
    @DisplayName("Teste de remover: deve lidar com ID inexistente sem lançar exceção")
    void testRemoverUsuarioInexistente() {
        Assertions.assertDoesNotThrow(() -> dadosUsuario.remover("id-nao-existe"));
    }

    @Test
    @DisplayName("Teste de buscar por Predicate: deve retornar a lista de usuários filtrados")
    void testBuscarComPredicate() {
        dadosUsuario.adicionar(new Dono("Dono A", "donoa@email.com", "senha", "111"));
        dadosUsuario.adicionar(new Vendedor("Vendedor B", "vendb@email.com", "senha", "222", "loja-1"));
        dadosUsuario.adicionar(new Vendedor("Vendedor C", "vendc@email.com", "senha", "333", "loja-1"));

        List<Usuario> vendedores = dadosUsuario.buscar(u -> u instanceof Vendedor);
        assertEquals(2, vendedores.size());

        List<Usuario> usuarioComCpf = dadosUsuario.buscar(u -> u.getCpf().equals("111"));
        assertEquals(1, usuarioComCpf.size());
        assertEquals("Dono A", usuarioComCpf.get(0).getNome());
    }

    @Test
    @DisplayName("Teste de existeCpf: deve retornar true se o CPF existir")
    void testExisteCpfExistente() {
        Usuario u1 = new Dono("Dono", "dono@email.com", "senha", "555.555.555-55");
        dadosUsuario.adicionar(u1);

        assertTrue(dadosUsuario.existeCpf("555.555.555-55"));
    }

    @Test
    @DisplayName("Teste de existeCpf: deve retornar false se o CPF não existir")
    void testExisteCpfInexistente() {
        assertFalse(dadosUsuario.existeCpf("999.999.999-99"));
    }
}
