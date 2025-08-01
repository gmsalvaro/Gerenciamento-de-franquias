package Model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pedido implements Serializable, Entidade {

    private String id;
    private String idLoja;
    private Map<String, Integer> produtosNoPedido;
    private Date dataPedido;
    private String status;


    public Pedido(String idLoja, Map<String, Integer> produtosNoPedido, Date dataPedido, String status) {
        this.id = UUID.randomUUID().toString();
        this.idLoja = idLoja;
        this.produtosNoPedido = new HashMap<>(produtosNoPedido);
        this.dataPedido = dataPedido;
        this.status = status;
    }

    @Override
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}