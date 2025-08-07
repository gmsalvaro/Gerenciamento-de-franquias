//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B
package exception.usuario;

import exception.autenticacao.UsuarioInvalidoException;

public class CampoInvalidoException extends UsuarioInvalidoException {
    public CampoInvalidoException(String message) {
        super(message);
    }
}
