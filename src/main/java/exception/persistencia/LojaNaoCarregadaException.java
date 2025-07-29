package exception.persistencia;

public class LojaNaoCarregadaException extends PersistenciaException {
    public LojaNaoCarregadaException(String message) {
        super(message);
    }
}
