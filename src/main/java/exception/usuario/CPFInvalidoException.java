package exception.usuario;

public class CPFInvalidoException extends ValidacaoUsuarioException {
    public CPFInvalidoException(String message) {
        super(message);
    }
}
