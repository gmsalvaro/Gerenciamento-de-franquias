package exception.persistencia;

public class ArquivoNaoCriadoException extends PersistenciaException {
    public ArquivoNaoCriadoException(String message) {
        super(message);
    }
}
