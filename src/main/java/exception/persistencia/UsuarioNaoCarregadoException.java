//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B
package exception.persistencia;

public class UsuarioNaoCarregadoException extends PersistenciaException {
    public UsuarioNaoCarregadoException(String message) {
        super(message);
    }
}
