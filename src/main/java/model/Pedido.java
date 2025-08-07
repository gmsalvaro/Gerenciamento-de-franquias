package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B


public class Pedido implements Serializable {

    private String id;
    private String idLoja;
    private Map<String, Integer> produtosNoPedido;
    private Date dataPedido;
    private StatusPedido status;
    private String idVendedor;
    private BigDecimal precoTotal;
    private FormaDePagamento formaDePagamento;
    private String justificativa;
    private Cliente cliente;

    public Pedido(){
        this.produtosNoPedido = new HashMap<>();
    } //contrutor pro JSON

    public Pedido(String idLoja, Map<String, Integer> produtosNoPedido, Date dataPedido, StatusPedido status, String idVendedor,  BigDecimal precoTotal, FormaDePagamento formaDePagamento, Cliente cliente) {
        this.id = UUID.randomUUID().toString();
        this.idLoja = idLoja;
        this.produtosNoPedido = new HashMap<>(produtosNoPedido);
        this.dataPedido = dataPedido;
        this.status = status;
        this.idVendedor = idVendedor;
        this.precoTotal = precoTotal;
        this.formaDePagamento = formaDePagamento;
        this.cliente = cliente;
    }
    //getters e setters

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
    public String getJustificativa() {
        return justificativa;
    }

    public void setEstoque(String id, Integer estoque) {
        produtosNoPedido.put(id, estoque);
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public String getIdVendedor(){
        return this.idVendedor;
    }
    public void setIdVendedor(String idVendedor){
        this.idVendedor = idVendedor;
    }


    public String getId() {
        return id;
    }

    public String getIdLoja() {
        return idLoja;
    }

    public Map<String, Integer> getProdutosNoPedido() {
        return new HashMap<>(produtosNoPedido);
    }

    public Date getDataPedido() {
        return dataPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public void setPrecoTotal(BigDecimal novoPrecoTotal) {
        this.precoTotal = novoPrecoTotal;
    }

    public void setProdutosNoPedido(Map<String, Integer> novosProdutosNoPedido) {
        if (novosProdutosNoPedido == null) {
            this.produtosNoPedido = new HashMap<>();
        } else {
            this.produtosNoPedido = new HashMap<>(novosProdutosNoPedido);
        }
    }

    public Cliente getCliente() { return cliente;}

    public void setId(String id) {
        this.id = id;
    }
}