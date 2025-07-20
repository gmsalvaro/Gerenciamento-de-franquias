package Service;

import Model.Dono;
import Model.Gerente;
import Model.Usuario;
import Model.Vendedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GerenciamentoUsuarios {
    private final String USUARIOS_FILE = "usuarios.json";
    private List<Usuario> usuarios;
    Dados dados;

    public GerenciamentoUsuarios(Dados dados) {
        this.dados = dados;
        usuarios = dados.carregarUsuarios(USUARIOS_FILE);
    }

    public List<Usuario> getUsuarios() {
        return  new ArrayList<>(usuarios);
    }

    public Optional<Usuario> getUsuarioID(String id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }
    public Optional<Usuario> getUsuarioNome(String Nome) {
        return usuarios.stream().filter(u -> u.getNome().equals(Nome)).findFirst();
    }

    public Optional<Usuario> login(String email, String senha) { //Login simples
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha))
                .findFirst();
    }

    public void addUsuario(Usuario usuario) {
        if (usuario.getId() == null || usuario.getId().isEmpty()) {
            usuario.setId(UUID.randomUUID().toString());
        }
        usuarios.add(usuario);
        dados.salvarDados(USUARIOS_FILE, usuarios);
    }

    public boolean removeUsuario(String id) {
        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));
        if (removed) {
            dados.salvarDados(USUARIOS_FILE, usuarios);
        }
        return removed;
    }

    public boolean updateUsuario(Usuario updatedUser) {
        boolean flag = false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(updatedUser.getId())) {
                usuarios.set(i, updatedUser);
                flag = true;
                break;
            }
        }
        if (flag) {
            dados.salvarDados(USUARIOS_FILE, usuarios);
        }
        return flag;
    }
}
