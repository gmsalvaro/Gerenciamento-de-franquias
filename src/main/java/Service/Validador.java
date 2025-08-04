package Service;

import exception.autenticacao.SenhaInvalidaException;
import exception.usuario.ValidacaoUsuarioException;

public interface Validador<T> {

    public void validar(T generico) throws ValidacaoUsuarioException, SenhaInvalidaException;
}
