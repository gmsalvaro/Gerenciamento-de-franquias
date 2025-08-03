package Service;

import Dados.DadosPedidos;
import Model.Pedido;
import Model.StatusPedido;
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.HashMap;
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

    public List<Pedido> listarPorIDLoja(String idLoja) {
        List<Pedido> pedidosDaLoja = new ArrayList<>();
        for(Pedido p : pedidoMap.values()) {
            if (p.getIdLoja().equals(idLoja)) {
                pedidosDaLoja.add(p);
            }
        }
        return pedidosDaLoja;
    }

    public List<Pedido> listarPorIDLoja(String idLoja, boolean incluirConcluidos) {
        List<Pedido> todosOsPedidos = listarPorIDLoja(idLoja);

        if (incluirConcluidos) {
            return todosOsPedidos; // Retorna tudo
        } else {
            // Retorna apenas os pedidos que NÃO estão concluídos ou cancelados
            return todosOsPedidos.stream()
                    .filter(p -> p.getStatus() != StatusPedido.CONCLUIDO &&
                            p.getStatus() != StatusPedido.ENTREGUE &&
                            p.getStatus() != StatusPedido.CANCELADO)
                    .collect(Collectors.toList());
        }
    }

    public List<Pedido> listarPorIdVendedor(String idVendedor) {
        if (idVendedor == null || idVendedor.trim().isEmpty()) {
            return new ArrayList<>(); // Retorna lista vazia se o ID for inválido
        }

        // Usando Stream para filtrar a lista de forma mais eficiente e legível
        return listarTodos().stream() // Pega todos os pedidos
                .filter(pedido -> idVendedor.equals(pedido.getIdVendedor())) // Mantém apenas os pedidos cujo idVendedor corresponde
                .collect(Collectors.toList()); // Coleta os resultados em uma nova lista
    }

    public List<Pedido> listarPorIdVendedor(String idVendedor, boolean incluirConcluidos) {
        // 1. Reutiliza o método acima para pegar todos os pedidos do vendedor primeiro
        List<Pedido> todosOsPedidosDoVendedor = listarPorIdVendedor(idVendedor);

        if (incluirConcluidos) {
            return todosOsPedidosDoVendedor; // Retorna a lista completa
        } else {
            // 2. Filtra a lista para retornar apenas os pedidos em andamento
            return todosOsPedidosDoVendedor.stream()
                    .filter(p -> p.getStatus() != StatusPedido.CONCLUIDO &&
                            p.getStatus() != StatusPedido.ENTREGUE &&
                            p.getStatus() != StatusPedido.CANCELADO)
                    .collect(Collectors.toList());
        }
    }


    public void atualizarPedido(Pedido pedidoAtualizado) throws PersistenciaException {
        if (pedidoMap.containsKey(pedidoAtualizado.getId())) {
            dadosPedidos.atualizar(pedidoAtualizado);
            this.pedidoMap = dadosPedidos.getPedidosMap();
        } else {
            throw new PersistenciaException("Pedido não encontrado para atualização.");
        }
    }

    public void atualizarStatusPedido(Pedido pedido, StatusPedido novoStatus) throws PersistenciaException {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido não pode ser nulo.");
        }

        // Altera o status do objeto em memória
        pedido.setStatus(novoStatus);

        // Chama o método 'atualizar' que já existe para salvar a mudança no arquivo JSON
        this.atualizarPedido(pedido);
    }
}