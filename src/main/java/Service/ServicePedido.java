package Service;
import Dados.DadosPedidos;
import Model.Loja;
import Model.Produtos;
import exception.ValidacaoException;
import exception.persistencia.LojaInvalidaException;
import exception.persistencia.LojaNaoAtualizadaException;
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServicePedido {
    String FILE_PEDIDOS;
    DadosPedidos dadosPedidos;
    private Map<String, Produtos> pedidoMap;

    public ServicePedido(String FILE_PEDIDOS) throws PersistenciaException {
        this.FILE_PEDIDOS = FILE_PEDIDOS;
        this.dadosPedidos = new DadosPedidos(FILE_PEDIDOS);
        this.pedidoMap = dadosPedidos.getLojasMap();
    }

    public void addPedidos(Produtos pedido, Loja loja) throws PersistenciaException {
        for (Produtos p : pedidoMap.values()) { // verificar essa validação !
//            if () { pensar na verificação de pedido
//                throw new ValidacaoException("Loja com nome ou endereço já existente.");
//            }
        }

        if(loja == null) {
            throw new LojaInvalidaException("Franquia invalida");
        }

        loja.adicionarIdPedido(pedido.getId());
        pedido.setIdLoja(loja.getFranquiaId());
        dadosPedidos.adicionar(pedido);
        pedidoMap = dadosPedidos.getLojasMap();
    }

    public void removerLoja(String id,  Loja loja) throws PersistenciaException {
        if (pedidoMap.containsKey(id)) {
            loja.removerIdPedido(id);
            dadosPedidos.remover(id);
            pedidoMap = dadosPedidos.getLojasMap();
        } else {
            throw new LojaInvalidaException("Loja não encontrada para remoção.");
        }
    }

    public ArrayList<Produtos> listarLojas() {
        return new ArrayList<>(pedidoMap.values());
    }

    public List<Produtos> listarPorIDLoja(String id) {
        ArrayList<Produtos> pedidos = new ArrayList<>();
        for(Produtos p : pedidoMap.values()) {
            if (p.getIdLoja().equalsIgnoreCase(id)) {
                pedidos.add(p);
            }
        }
        return pedidos;
    }

    public Produtos buscarPorId(String id) {
        return pedidoMap.get(id);
    }

    public void atualizarLoja(Produtos pedidoAtualizado) throws PersistenciaException {
        if (pedidoMap.containsKey(pedidoAtualizado.getId())) {
            dadosPedidos.atualizar(pedidoAtualizado);
            pedidoMap = dadosPedidos.getLojasMap();
        } else {
            throw new LojaNaoAtualizadaException("Loja não encontrada para atualização.");
        }
    }

}
