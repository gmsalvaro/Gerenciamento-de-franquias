package Model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "userType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dono.class, name = "dono"),
        @JsonSubTypes.Type(value = Gerente.class, name = "gerente"),
        @JsonSubTypes.Type(value = Vendedor.class, name = "vendedor")
})

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha; // Adicionado para login
    private String dataContratacao;

    // Construtor padrão
    public Usuario() {}

    public Usuario(String nome, String email, String senha, String dataContratacao) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataContratacao = dataContratacao;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getDataContratacao() {
        return dataContratacao;
    }
    public void setDataContratacao(String dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    //Logica de autenticação;


    public String exibirDetalhes() {
        return "ID: " + id + ", Nome: " + nome + ", Email: " + email + ", Data Contratação: " + dataContratacao;
    }
}