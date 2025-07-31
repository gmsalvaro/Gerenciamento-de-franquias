package Service;

import exception.autenticacao.SenhaInvalidaException;

public class ValidadorSenha implements Validador<String>{
    private static final String REGEX_SENHA = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(\\S){8,16}$";

    @Override
    public boolean validar(String senha) throws SenhaInvalidaException {
        if (senha == null || senha.isEmpty()) {
            return false;
        }

        if(!senha.matches(REGEX_SENHA))
            throw new SenhaInvalidaException("Senha inválida!");

        return true;
    }

}
