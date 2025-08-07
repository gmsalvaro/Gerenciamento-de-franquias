package service;

import exception.autenticacao.SenhaInvalidaException;

public class ValidadorSenha implements Validador<String> {
    private static final String REGEX_SENHA = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(\\S){8,16}$";

    @Override
    public void validar(String senha) throws SenhaInvalidaException {
        if (senha == null || senha.isEmpty()) {
            return;
        }
        if(!senha.matches(REGEX_SENHA))
            throw new SenhaInvalidaException("Senha inválida!");

    }

}
