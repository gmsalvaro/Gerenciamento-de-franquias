package Service;

import Dados.DadosProdutos;
import Dados.DadosProdutos;
import Model.Produto;
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceProduto {
    private final String FILE_PRODUTOS;
    private DadosProdutos dadosProdutos;
    private Map<String, Produto> produtosMap;

    public ServiceProduto(String FILE_PRODUTOS) throws PersistenciaException {
        this.FILE_PRODUTOS = FILE_PRODUTOS;
        this.dadosProdutos = new DadosProdutos(FILE_PRODUTOS);
        this.produtosMap = dadosProdutos.getProdutosMap();
    }

    public void addProduto(Produto produto) throws PersistenciaException {
        dadosProdutos.adicionar(produto);
        this.produtosMap = dadosProdutos.getProdutosMap();
    }

    public void removerProduto(Produto produto) throws PersistenciaException {
        if (produtosMap.containsKey(produto.getId())) {
            dadosProdutos.remover(produto.getId());
            this.produtosMap = dadosProdutos.getProdutosMap();
        } else {
            throw new PersistenciaException("Produto '" + produto.getNome() + "' não encontrado para remoção.");
        }
    }

    public Produto getProdutoById(String idProduto) {
        return produtosMap.get(idProduto);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtosMap.values());
    }

    public List<Produto> listarPorIDLoja(String idLoja) {
        List<Produto> produtosDaLoja = new ArrayList<>();
        for(Produto p : produtosMap.values()) {
                if(p.getIdLoja().equals(idLoja))
                    produtosDaLoja.add(p);
            }
        return produtosDaLoja;
    }

    public void atualizarProduto(Produto produtoAtualizado) throws PersistenciaException {
        if (produtosMap.containsKey(produtoAtualizado.getId())) {
            dadosProdutos.atualizar(produtoAtualizado);
            this.produtosMap = dadosProdutos.getProdutosMap();
        } else {
            throw new PersistenciaException("Produto não encontrado para atualização.");
        }
    }
}