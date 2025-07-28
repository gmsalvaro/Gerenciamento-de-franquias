package exception.pedido;

public class LimiteDeItensException extends PedidoException {
    public LimiteDeItensException(String message) {
        super(message);
    }
}
