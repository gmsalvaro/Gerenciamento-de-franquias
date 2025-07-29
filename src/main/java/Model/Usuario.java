package Model;
import java.util.UUID;

public  class Usuario {
    private String id;
    private String nome;
    private String email;
    private String cpf;
    private int permissao;
    private String senha;

    // Construtor padrão
    public Usuario() {}

    public Usuario(String nome, String email, String senha, String cpf, int permissao) {
        this.nome = nome;
        this.permissao = permissao;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.id = UUID.randomUUID().toString();; // criar uma logica para gerar id aleatorio !
    }

    // Getters e Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setPermissao(int permissao) {
        this.permissao = permissao;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public  void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getPermissao() {return permissao;} ;
}