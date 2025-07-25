package Model;

import java.util.List;
import java.util.UUID;

public class Pedido {
    private String cliente;
    private String nome;
    private String id;
    private double preco;
    private String data;
    private int estoque;

    public Pedido(String cliente, String data, int estoque,  String nome, double preco) {
        this.cliente = cliente;
        this.data = data;
        this.nome = nome;
        this.id = UUID.randomUUID().toString();
        this.preco = preco;
        this.estoque = estoque;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId(){
        return this.id;
    }

    public String getCliente() {
        return cliente;
    }

    public int getEstoque(){
        return estoque;
    }

    public double getPreco(){
        return preco;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
