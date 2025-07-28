package exception.usuario;

import exception.autenticacao.UsuarioInvalidoException;

public class CampoInvalidoException extends UsuarioInvalidoException {
    public CampoInvalidoException(String message) {
        super(message);
    }
}
