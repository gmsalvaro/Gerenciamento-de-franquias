package Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo" // Este campo é crucial!
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dono.class, name = "dono"),
        @JsonSubTypes.Type(value = Gerente.class, name = "gerente"),
        @JsonSubTypes.Type(value = Vendedor.class, name = "vendedor")
})

public abstract class Usuario {
    private String id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private int permissao;
    private String senha;

    // Construtor padrão
    public Usuario() {}

    public Usuario(String nome, String email, String senha, String cpf, int permissao) {
        this.nome = nome;
        this.permissao = permissao;
        this.email = email;
        this.senha = senha;
        this.telefone = "123456789";
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

    public int getPermissao() {return permissao;}

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelefone() {
        return telefone;
    }

    ;
}