package Model;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Franquia {
    private String id;
    private String nome;
    private String endereco;
    private String telefone;
    private List<String> idLojas;


    public Franquia(String nome, String endereco, String telefone) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.id = UUID.randomUUID().toString();
        this.idLojas = new ArrayList<>();
    }

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
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public List<String> getIdLojas() {
        return idLojas;
    }
    public void setIdLojas(List<String> idLojas) {
        this.idLojas = idLojas;
    }

    public void adicionarIdLoja(String lojaId) {
        if (this.idLojas == null) {
            this.idLojas = new ArrayList<>();
        }
        this.idLojas.add(lojaId);
    }
}
