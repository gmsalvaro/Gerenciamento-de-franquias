//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package service;

import exception.autenticacao.SenhaInvalidaException;
import exception.usuario.ValidacaoUsuarioException;

public interface Validador<T> {

    public void validar(T generico) throws ValidacaoUsuarioException, SenhaInvalidaException;
}
