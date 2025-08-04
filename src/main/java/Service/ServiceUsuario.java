package Service;

import Dados.*;
import Model.*;
import exception.autenticacao.SenhaInvalidaException;
import exception.autenticacao.UsuarioInvalidoException;
import exception.persistencia.PersistenciaException;
import exception.usuario.CPFInvalidoException;
import exception.usuario.EmailInvalidoException;
import exception.usuario.ValidacaoUsuarioException;
import Model.Vendedor;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceUsuario{
    private final String FILE_USUARIOS;
    private final DadosUsuario dadosUsuarios;
    private  ValidadorEmail validadorEmail;
    private ValidadorCPF validadorCPF;
    private ValidadorSenha validadorSenha;

    public ServiceUsuario(String FILE_USUARIOS)  {
        this.FILE_USUARIOS = FILE_USUARIOS;
        this.dadosUsuarios = new DadosUsuario(FILE_USUARIOS);
        this.validadorEmail = new ValidadorEmail();
        this.validadorCPF = new ValidadorCPF();
        this.validadorSenha = new ValidadorSenha();
    }

    public List<Usuario> getUsuariosPorLoja(Loja loja) {
        List<Usuario> usuarios = new ArrayList<>();
        if (loja != null && loja.getIdsUsuarios() != null) {
            for (String idUsuario : loja.getIdsUsuarios()) {
                Usuario u = dadosUsuarios.listarMap().get(idUsuario);
                if (u != null) {
                    usuarios.add(u);
                }
            }
        }
        return usuarios;
    }

    public List<Vendedor> getVendedoresPorLoja(Loja loja) {
        return getUsuariosPorLoja(loja).stream()
                .filter(usuario -> usuario instanceof Vendedor)
                .map(usuario -> (Vendedor) usuario)
                .collect(Collectors.toList());
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(dadosUsuarios.listarMap().values());
    }

    public List<Gerente> listarGerentes() {
        return getUsuarios().stream()
                .filter(u -> u instanceof Gerente)
                .map(u -> (Gerente) u)
                .collect(Collectors.toList());
    }

    public List<Vendedor> listarVendedores() {
        return getUsuarios().stream()
                .filter(u-> u instanceof Vendedor)
                .map(u ->(Vendedor)u )
                .collect(Collectors.toList());
    }

    public void adicionar(Usuario usuario) throws ValidacaoUsuarioException {
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

        for (Usuario u : dadosUsuarios.listarMap().values()) {
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
    }

    public void removeUsuario(Usuario usuario) throws PersistenciaException {
        if (dadosUsuarios.listarMap().containsKey(usuario.getId())) {
            dadosUsuarios.remover(usuario.getId());
        } else {
            throw new PersistenciaException("Usuário '" + usuario.getNome() + "' não encontrado para remoção.");
        }
    }

    public void atualizarUsuario(Usuario usuarioAtualizado) throws PersistenciaException {
        if (dadosUsuarios.listarMap().containsKey(usuarioAtualizado.getId())) {
            dadosUsuarios.atualizar(usuarioAtualizado);
        } else {
            throw new PersistenciaException("Usuário '" + usuarioAtualizado.getNome() + "' não encontrado para atualização.");
        }
    }

    public Usuario autenticarUsuario(String email, String senha) throws UsuarioInvalidoException {
        for (Usuario usuario : dadosUsuarios.listarMap().values()) {
            if (usuario.getEmail().equalsIgnoreCase(email) && usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }
        throw new UsuarioInvalidoException("Email ou senha inválidos.");
    }

    public Usuario getUsuarioById(String idUsuario) {
        return dadosUsuarios.listarMap().get(idUsuario);
    }

    public Optional<Usuario> buscarPorId(String id) {
        return dadosUsuarios.buscarPorId(id);
    }

    public void rebaixarGerenteParaVendedor(Gerente gerente) throws PersistenciaException {
        if (gerente == null) {
            throw new IllegalArgumentException("Gerente não pode ser nulo.");
        }
        Vendedor novoVendedor = new Vendedor(
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getSenha(),
                gerente.getCpf()
        );
        novoVendedor.setId(gerente.getId());
        dadosUsuarios.atualizar(novoVendedor);
    }

    public List<Gerente> listarGerentesDisponiveis(ServiceLoja serviceLoja) {
        return listarGerentes().stream()
                .filter(gerente -> serviceLoja.buscarLojaPorUsuario(gerente).isEmpty())
                .collect(Collectors.toList());
    }
}