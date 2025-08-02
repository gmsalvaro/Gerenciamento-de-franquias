package Model;

import java.math.BigDecimal;
import java.util.UUID;

public class Produto implements Entidade{
    private String nome;
    private String id;
    private BigDecimal preco;
    private int estoque;
    private String idLoja;

    public Produto() {
    }

    public Produto(String nome, BigDecimal preco) {
        this.nome = nome;
        this.preco = preco;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getId(){
        return this.id;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }

    public String getIdLoja() {
        return idLoja;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

}
