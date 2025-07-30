package exception.autenticacao;

public class SenhaInvalidaException extends AutenticacaoException {
    public SenhaInvalidaException(String message) {
        super(message);
    }
}
