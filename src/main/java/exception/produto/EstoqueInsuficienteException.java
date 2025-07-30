package exception.produto;

public class EstoqueInsuficienteException extends ProdutoException {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}
