package Service;

import Model.*;
import Model.PerformanceVendedor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ServiceRelatorio {
    private final ServiceLoja serviceLoja;
    private final ServicePedido servicePedido;
    private final ServiceUsuario serviceUsuario;
    private final ServiceFranquia serviceFranquia;

    // Modifique o construtor para receber todos os serviços necessários
    public ServiceRelatorio(ServiceLoja sl, ServicePedido sp, ServiceUsuario su, ServiceFranquia sf) {
        this.serviceLoja = sl;
        this.servicePedido = sp;
        this.serviceUsuario = su;
        this.serviceFranquia = sf;
    }

    // Metodo principal para gerar o ranking
    public List<PerformanceVendedor> gerarRankingVendedores() {
        List<PerformanceVendedor> performance = new ArrayList<>();
        List<Vendedor> todosVendedores = serviceUsuario.listarVendedores();

        for (Vendedor vendedor : todosVendedores) {
            List<Pedido> pedidosDoVendedor = servicePedido.listarPorIdVendedor(vendedor.getId());
            int numeroDeVendas = pedidosDoVendedor.size();

            // Calcula o valor total usando BigDecimal
            BigDecimal valorTotal = pedidosDoVendedor.stream()
                    .map(Pedido::getPrecoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Loja lojaDoVendedor = serviceLoja.buscarLojaPorUsuario(vendedor).orElse(null);
            Franquia franquia = (lojaDoVendedor != null) ?
                    serviceFranquia.buscarPorId(lojaDoVendedor.getFranquiaId()) : null;

            performance.add(new PerformanceVendedor(
                    vendedor,
                    lojaDoVendedor,
                    franquia,
                    numeroDeVendas,
                    valorTotal
            ));
        }
        return performance;
    }

    public List<PerformanceVendedor> gerarRankingVendedoresPorLoja(Loja loja) {
        if (loja == null) {
            return new ArrayList<>();
        }

        List<PerformanceVendedor> performance = new ArrayList<>();
        List<Vendedor> vendedoresDaLoja = serviceUsuario.getVendedoresPorLoja(loja);

        for (Vendedor vendedor : vendedoresDaLoja) {
            List<Pedido> pedidosDoVendedor = servicePedido.listarPorIdVendedor(vendedor.getId());
            int numeroDeVendas = pedidosDoVendedor.size();

            BigDecimal valorTotal = pedidosDoVendedor.stream()
                    .map(Pedido::getPrecoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Franquia franquia = serviceFranquia.buscarPorId(loja.getFranquiaId());

            performance.add(new PerformanceVendedor(
                    vendedor,
                    loja,  // Já temos a loja, não precisamos buscar
                    franquia,
                    numeroDeVendas,
                    valorTotal
            ));
        }

        return performance;
    }


}
