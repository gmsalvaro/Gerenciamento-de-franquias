package Service;

import Dados.DadosPedidos;
import Model.Pedido;
import Model.StatusPedido;
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServicePedido {
    private final String FILE_PEDIDOS;
    private DadosPedidos dadosPedidos;
    private Map<String, Pedido> pedidoMap;

    public ServicePedido(String FILE_PEDIDOS) throws PersistenciaException {
        this.FILE_PEDIDOS = FILE_PEDIDOS;
        this.dadosPedidos = new DadosPedidos(FILE_PEDIDOS);
    }

    public void addPedido(Pedido pedido) throws PersistenciaException {
         if (pedidoMap.containsKey(pedido.getId())) {
             throw new PersistenciaException("Pedido com ID '" + pedido.getId() + "' já existe.");
         }
        dadosPedidos.adicionar(pedido);
    }

    public void remover(Pedido pedido) throws PersistenciaException {
        if (dadosPedidos.listarMap().containsKey(pedido.getId())) {
            dadosPedidos.remover(pedido.getId());
        } else {
            throw new PersistenciaException("Pedido '" + pedido.getId() + "' não encontrado para remoção.");
        }
    }

    public Pedido getPedidoById(String idPedido) {
        return dadosPedidos.listarMap().get(idPedido);
    }

    public Map<String, Pedido> getPedidoMap() {
        return dadosPedidos.listarMap();
    }

    public List<Pedido> listarTodos() {
        return new ArrayList<>(dadosPedidos.listarMap().values());
    }

    public List<Pedido> listarPorIDLoja(String idLoja) {
        List<Pedido> pedidosDaLoja = new ArrayList<>();
        for(Pedido p : dadosPedidos.listarMap().values()) {
            if (p.getIdLoja().equals(idLoja)) {
                pedidosDaLoja.add(p);
            }
        }
        return pedidosDaLoja;
    }


    public List<Pedido> listarPorVendedor(String idVendedor) {
        if (idVendedor == null || idVendedor.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return listarTodos().stream()
                .filter(pedido -> idVendedor.equals(pedido.getIdVendedor()))
                .collect(Collectors.toList());
    }

    public List<Pedido> listarPorVendedor(String idVendedor, boolean incluirConcluidos) {
        List<Pedido> todosOsPedidosDoVendedor = listarPorVendedor(idVendedor);
        if (incluirConcluidos) {
            return todosOsPedidosDoVendedor;
        } else {
            return todosOsPedidosDoVendedor.stream()
                    .filter(p -> p.getStatus() != StatusPedido.CONCLUIDO &&
                            p.getStatus() != StatusPedido.ENTREGUE &&
                            p.getStatus() != StatusPedido.CANCELADO)
                    .collect(Collectors.toList());
        }
    }

    public void atualizarPedido(Pedido pedidoAtualizado) throws PersistenciaException {
        if (dadosPedidos.listarMap().containsKey(pedidoAtualizado.getId())) {
            dadosPedidos.atualizar(pedidoAtualizado);
        } else {
            throw new PersistenciaException("Pedido não encontrado para atualização.");
        }
    }

    public void atualizarStatusPedido(Pedido pedido, StatusPedido novoStatus) throws PersistenciaException {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido não pode ser nulo.");
        }
        pedido.setStatus(novoStatus);
        this.atualizarPedido(pedido);
    }
}