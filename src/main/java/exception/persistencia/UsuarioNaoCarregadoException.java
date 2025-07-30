package exception.persistencia;

public class UsuarioNaoCarregadoException extends PersistenciaException {
    public UsuarioNaoCarregadoException(String message) {
        super(message);
    }
}
