package service;

import exception.usuario.CPFInvalidoException;


public class ValidadorCPF implements Validador<String> {

    @Override
    public void validar(String cpf) throws CPFInvalidoException {
        if (cpf == null || cpf.isEmpty()) {
            throw new CPFInvalidoException("ERRO: CPF não pode ser vazio!");
        }

        for (Character ch : cpf.toCharArray()) {
            if (!Character.isDigit(ch)) {
                throw new CPFInvalidoException("ERRO: CPF deve conter apenas dígitos!");
            }
        }

        if(cpf.length() != 11)
            throw new CPFInvalidoException("ERRO: CPF fora dos padrões!");
    }

}
