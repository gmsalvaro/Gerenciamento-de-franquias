package Service;

import exception.produto.ProdutoException;
import repository.DadosProdutos;
import model.Loja;
import model.Produto;
import exception.persistencia.PersistenciaException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceProduto {
    private final String FILE_PRODUTOS;
    private DadosProdutos dadosProdutos;

    public ServiceProduto(String FILE_PRODUTOS) throws PersistenciaException {
        this.FILE_PRODUTOS = FILE_PRODUTOS;
        this.dadosProdutos = new DadosProdutos(FILE_PRODUTOS);
    }

    public void adicionar(Produto produto, Loja loja, ServiceLoja serviceLoja) throws ProdutoException, PersistenciaException {
        List<Produto> produtosDaLoja = listarPorLoja(loja.getId());
        for (Produto p : produtosDaLoja) {
            if (p.getNome().equalsIgnoreCase(produto.getNome())) {
                throw new ProdutoException("Já existe um produto com o nome '" + produto.getNome() + "' nesta loja.");
            }
        }
        produto.setIdLoja(loja.getId());
        dadosProdutos.adicionar(produto);
        loja.adicionarIdProduto(produto.getId());
        serviceLoja.atualizar(loja);
    }

    public void remover(Produto produtoParaRemover, Loja loja, ServiceLoja serviceLoja) throws PersistenciaException {
        if (produtoParaRemover == null || loja == null) {
            throw new IllegalArgumentException("Produto e Loja não podem ser nulos.");
        }
        loja.removerIdProduto(produtoParaRemover.getId());
        serviceLoja.atualizar(loja);
        dadosProdutos.remover(produtoParaRemover.getId());
    }

    public Map<String, Produto> getProdutosMap() { return  new HashMap<>(dadosProdutos.listarMap()); }

    public Produto getProduto(String idProduto) {
        return dadosProdutos.listarMap().get(idProduto);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(dadosProdutos.listarMap().values());
    }

    public List<Produto> listarPorLoja(String idLoja) {
        List<Produto> produtosDaLoja = new ArrayList<>();
        for(Produto p : dadosProdutos.listarMap().values()) {
                if(p.getIdLoja().equals(idLoja))
                    produtosDaLoja.add(p);
            }
        return produtosDaLoja;
    }

    public void atualizarProduto(Produto produtoAtualizado) throws PersistenciaException {
        if (dadosProdutos.listarMap().containsKey(produtoAtualizado.getId())) {
            dadosProdutos.atualizar(produtoAtualizado);
        } else {
            throw new PersistenciaException("Produto não encontrado para atualização.");
        }
    }

    public List<Produto> listarProdutosComEstoqueBaixo(Loja loja, int limite) {
        if (loja == null) {
            return new ArrayList<>();
        }

        return listarPorLoja(loja.getId()).stream()
                .filter(produto -> produto.getEstoque() > 0 && produto.getEstoque() <= limite)
                .collect(Collectors.toList());
    }
}