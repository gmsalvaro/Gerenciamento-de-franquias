//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B
package exception.persistencia;

public class ArquivoNaoCriadoException extends PersistenciaException {
    public ArquivoNaoCriadoException(String message) {
        super(message);
    }
}
