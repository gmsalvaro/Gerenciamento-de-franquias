package Service;

import Model.Loja;
import Model.Pedido;
import Model.Produto;
import exception.persistencia.PersistenciaException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Model.Vendedor;
import java.util.UUID; // Para gerar IDs únicos

public class serviceEstoque {
    private ServiceManager serviceManager;
    private Loja lojaAssociada;

    public serviceEstoque(Loja loja, ServiceManager serviceManager) throws PersistenciaException {
        this.lojaAssociada = loja;
        this.serviceManager = serviceManager;
    }

    public List<Produto> listarProdutosDisponiveis() throws PersistenciaException {
        return serviceManager.getServiceProduto().listarPorIDLoja(lojaAssociada.getId());
    }

    public List<Pedido> listarPedidosDaLoja() throws PersistenciaException {
        return serviceManager.getServicePedido().listarPorIDLoja(lojaAssociada.getId());
    }


    public Pedido finalizarCompra(Map<String, Integer> itensComprados, Vendedor vendedor)  throws PersistenciaException {
        if (itensComprados == null || itensComprados.isEmpty()) {
            throw new PersistenciaException("Nenhum item selecionado para a compra.");
        }

        List<Produto> produtosAtualizados = new ArrayList<>();
        Map<String, Integer> produtosNoPedido = new HashMap<>();
        BigDecimal totalComprado = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : itensComprados.entrySet()) {
            String idProduto = entry.getKey();
            Integer quantidadeDesejada = entry.getValue();

            Produto produto = serviceManager.getServiceProduto().getProdutoById(idProduto);

            if (produto == null) {
                throw new PersistenciaException("Produto com ID " + idProduto + " não encontrado.");
            }
            if (quantidadeDesejada <= 0) {
                throw new PersistenciaException("Quantidade inválida (" + quantidadeDesejada + ") para o produto " + produto.getNome());
            }

            if (produto.getEstoque() < quantidadeDesejada) {
                throw new PersistenciaException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + produto.getEstoque() +
                                ", Desejado: " + quantidadeDesejada
                );
            }

            produto.setEstoque(produto.getEstoque() - quantidadeDesejada);
            produtosAtualizados.add(produto);
            produtosNoPedido.put(produto.getId(), quantidadeDesejada);
            totalComprado = totalComprado.add(calcularSubtotalItem(produto, quantidadeDesejada));
        }

        Pedido novoPedido = new Pedido(lojaAssociada.getId(), produtosNoPedido, new Date(), "Pendente", vendedor.getId(), totalComprado);
        novoPedido.setIdLoja(lojaAssociada.getId());

        serviceManager.getServicePedido().addPedido(novoPedido);

        for (Produto p : produtosAtualizados) {
            serviceManager.getServiceProduto().atualizarProduto(p);
        }

        lojaAssociada.adicionarIdPedido(novoPedido.getId());
        serviceManager.getServiceLoja().atualizarLoja(lojaAssociada);
        return novoPedido;
    }

    public BigDecimal calcularSubtotalItem(Produto produto, Integer quantidade) {
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

}