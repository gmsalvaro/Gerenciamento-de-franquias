package Model;

import java.util.UUID;

public class Pedido {
    private String nome;
    private String id;
    private double preco;
    private int estoque;
    String idLoja;


    public Pedido(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
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

    public String getId(){
        return this.id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

}
