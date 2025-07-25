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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DadosUsuario {
    private final String USUARIO_FILE;
    private final ObjectMapper mapper;

    public DadosUsuario(String filePath) {
        this.USUARIO_FILE = filePath;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        carregar();
    }

    private List<Usuario> carregar() {
        File file = new File(USUARIO_FILE);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(USUARIO_FILE), "[]".getBytes());
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de usuários: " + e.getMessage());
            }
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Usuario>>() {});
        } catch (MismatchedInputException e) {
            System.err.println("Erro no formato do arquivo de usuários: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void salvar(List<Usuario> usuarios) {
        try {
            mapper.writeValue(new File(USUARIO_FILE), usuarios);
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodas() {
        return carregar();
    }

    public boolean existeCpf(String cpf) {
        for (Usuario u : carregar()) {
            if (u.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }

    public void adicionar(Usuario usuario) {
        List<Usuario> lista = carregar();
        lista.add(usuario);
        salvar(lista);
    }

    public void remover(Usuario usuario) {
        List<Usuario> lista = carregar();
        lista.remove(usuario);
        salvar(lista);
    }
}
