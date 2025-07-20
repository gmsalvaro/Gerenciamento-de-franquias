package Service;

import Model.Produto;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class GerenciamentoProdutos {
    private final String PRODUTOS_FILE = "produtos.json";
    private List<Produto> produtos;
    Dados dados =  new Dados();

    public GerenciamentoProdutos() {
        produtos = dados.carregarDados(PRODUTOS_FILE, new TypeReference<List<Produto>>() {}); // Duvida se iremos utilizar produto - herança - pedido

    }
}
