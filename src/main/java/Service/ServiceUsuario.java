package Service;

import Dados.*;
import Model.*;
import exception.ValidacaoException;
import exception.autenticacao.AutenticacaoException;
import exception.autenticacao.SenhaInvalidaException;
import exception.autenticacao.UsuarioInvalidoException;
import exception.persistencia.PersistenciaException;
import exception.usuario.CPFInvalidoException;
import exception.usuario.EmailInvalidoException;
import exception.usuario.UsuarioJaExistenteException;
import exception.usuario.ValidacaoUsuarioException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceUsuario{
    private final String FILE_USUARIOS;
    private final DadosUsuario dadosUsuarios;
    private Map<String, Usuario> usuarioMap;
    private  ValidadorEmail validadorEmail;
    private ValidadorCPF validadorCPF;
    private ValidadorSenha validadorSenha;

    public ServiceUsuario(String FILE_USUARIOS)  {
        this.FILE_USUARIOS = FILE_USUARIOS;
        this.dadosUsuarios = new DadosUsuario(FILE_USUARIOS);
        this.usuarioMap = dadosUsuarios.getUsuariosMap();
        this.validadorEmail = new ValidadorEmail();
        this.validadorCPF = new ValidadorCPF();
        this.validadorSenha = new ValidadorSenha();
    }

    public List<Usuario> getUsuariosPorLoja(Loja loja) {
        List<Usuario> usuarios = new ArrayList<>();
        if (loja != null && loja.getIdsUsuarios() != null) {
            for (String idUsuario : loja.getIdsUsuarios()) {
                Usuario u = usuarioMap.get(idUsuario);
                if (u != null) {
                    usuarios.add(u);
                }
            }
        }
        return usuarios;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarioMap.values());
    }

    public void addUsuario(Usuario usuario) throws ValidacaoUsuarioException {

        try{
            validadorCPF.validar(usuario.getCpf());
            validadorEmail.validar(usuario.getEmail());
            validadorSenha.validar(usuario.getSenha());
        }catch(CPFInvalidoException | EmailInvalidoException | SenhaInvalidaException e){
            switch(e){
                case CPFInvalidoException cpfE -> throw new CPFInvalidoException("CPF invalido");
                case EmailInvalidoException emailE -> throw new EmailInvalidoException("Email invalido");
                //case SenhaInvalidaException senhaE -> throw new SenhaInvalidaException("Senha invalida");
                default -> throw new IllegalStateException("ERRO INESPERADO: " + e.getMessage());
            }
        }



        for (Usuario u : usuarioMap.values()) {
            if (u.getNome().equalsIgnoreCase(usuario.getNome())) {
                throw new ValidacaoUsuarioException("Usuário com o nome '" + usuario.getNome() + "' já existe.");
            }
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                throw new ValidacaoUsuarioException("Usuário com o email '" + usuario.getEmail() + "' já existe.");
            }
            if(u.getCpf().equals(usuario.getCpf())){
                throw new CPFInvalidoException("Usuario com o CPF '"+ usuario.getCpf()+"' já existe.");
            }
        }

        dadosUsuarios.adicionar(usuario);
        this.usuarioMap = dadosUsuarios.getUsuariosMap();
    }

    public void removeUsuario(Usuario usuario) throws PersistenciaException {
        if (usuarioMap.containsKey(usuario.getId())) {
            dadosUsuarios.remover(usuario.getId());
            this.usuarioMap = dadosUsuarios.getUsuariosMap();
        } else {
            throw new PersistenciaException("Usuário '" + usuario.getNome() + "' não encontrado para remoção.");
        }
    }

    public void atualizarUsuario(Usuario usuarioAtualizado) throws PersistenciaException {
        if (usuarioMap.containsKey(usuarioAtualizado.getId())) {
            dadosUsuarios.atualizar(usuarioAtualizado);
            this.usuarioMap = dadosUsuarios.getUsuariosMap();
        } else {
            throw new PersistenciaException("Usuário '" + usuarioAtualizado.getNome() + "' não encontrado para atualização.");
        }
    }

    public Usuario autenticarUsuario(String email, String senha) throws UsuarioInvalidoException {
        for (Usuario usuario : usuarioMap.values()) {
            if (usuario.getEmail().equalsIgnoreCase(email) && usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }
        throw new UsuarioInvalidoException("Email ou senha inválidos.");
    }

    public Usuario getUsuarioById(String idUsuario) {
        return usuarioMap.get(idUsuario);
    }





}