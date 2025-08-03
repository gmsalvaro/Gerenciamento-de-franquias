package test.Service;

import Service.ValidadorSenha;
import exception.autenticacao.SenhaInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorSenhaTest {

    private final ValidadorSenha validador = new ValidadorSenha();

    @Test
    void testValidar_senhaValida_deveRetornarTrue() {
        // Senha com 8 a 16 caracteres, com pelo menos uma letra maiúscula, uma minúscula e um número.
        String senhaValida = "Senha123";
        assertDoesNotThrow(() -> {
            assertTrue(validador.validar(senhaValida));
        });
    }

    @Test
    void testValidar_senhaCurta_deveLancarExcecao() {
        String senhaCurta = "aB12345"; // Menos de 8 caracteres
        assertThrows(SenhaInvalidaException.class, () -> validador.validar(senhaCurta));
    }

    @Test
    void testValidar_senhaLonga_deveLancarExcecao() {
        String senhaLonga = "Ab1234567890123456"; // Mais de 16 caracteres
        assertThrows(SenhaInvalidaException.class, () -> validador.validar(senhaLonga));
    }

    @Test
    void testValidar_senhaSemDigito_deveLancarExcecao() {
        String senhaSemDigito = "Senha!@#";
        assertThrows(SenhaInvalidaException.class, () -> validador.validar(senhaSemDigito));
    }

    @Test
    void testValidar_senhaSemLetraMaiuscula_deveLancarExcecao() {
        String senhaSemMaiuscula = "senha123";
        assertThrows(SenhaInvalidaException.class, () -> validador.validar(senhaSemMaiuscula));
    }

    @Test
    void testValidar_senhaSemLetraMinuscula_deveLancarExcecao() {
        String senhaSemMinuscula = "SENHA123";
        assertThrows(SenhaInvalidaException.class, () -> validador.validar(senhaSemMinuscula));
    }

    @Test
    void testValidar_senhaNula_deveRetornarFalse() throws SenhaInvalidaException {
        assertFalse(validador.validar(null));
    }

    @Test
    void testValidar_senhaVazia_deveRetornarFalse() throws SenhaInvalidaException {
        assertFalse(validador.validar(""));
    }
}