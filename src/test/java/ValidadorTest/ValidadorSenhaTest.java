package ValidadorTest;

import Service.ValidadorSenha;
import exception.autenticacao.SenhaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para a classe ValidadorSenha")
class ValidadorSenhaTest {

    private final ValidadorSenha validadorSenha = new ValidadorSenha();

    @Test
    @DisplayName("Teste de validação com senha válida")
    void testValidarSenhaValida() {
        String senhaValida = "Senha123";
        Assertions.assertDoesNotThrow(() -> validadorSenha.validar(senhaValida));
    }

    @Test
    @DisplayName("Teste de validação com senha nula")
    void testValidarSenhaNula() {
        Assertions.assertDoesNotThrow(() -> validadorSenha.validar(null));
    }

    @Test
    @DisplayName("Teste de validação com senha vazia")
    void testValidarSenhaVazia() {
        Assertions.assertDoesNotThrow(() -> validadorSenha.validar(""));
    }

    @Test
    @DisplayName("Teste de validação com senha muito curta (menos de 8 caracteres)")
    void testValidarSenhaMuitoCurta() {
        String senhaCurta = "Senha12";
        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaCurta));
        Assertions.assertEquals("Senha inválida!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com senha muito longa (mais de 16 caracteres)")
    void testValidarSenhaMuitoLonga() {
        String senhaLonga = "Senha123456789101";
        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaLonga));
        Assertions.assertEquals("Senha inválida!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com senha sem letra maiúscula")
    void testValidarSenhaSemLetraMaiuscula() {
        String senhaSemMaiuscula = "senha123";
        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaSemMaiuscula));
        Assertions.assertEquals("Senha inválida!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com senha sem letra minúscula")
    void testValidarSenhaSemLetraMinuscula() {
        String senhaSemMinuscula = "SENHA123";
        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaSemMinuscula));
        Assertions.assertEquals("Senha inválida!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com senha sem dígito")
    void testValidarSenhaSemDigito() {
        String senhaSemDigito = "SenhaTeste";
        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaSemDigito));
        Assertions.assertEquals("Senha inválida!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com senha com espaço")
    void testValidarSenhaComEspaco() {
        String senhaComEspaco = "Senha 123";
        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaComEspaco));
        Assertions.assertEquals("Senha inválida!", exception.getMessage());
    }

//    @Test
//    @DisplayName("Teste de validação com senha com caractere especial (que não seja um espaço)")
//    void testValidarSenhaComCaractereEspecial() {
//        // O REGEX_SENHA atual não permite caracteres especiais, pois usa \S (não-whitespace) e não permite outros símbolos.
//        String senhaComEspecial = "Senha#123";
//        SenhaInvalidaException exception = Assertions.assertThrows(SenhaInvalidaException.class, () -> validadorSenha.validar(senhaComEspecial));
//        Assertions.assertEquals("Senha inválida!", exception.getMessage());
//    }
}

