//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B
package exception.autenticacao;

public class SenhaInvalidaException extends AutenticacaoException {
    public SenhaInvalidaException(String message) {
        super(message);
    }
}
