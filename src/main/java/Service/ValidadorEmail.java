package Service;

import exception.usuario.EmailInvalidoException;

public class ValidadorEmail implements Validador<String> {
    @Override
    public boolean validar(String email) throws EmailInvalidoException {
        if (email == null || email.isEmpty()) {
            throw new EmailInvalidoException("ERRO: Email não pode ser vazio!");
        }

        // Verifica se contém apenas um @
        if (!email.contains("@") || email.indexOf("@") != email.lastIndexOf("@")) {
            throw new EmailInvalidoException("ERRO: Email deve conter exatamente um '@'!");
        }

        // Divide o email em parte local e domínio
        String[] partes = email.split("@");
        if (partes.length != 2) {
            throw new EmailInvalidoException("ERRO: Formato de email inválido!");
        }

        String parteLocal = partes[0];
        String dominio = partes[1];

        // Verifica se a parte local não está vazia
        if (parteLocal.isEmpty()) {
            throw new EmailInvalidoException("ERRO: Parte local do email não pode ser vazia!");
        }

        // Verifica se o domínio tem pelo menos um ponto
        if (!dominio.contains(".")) {
            throw new EmailInvalidoException("ERRO: Domínio deve conter pelo menos um '.'!");
        }

        // Verifica se o domínio não começa ou termina com ponto
        if (dominio.startsWith(".") || dominio.endsWith(".")) {
            throw new EmailInvalidoException("ERRO: Domínio não pode começar ou terminar com '.'!");
        }

        // Verifica caracteres permitidos usando expressão regular
        String regex = "^[A-Za-z0-9+_.]+@[A-Za-z0-9.]+$";
        if (!email.matches(regex)) {
            throw new EmailInvalidoException("ERRO: Email contém caracteres inválidos!");
        }

        return true;
    }
}