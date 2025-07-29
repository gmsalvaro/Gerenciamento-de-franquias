package exception.usuario;

public class EmailInvalidoException extends ValidacaoUsuarioException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
