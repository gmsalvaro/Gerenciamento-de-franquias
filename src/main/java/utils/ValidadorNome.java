package utils;

import exception.usuario.NomeInvalidoException;

public class ValidadorNome implements Validador<String> {

    @Override
    public void validar(String nome) throws NomeInvalidoException {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("ERRO: Nome não pode ser vazio!");
        }

        if (nome.length() < 2 || nome.length() > 50) {
            throw new IllegalArgumentException("ERRO: Nome deve ter entre 3 e 50 caracteres!");
        }

        if (!nome.matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("ERRO: Nome deve conter apenas letras e espaços!");
        }
    }
}
