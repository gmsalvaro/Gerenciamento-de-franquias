package Service;

import Model.Usuario;
import exception.autenticacao.SenhaInvalidaException;
import exception.usuario.ValidacaoUsuarioException;

public interface Validador<T> {

    public boolean validar(T generico) throws ValidacaoUsuarioException, SenhaInvalidaException;
}
