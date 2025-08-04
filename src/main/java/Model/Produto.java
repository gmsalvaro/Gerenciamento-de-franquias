package Model;

import java.math.BigDecimal;
import java.util.UUID;

public class Produto {
    private String nome;
    private String id;
    private String descricao;
    private BigDecimal preco;
    private int estoque;
    String idLoja;

    public Produto() {
    }

    public Produto(String nome, BigDecimal preco, int estoque, String descricao) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.estoque = estoque;
        this.id = UUID.randomUUID().toString();
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

    ///id produto
    public String getId(){
        return this.id;
    }

    /// preco
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

    /// descricao
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDescricao() {return descricao;}


    }
