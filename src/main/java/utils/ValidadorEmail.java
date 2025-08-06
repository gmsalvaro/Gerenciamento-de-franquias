package utils;

import exception.usuario.EmailInvalidoException;

public class ValidadorEmail implements Validador<String> {
    @Override
    public void validar(String email) throws EmailInvalidoException {
        if (email == null || email.isEmpty()) {
            throw new EmailInvalidoException("ERRO: Email não pode ser vazio!");
        }
        if (!email.contains("@") || email.indexOf("@") != email.lastIndexOf("@")) {
            throw new EmailInvalidoException("ERRO: Email deve conter exatamente um '@'!");
        }
        String[] partes = email.split("@");
        if (partes.length != 2) {
            throw new EmailInvalidoException("ERRO: Formato de email inválido!");
        }
        String parteLocal = partes[0];
        String dominio = partes[1];
        if (parteLocal.isEmpty()) {
            throw new EmailInvalidoException("ERRO: Parte local do email não pode ser vazia!");
        }
        if (!dominio.contains(".")) {
            throw new EmailInvalidoException("ERRO: Domínio deve conter pelo menos um '.'!");
        }
        if (dominio.startsWith(".") || dominio.endsWith(".")) {
            throw new EmailInvalidoException("ERRO: Domínio não pode começar ou terminar com '.'!");
        }
        String regex = "^[A-Za-z0-9+_.]+@[A-Za-z0-9.]+$";
        if (!email.matches(regex)) {
            throw new EmailInvalidoException("ERRO: Email contém caracteres inválidos!");
        }

    }
}