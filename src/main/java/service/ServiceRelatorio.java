package service;

import model.*;
import model.PerformanceVendedor;

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

    public ServiceRelatorio(ServiceLoja sl, ServicePedido sp, ServiceUsuario su, ServiceFranquia sf) {
        this.serviceLoja = sl;
        this.servicePedido = sp;
        this.serviceUsuario = su;
        this.serviceFranquia = sf;
    }

    public BigDecimal calcularFaturamentoLoja(Loja loja) {
        if (loja == null)
            return BigDecimal.ZERO;
        List<Pedido> pedidosDaLoja = servicePedido.listarPorIDLoja(loja.getId());
        return pedidosDaLoja.stream()
                .filter(p -> p.getStatus() == StatusPedido.ENTREGUE || p.getStatus() == StatusPedido.CONCLUIDO)
                .map(Pedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<PerformanceVendedor> gerarRankingVendedores() {
        List<PerformanceVendedor> performance = new ArrayList<>();
        List<Vendedor> todosVendedores = serviceUsuario.listarVendedores();
        for (Vendedor vendedor : todosVendedores) {
            List<Pedido> pedidosDoVendedor = servicePedido.listarPorVendedor(vendedor.getId());
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
            List<Pedido> pedidosDoVendedor = servicePedido.listarPorVendedor(vendedor.getId());
            int numeroDeVendas = pedidosDoVendedor.size();
            BigDecimal valorTotal = pedidosDoVendedor.stream()
                    .map(Pedido::getPrecoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Franquia franquia = serviceFranquia.buscarPorId(loja.getFranquiaId());
            performance.add(new PerformanceVendedor(
                    vendedor,
                    loja,
                    franquia,
                    numeroDeVendas,
                    valorTotal
            ));
        }
        return performance;
    }

    public List<Pedido> getHistoricoVendas(Loja loja) {
        return servicePedido.listarPorIDLoja(loja.getId()).stream()
                // Filtra para incluir pedidos com status ENTREGUE ou CONCLUIDO
                .filter(p -> p.getStatus() ==  StatusPedido.ENTREGUE || p.getStatus() == StatusPedido.CONCLUIDO)
                .sorted(Comparator.comparing(Pedido::getDataPedido).reversed()) // Mais recentes primeiro
                .collect(Collectors.toList());
    }

    public BigDecimal calcularFaturamentoFranquia(Franquia franquia) {
        // 1. Garante que a franquia não é nula para evitar erros
        if (franquia == null) {
            return BigDecimal.ZERO;
        }

        // 2. Obtém a lista de todas as lojas da franquia
        List<Loja> lojasDaFranquia = serviceLoja.listarPorFranquia(franquia.getId());

        // 3. Itera sobre as lojas, coleta os pedidos, filtra e soma os valores
        BigDecimal faturamentoTotal = lojasDaFranquia.stream()
                // Obtém todos os pedidos de cada loja da franquia
                .flatMap(loja -> servicePedido.listarPorIDLoja(loja.getId()).stream())
                // Filtra apenas os pedidos com status ENTREGUE ou CONCLUIDO
                .filter(pedido -> pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CONCLUIDO)
                // Mapeia cada pedido para seu valor total
                .map(Pedido::getPrecoTotal)
                // Soma todos os valores, começando de ZERO
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return faturamentoTotal;
    }

    public int contarPedidosTotaisFranquia(Franquia franquia) {
        if (franquia == null) {
            return 0;
        }
        List<Loja> lojasDaFranquia = serviceLoja.listarPorFranquia(franquia.getId());
        return lojasDaFranquia.stream()
                .mapToInt(loja -> loja.getIdPedidos() != null ? loja.getIdPedidos().size() : 0)
                .sum();
    }

}
