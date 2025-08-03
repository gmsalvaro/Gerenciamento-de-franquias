package Persistencia.Test;

import Model.Usuario;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// ====================================================================================
// Mocks para a classe Usuario e suas subclasses para permitir a execução dos testes
// A classe Usuario real e suas subclasses devem ser fornecidas para testes de integração
// ====================================================================================
@JsonTypeName("dono")
class Dono extends Usuario {
    public Dono() {}
    public Dono(String nome, String email, String senha, String cpf, int permissao) {
        super(nome, email, senha, cpf, permissao);
    }
}

@JsonTypeName("gerente")
class Gerente extends Usuario {
    public Gerente() {}
    public Gerente(String nome, String email, String senha, String cpf, int permissao) {
        super(nome, email, senha, cpf, permissao);
    }
}

@JsonTypeName("vendedor")
class Vendedor extends Usuario {
    public Vendedor() {}
    public Vendedor(String nome, String email, String senha, String cpf, int permissao) {
        super(nome, email, senha, cpf, permissao);
    }
}


/**
 * Classe de teste para a persistência de dados de usuários em um arquivo JSON.
 */
class DadosUsuarioTest {

    private static final String TEST_FILE_PATH = "usuarios_test.json";
    private DadosUsuario dadosUsuario;

    @BeforeEach
    void setup() throws IOException {
        // Garante que o arquivo não existe antes de cada teste
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        dadosUsuario = new DadosUsuario(TEST_FILE_PATH);
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
    void testAdicionarEListarUsuario() {
        // Cria um novo usuário
        Usuario usuario = new Dono("João da Silva", "joao@email.com", "senha123", "123.456.789-00", 1);
        dadosUsuario.adicionar(usuario);

        // Verifica se o mapa contém o novo usuário
        Map<String, Usuario> usuarios = dadosUsuario.getUsuariosMap();
        assertEquals(1, usuarios.size());
        assertTrue(usuarios.containsKey(usuario.getId()));
        assertEquals("João da Silva", usuarios.get(usuario.getId()).getNome());
    }

    @Test
    void testBuscarUsuarioPorId() {
        // Cria e adiciona um usuário
        Usuario usuario = new Gerente("Maria Gerente", "maria@email.com", "senha456", "987.654.321-11", 2);
        dadosUsuario.adicionar(usuario);

        // Busca o usuário por ID
        Optional<Usuario> usuarioEncontrado = dadosUsuario.buscarPorId(usuario.getId());
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals(usuario.getId(), usuarioEncontrado.get().getId());

        // Busca por um ID inexistente
        Optional<Usuario> usuarioNaoEncontrado = dadosUsuario.buscarPorId(UUID.randomUUID().toString());
        assertFalse(usuarioNaoEncontrado.isPresent());
    }

    @Test
    void testAtualizarUsuarioExistente() {
        // Cria e adiciona um usuário inicial
        Usuario usuarioOriginal = new Vendedor("Pedro Vendedor", "pedro@email.com", "senha789", "111.222.333-44", 3);
        dadosUsuario.adicionar(usuarioOriginal);

        // Cria um usuário com o mesmo ID, mas com dados atualizados
        Usuario usuarioAtualizado = new Vendedor("Pedro Atualizado", "pedro_new@email.com", "new_pass", "111.222.333-44", 3);
        usuarioAtualizado.setId(usuarioOriginal.getId());

        // Atualiza o usuário
        dadosUsuario.atualizar(usuarioAtualizado);

        // Verifica se o usuário foi atualizado
        Map<String, Usuario> usuarios = dadosUsuario.getUsuariosMap();
        assertEquals(1, usuarios.size());
        assertEquals("Pedro Atualizado", usuarios.get(usuarioOriginal.getId()).getNome());
        assertEquals("pedro_new@email.com", usuarios.get(usuarioOriginal.getId()).getEmail());
    }

    @Test
    void testAtualizarUsuarioNaoExistente() {
        // Tenta atualizar um usuário que não existe
        Usuario usuario = new Dono("Inexistente", "email@email.com", "senha", "000.000.000-00", 1);
        assertDoesNotThrow(() -> dadosUsuario.atualizar(usuario));
    }

    @Test
    void testRemoverUsuarioExistente() {
        // Cria e adiciona um usuário
        Usuario usuario = new Dono("Remover Teste", "remover@email.com", "senha", "999.888.777-66", 1);
        dadosUsuario.adicionar(usuario);

        // Remove o usuário
        dadosUsuario.remover(usuario.getId());

        // Verifica se o usuário foi removido
        Map<String, Usuario> usuarios = dadosUsuario.getUsuariosMap();
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void testRemoverUsuarioNaoExistente() {
        // Tenta remover um usuário com um ID inexistente
        String idInexistente = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> dadosUsuario.remover(idInexistente));
    }

    @Test
    void testCarregarArquivoCorrompido() throws IOException {
        // Cria um arquivo com JSON inválido
        FileWriter writer = new FileWriter(TEST_FILE_PATH);
        writer.write("isso não é um json valido");
        writer.close();

        // Tenta carregar o arquivo, o que não deve lançar exceção mas inicializar o mapa vazio
        DadosUsuario dadosUsuarioCorrompido = new DadosUsuario(TEST_FILE_PATH);
        assertTrue(dadosUsuarioCorrompido.getUsuariosMap().isEmpty());
    }

    @Test
    void testInicializarComArquivoVazio() {
        // O setup já cria um arquivo vazio, então o mapa deve estar vazio
        assertTrue(dadosUsuario.getUsuariosMap().isEmpty());
    }

//    @Test
//    void testBuscarComNome() {
//        // Adiciona vários usuários
//        dadosUsuario.adicionar(new Dono("Dono A", "donoA@email.com", "senha", "11111", 1));
//        dadosUsuario.adicionar(new Dono("Dono B", "donoB@email.com", "senha", "22222", 1));
//        dadosUsuario.adicionar(new Gerente("Gerente C", "gerenteC@email.com", "senha", "33333", 2));
//        dadosUsuario.adicionar(new Vendedor("Vendedor D", "vendedorD@email.com", "senha", "44444", 3));
//
//
//        // Busca por nome
//        List<Usuario> gerentes = dadosUsuario.buscar(u -> u.getNome().startsWith("Gerente"));
//        assertEquals(1, gerentes.size());
//        assertEquals("Gerente C", gerentes.get(0).getNome());
//    }

    @Test
    void testExisteCpf() {
        // Adiciona um usuário com CPF
        Usuario usuario = new Dono("Teste CPF", "cpf@email.com", "senha", "123.456.789-00", 1);
        dadosUsuario.adicionar(usuario);

        // Verifica se o CPF existe
        assertTrue(dadosUsuario.existeCpf("123.456.789-00"));
        assertFalse(dadosUsuario.existeCpf("000.000.000-00"));
    }

    @Test
    void testRemoverComPredicado() { //Nao tem tanta funcionalidade assim !
        // Adiciona vários usuários
        dadosUsuario.adicionar(new Dono("Dono A", "donoA@email.com", "senha", "11111", 1));
        dadosUsuario.adicionar(new Dono("Dono B", "donoB@email.com", "senha", "22222", 1));
        dadosUsuario.adicionar(new Gerente("Gerente C", "gerenteC@email.com", "senha", "33333", 2));

        // Remove todos os donos (permissao 1)
        dadosUsuario.remover(u -> u.getPermissao() == 1);

        // Verifica se os donos foram removidos
        Map<String, Usuario> usuarios = dadosUsuario.getUsuariosMap();
        assertEquals(1, usuarios.size());
        assertTrue(usuarios.values().stream().anyMatch(u -> u.getNome().equals("Gerente C")));
    }
}
