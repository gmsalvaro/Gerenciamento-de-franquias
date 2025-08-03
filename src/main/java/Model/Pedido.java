package Model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class Pedido {
    private String id;
    private String idLoja;
    private Map<String, Integer> produtosNoPedido;
    private Date dataPedido;
    private String status;

    // Construtor padrão necessário para o Jackson
    public Pedido() {
        this.id = UUID.randomUUID().toString();
        this.produtosNoPedido = new HashMap<>();
    }

    public Pedido(String idLoja, Map<String, Integer> produtosNoPedido, Date dataPedido, String status) {
        this();
        this.idLoja = idLoja;
        this.produtosNoPedido = new HashMap<>(produtosNoPedido);
        this.dataPedido = dataPedido;
        this.status = status;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }
}
