package Dados;

import Model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DadosUsuario {
    private final String USUARIO_FILE;
    private final ObjectMapper mapper;
    private Map<String, Usuario> usuariosMap;

    public DadosUsuario(String filePath) {
        this.USUARIO_FILE = filePath;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }

    private void carregar() {
        File file = new File(USUARIO_FILE);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(USUARIO_FILE), "[]".getBytes());
            } catch (IOException e) {
                System.err.println("Erro ao inicializar arquivo de usuários: " + e.getMessage());
            }
            usuariosMap = new ConcurrentHashMap<>();
            return;
        }
        try {
            List<Usuario> lista = mapper.readValue(file, new TypeReference<List<Usuario>>() {});
            usuariosMap = new ConcurrentHashMap<>();
            lista.forEach(usuario -> usuariosMap.put(usuario.getId(), usuario));
        } catch (MismatchedInputException e) {
            System.err.println("Erro no formato do arquivo de usuários: " + e.getMessage());
            usuariosMap = new ConcurrentHashMap<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
            usuariosMap = new ConcurrentHashMap<>();
        }
    }

    private void salvar() {
        try {
            mapper.writeValue(new File(USUARIO_FILE), new ArrayList<>(usuariosMap.values()));
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodas() {
        return new ArrayList<>(usuariosMap.values());
    }

    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(usuariosMap.get(id));
    }

    public List<Usuario> buscar(Predicate<Usuario> condicao) {
        return usuariosMap.values().stream()
                .filter(condicao)
                .collect(Collectors.toList());
    }

    public boolean existeCpf(String cpf) {
        return usuariosMap.values().stream()
                .anyMatch(u -> u.getCpf().equals(cpf));
    }

    public void adicionar(Usuario usuario) {
        if (usuariosMap.containsKey(usuario.getId())) {
            System.err.println("Erro: Usuário com ID " + usuario.getId() + " já existe. Use 'atualizar' para modificar.");
            return;
        }
        usuariosMap.put(usuario.getId(), usuario);
        salvar();
    }

    public void atualizar(Usuario usuarioAtualizado) {
        if (usuariosMap.containsKey(usuarioAtualizado.getId())) {
            usuariosMap.put(usuarioAtualizado.getId(), usuarioAtualizado);
            salvar();
        } else {
            System.err.println("Erro: Usuário com ID " + usuarioAtualizado.getId() + " não encontrado para atualização.");
        }
    }

    public void remover(String id) {
        if (usuariosMap.remove(id) != null) {
            salvar();
        } else {
            System.err.println("Erro: Usuário com ID " + id + " não encontrado para remoção.");
        }
    }

    public void remover(Predicate<Usuario> condicao) {
        List<String> idsParaRemover = usuariosMap.values().stream()
                .filter(condicao)
                .map(Usuario::getId)
                .collect(Collectors.toList());

        idsParaRemover.forEach(usuariosMap::remove);

        if (!idsParaRemover.isEmpty()) {
            salvar();
        }
    }
}