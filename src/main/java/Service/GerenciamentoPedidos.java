package Service;

import Model.Pedido;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class GerenciamentoPedidos {
    private final String PEDIDOS_FILE = "pedidos.json";
    private List<Pedido> pedidos;
    Dados dados =  new Dados();

    public GerenciamentoPedidos() {
        pedidos = dados.carregarDados(PEDIDOS_FILE, new TypeReference<List<Pedido>>() {});

    }
}
