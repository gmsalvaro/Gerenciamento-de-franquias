package Service;

import Dados.DadosProdutos;
import Dados.DadosProdutos;
import Model.Loja;
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

    public void addProduto(Produto produto, Loja loja, ServiceLoja serviceLoja) throws PersistenciaException {
        // 1. Validação: Verifica se já existe um produto com o mesmo nome nesta loja
        List<Produto> produtosDaLoja = listarPorIDLoja(loja.getId());
        for (Produto p : produtosDaLoja) {
            if (p.getNome().equalsIgnoreCase(produto.getNome())) {
                throw new PersistenciaException("Já existe um produto com o nome '" + produto.getNome() + "' nesta loja.");
            }
        }

        // 2. Associa o produto à loja
        produto.setIdLoja(loja.getId());

        // 3. Adiciona o produto à base de dados de produtos
        dadosProdutos.adicionar(produto);

        // 4. Atualiza a loja para incluir o ID do novo produto
        loja.adicionarIdProduto(produto.getId());
        serviceLoja.atualizarLoja(loja);
    }

    public void removerProduto(Produto produtoParaRemover, Loja loja, ServiceLoja serviceLoja) throws PersistenciaException {
        // 1. Validação básica
        if (produtoParaRemover == null || loja == null) {
            throw new IllegalArgumentException("Produto e Loja não podem ser nulos.");
        }

        // 2. Remove o ID do produto da lista da loja
        loja.removerIdProduto(produtoParaRemover.getId());

        // 3. Salva o estado atualizado da loja
        serviceLoja.atualizarLoja(loja);

        // 4. Remove o produto da base de dados de produtos
        dadosProdutos.remover(produtoParaRemover.getId());
    }

    public void removerProdutoID(String id) throws PersistenciaException {
        if (id == null || !dadosProdutos.getProdutosMap().containsKey(id)) {
            throw new PersistenciaException("Produto não encontrado para remoção.");
        }

        dadosProdutos.remover(id);
        this.produtosMap = dadosProdutos.getProdutosMap();
    }



    public Map<String, Produto> getProdutosMap() {
        return produtosMap;
    }

    public Produto getProdutoById(String idProduto) throws PersistenciaException
    {
        return produtosMap.get(idProduto);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtosMap.values());
    }

    public List<Produto> listarPorIDLoja(String idLoja) throws PersistenciaException {
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