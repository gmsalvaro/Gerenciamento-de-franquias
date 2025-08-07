package model;

import java.math.BigDecimal;

public class PerformanceCliente {
    private final Cliente cliente;
    private final int numeroDeCompras;
    private final BigDecimal valorTotalCompras;

    public PerformanceCliente(Cliente cliente, int numeroDeCompras, BigDecimal valorTotalCompras) {
        this.cliente = cliente;
        this.numeroDeCompras = numeroDeCompras;
        this.valorTotalCompras = valorTotalCompras;
    }

    // Getters
    public Cliente getCliente() { return cliente; }
    public int getNumeroDeCompras() { return numeroDeCompras; }
    public BigDecimal getValorTotalCompras() { return valorTotalCompras; }
}