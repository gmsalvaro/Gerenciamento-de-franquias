package Model;

import java.math.BigDecimal;

public class PerformanceVendedor {
        private final Vendedor vendedor;
        private final Loja loja;
        private final Franquia franquia;
        private final int numeroDeVendas;
        private final BigDecimal valorTotalVendas;
        public PerformanceVendedor(Vendedor vendedor, Loja loja, Franquia franquia, int numeroDeVendas, BigDecimal valorTotalVendas) {
            this.vendedor = vendedor;
            this.loja = loja;
            this.franquia = franquia;
            this.numeroDeVendas = numeroDeVendas;
            this.valorTotalVendas = valorTotalVendas;
        }

        public Vendedor getVendedor() { return vendedor; }
        public Loja getLoja() { return loja; }
        public Franquia getFranquia() { return franquia; }
        public int getNumeroDeVendas() { return numeroDeVendas; }
        public BigDecimal getValorTotalVendas() { return valorTotalVendas; }
    }


