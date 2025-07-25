package Model;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Loja {
    private String id;
    private String nome;
    private String endereco;
    private String franquiaId;

    private List<String> idPedidos;
    private List<String> idProdutos;

    public Loja() {
        this.id = UUID.randomUUID().toString();
        this.idPedidos = new ArrayList<>();
        this.idProdutos = new ArrayList<>();
    }

    public Loja(String nome, String endereco, String franquiaId) {
        this();
        this.nome = nome;
        this.endereco = endereco;
        this.franquiaId = franquiaId;
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
    public String getFranquiaId() {
        return franquiaId;
    }
    public void setFranquiaId(String franquiaId) {
        this.franquiaId = franquiaId;
    }
    public List<String> getIdPedidos() {
        return idPedidos;
    }
    public void setIdPedidos(List<String> idPedidos) {
        this.idPedidos = idPedidos;
    }
    public List<String> getIdProdutos() {
        return idProdutos;
    }
    public void setIdProdutos(List<String> idProdutos) {
        this.idProdutos = idProdutos;
    }

    public void adicionarIdPedido(String pedidoId) {
        if (this.idPedidos == null) this.idPedidos = new ArrayList<>();
        this.idPedidos.add(pedidoId);
    }
    public void adicionarIdProduto(String produtoId) {
        if (this.idProdutos == null) this.idProdutos = new ArrayList<>();
        this.idProdutos.add(produtoId);
    }
}