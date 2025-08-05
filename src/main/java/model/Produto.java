package model;

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

    public Produto(String nome, BigDecimal preco, int quantidade, String descricao) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = quantidade;
        this.descricao = descricao;
        this.id = UUID.randomUUID().toString();
    }

    /// id loja
    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }
    public String getIdLoja() {
        return idLoja;
    }

    /// nome
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

    /// estoque
    public int getEstoque() {
        return estoque;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    /// descricao
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDescricao() {return descricao;}


    public void setId(String id) {
        this.id = id;
    }
}

