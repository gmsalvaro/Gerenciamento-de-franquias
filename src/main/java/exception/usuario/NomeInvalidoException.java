package exception.usuario;

public class NomeInvalidoException  extends ValidacaoUsuarioException {
    public NomeInvalidoException(String message) {
        super(message);
    }
}
