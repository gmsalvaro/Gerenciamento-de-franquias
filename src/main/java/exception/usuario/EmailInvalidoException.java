//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B
package exception.usuario;

public class EmailInvalidoException extends ValidacaoUsuarioException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
