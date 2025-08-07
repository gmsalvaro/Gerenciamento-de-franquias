//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

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

    // getters
    public Cliente getCliente() { return cliente; }
    public int getNumeroDeCompras() { return numeroDeCompras; }
    public BigDecimal getValorTotalCompras() { return valorTotalCompras; }
}