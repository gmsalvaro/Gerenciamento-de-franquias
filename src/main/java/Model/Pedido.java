package Model;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Pedido implements Serializable {

    private String id;
    private String idLoja;
    private Map<String, Integer> produtosNoPedido;
    private String dataPedido;
    private StatusPedido status;
    private formaPagamento formaPagamento;

    public Pedido(String id, Map<String, Integer> produtosNoPedido, String pendente) {
        // necessário para a desserialização JSON via Jackson
    }

    public Pedido(String idLoja, Map<String, Integer> produtosNoPedido, Instant datapedido, StatusPedido status, formaPagamento formaPagamento) {
        this.id = UUID.randomUUID().toString();
        this.idLoja = idLoja;
        this.produtosNoPedido = new HashMap<>(produtosNoPedido);
        this.dataPedido = datapedido.toString();
        this.formaPagamento = formaPagamento;
        this.status = status;
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

    public String getDataPedido() {
        return dataPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public  formaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

}