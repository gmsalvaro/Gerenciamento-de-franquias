package Service;

import Dados.DadosPedidos; // Certifique-se de que esta é a classe correta para lidar com Pedidos
import Model.Pedido; // Importe a classe Pedido
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicePedido {
    private final String FILE_PEDIDOS;
    private DadosPedidos dadosPedidos;
    private Map<String, Pedido> pedidoMap;

    public ServicePedido(String FILE_PEDIDOS) throws PersistenciaException {
        this.FILE_PEDIDOS = FILE_PEDIDOS;
        this.dadosPedidos = new DadosPedidos(FILE_PEDIDOS);
        this.pedidoMap = dadosPedidos.getPedidosMap();
    }

    public void addPedido(Pedido pedido) throws PersistenciaException {

        // if (pedidoMap.containsKey(pedido.getId())) {
        //     throw new PersistenciaException("Pedido com ID '" + pedido.getId() + "' já existe.");
        // }
        dadosPedidos.adicionar(pedido);
        this.pedidoMap = dadosPedidos.getPedidosMap();
    }

    public void removerPedido(Pedido pedido) throws PersistenciaException {
        if (pedidoMap.containsKey(pedido.getId())) {
            dadosPedidos.remover(pedido.getId());
            this.pedidoMap = dadosPedidos.getPedidosMap();
        } else {
            throw new PersistenciaException("Pedido '" + pedido.getId() + "' não encontrado para remoção.");
        }
    }

    public Pedido getPedidoById(String idPedido) {
        return pedidoMap.get(idPedido);
    }

    public Map<String, Pedido> getPedidoMap() {
        return pedidoMap;
    }

    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidoMap.values());
    }

    public List<Pedido> listarPorIDLoja(String idLoja) throws PersistenciaException{
        List<Pedido> pedidosDaLoja = new ArrayList<>();
        for(Pedido p : pedidoMap.values()) {
            if (p.getIdLoja().equals(idLoja)) {
                pedidosDaLoja.add(p);
            }
        }
        return pedidosDaLoja;
    }

    public void atualizarPedido(Pedido pedidoAtualizado) throws PersistenciaException {
        if (pedidoMap.containsKey(pedidoAtualizado.getId())) {
            dadosPedidos.atualizar(pedidoAtualizado);
            this.pedidoMap = dadosPedidos.getPedidosMap();
        } else {
            throw new PersistenciaException("Pedido não encontrado para atualização.");
        }
    }
}