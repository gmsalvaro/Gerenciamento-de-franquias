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
    private String status;
    private String idVendedor;
    private BigDecimal precoTotal;


    public Pedido(String idLoja, Map<String, Integer> produtosNoPedido, Date dataPedido, String status, String idVendedor,  BigDecimal precoTotal) {
        this.id = UUID.randomUUID().toString();
        this.idVendedor = idVendedor;
        this.precoTotal = precoTotal;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
        this.produtosNoPedido = new HashMap<>(produtosNoPedido);
        this.dataPedido = dataPedido;
        this.status = status;
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

    public void setId(String id) {this.id = id;}

    public String getIdLoja() {
        return idLoja;
    }

    public Map<String, Integer> getProdutosNoPedido() {
        return new HashMap<>(produtosNoPedido);
    }

    public Date getDataPedido() {
        return dataPedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}