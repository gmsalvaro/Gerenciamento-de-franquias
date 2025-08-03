package Service;

import Model.*;
import Model.PerformanceVendedor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public BigDecimal calcularFaturamentoLoja(Loja loja) {

        if (loja != null)
            return BigDecimal.ZERO;

        List<Pedido> pedidosDaLoja = servicePedido.listarPorIDLoja(loja.getId());

        return pedidosDaLoja.stream()
                .filter(p -> p.getStatus() == StatusPedido.ENTREGUE || p.getStatus() == StatusPedido.CONCLUIDO)
                .map(Pedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    public List<Pedido> getHistoricoVendas(Loja loja) {
        return servicePedido.listarPorIDLoja(loja.getId()).stream()
                .filter(p -> p.getStatus() ==  StatusPedido.ENTREGUE)
                .sorted(Comparator.comparing(Pedido::getDataPedido).reversed()) // Mais recentes primeiro
                .collect(Collectors.toList());
    }

    public BigDecimal calcularFaturamentoFranquia(Franquia franquia) {
        if (franquia == null) {
            return BigDecimal.ZERO;
        }

        // Busca todas as lojas que pertencem a esta franquia
        List<Loja> lojasDaFranquia = serviceLoja.listarPorIDFranquia(franquia.getId());

        // Para cada loja da franquia, calcula seu faturamento e soma tudo
        return lojasDaFranquia.stream()
                .map(this::calcularFaturamentoLoja) // Reutiliza o método que calcula o faturamento por loja
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int contarPedidosTotaisFranquia(Franquia franquia) {
        if (franquia == null) {
            return 0;
        }

        // Busca todas as lojas que pertencem a esta franquia
        List<Loja> lojasDaFranquia = serviceLoja.listarPorIDFranquia(franquia.getId());

        // Usa um Stream para somar o tamanho da lista de pedidos de cada loja
        return lojasDaFranquia.stream()
                .mapToInt(loja -> loja.getIdPedidos() != null ? loja.getIdPedidos().size() : 0)
                .sum();
    }

}
