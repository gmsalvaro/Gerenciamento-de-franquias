package Model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pedido implements Serializable {

    private String id;
    private String idLoja;
    private Map<String, Integer> produtosNoPedido;
    private Date dataPedido;
    private StatusPedido status;
    private String idVendedor;
    private BigDecimal precoTotal;
    private FormaDePagamento formaDePagamento;

    public Pedido(){
        this.produtosNoPedido = new HashMap<>();
    }

    public Pedido(String idLoja, Map<String, Integer> produtosNoPedido, Date dataPedido, StatusPedido status, String idVendedor,  BigDecimal precoTotal, FormaDePagamento formaDePagamento) {
        this.id = UUID.randomUUID().toString();
        this.idLoja = idLoja;
        this.produtosNoPedido = new HashMap<>(produtosNoPedido); // Cria uma cópia segura
        this.dataPedido = dataPedido;
        this.status = status;
        this.idVendedor = idVendedor;
        this.precoTotal = precoTotal;
        this.formaDePagamento = formaDePagamento;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
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

}