package Service;

import Dados.DadosPedidos;
import Model.Loja;
import Model.Produto;
import Model.Produtos;
import exception.ValidacaoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceProduto {
    String FILE_PRODUTOS;
    DadosPedidos dadosProdutos;
    private Map<String, Produto> produtosMap;

    public ServiceProduto(String FILE_PEDIDOS) {
        this.FILE_PRODUTOS = FILE_PEDIDOS;
        this.dadosProdutos = new DadosPedidos(FILE_PEDIDOS);
        this.produtosMap = dadosProdutos.getLojasMap();
    }

    public void addPedidos(Produto produto, Loja loja) throws ValidacaoException {
        for (Produto p : produtosMap.values()) { // verificar essa validação !
//            if () { pensar na verificação de pedido
//                throw new ValidacaoException("Loja com nome ou endereço já existente.");
//            }
        }

        if(loja == null) {
            throw new ValidacaoException("Franquia invalida");
        }

        loja.adicionarIdProduto(produto.getId());
        produto.setIdLoja(loja.getFranquiaId());
        dadosProdutos.adicionar(produto);
        produtosMap = dadosProdutos.getLojasMap();
    }

    public void removerLoja(String id,  Loja loja) throws ValidacaoException {
        if (produtosMap.containsKey(id)) {
            loja.removerIdPedido(id);
            dadosProdutos.remover(id);
            produtosMap = dadosProdutos.getLojasMap();
        } else {
            throw new ValidacaoException("Loja não encontrada para remoção.");
        }
    }

    public ArrayList<E> listarLojas() {
        return new ArrayList<>(produtosMap.values());
    }

    public List<Produtos> listarPorIDLoja(String id) {
        ArrayList<Produtos> pedidos = new ArrayList<>();
        for(Produtos p : produtosMap.values()) {
            if (p.getIdLoja().equalsIgnoreCase(id)) {
                pedidos.add(p);
            }
        }
        return pedidos;
    }

    public Produtos buscarPorId(String id) {
        return produtosMap.get(id);
    }

    public void atualizarLoja(Produtos pedidoAtualizado) throws ValidacaoException {
        if (produtosMap.containsKey(pedidoAtualizado.getId())) {
            dadosProdutos.atualizar(pedidoAtualizado);
            produtosMap = dadosProdutos.getLojasMap();
        } else {
            throw new ValidacaoException("Loja não encontrada para atualização.");
        }
    }


}
