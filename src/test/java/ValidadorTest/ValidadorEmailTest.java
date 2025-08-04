package ValidadorTest;
import Service.ValidadorEmail;
import exception.usuario.EmailInvalidoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para a classe ValidadorEmail")
class ValidadorEmailTest {

    private final ValidadorEmail validadorEmail = new ValidadorEmail();

    @Test
    @DisplayName("Teste de validação com email válido")
    void testValidarEmailValido() {
        String emailValido = "teste.usuario+extra@dominio.com";
        Assertions.assertDoesNotThrow(() -> validadorEmail.validar(emailValido));
    }

    @Test
    @DisplayName("Teste de validação com email nulo")
    void testValidarEmailNulo() {
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(null));
        Assertions.assertEquals("ERRO: Email não pode ser vazio!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com email vazio")
    void testValidarEmailVazio() {
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(""));
        Assertions.assertEquals("ERRO: Email não pode ser vazio!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com email sem '@'")
    void testValidarEmailSemArroba() {
        String emailSemArroba = "testedominiocom";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailSemArroba));
        Assertions.assertEquals("ERRO: Email deve conter exatamente um '@'!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com email com múltiplos '@'")
    void testValidarEmailComMultiplosArrobas() {
        String emailComMultiplosArrobas = "teste@dominio@com";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailComMultiplosArrobas));
        Assertions.assertEquals("ERRO: Email deve conter exatamente um '@'!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com email sem domínio")
    void testValidarEmailSemDominio() {
        String emailSemDominio = "teste@";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailSemDominio));
        Assertions.assertEquals("ERRO: Formato de email inválido!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com parte local vazia")
    void testValidarEmailComParteLocalVazia() {
        String emailComParteLocalVazia = "@dominio.com";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailComParteLocalVazia));
        Assertions.assertEquals("ERRO: Parte local do email não pode ser vazia!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com domínio sem ponto")
    void testValidarEmailDominioSemPonto() {
        String emailDominioSemPonto = "teste@dominio";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailDominioSemPonto));
        Assertions.assertEquals("ERRO: Domínio deve conter pelo menos um '.'!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com domínio começando com ponto")
    void testValidarEmailDominioComecandoComPonto() {
        String emailDominioComecandoComPonto = "teste@.dominio.com";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailDominioComecandoComPonto));
        Assertions.assertEquals("ERRO: Domínio não pode começar ou terminar com '.'!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com domínio terminando com ponto")
    void testValidarEmailDominioTerminandoComPonto() {
        String emailDominioTerminandoComPonto = "teste@dominio.com.";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailDominioTerminandoComPonto));
        Assertions.assertEquals("ERRO: Domínio não pode começar ou terminar com '.'!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com caracteres inválidos")
    void testValidarEmailComCaracteresInvalidos() {
        String emailComCaracteresInvalidos = "test!e@dominio.com";
        EmailInvalidoException exception = Assertions.assertThrows(EmailInvalidoException.class, () -> validadorEmail.validar(emailComCaracteresInvalidos));
        Assertions.assertEquals("ERRO: Email contém caracteres inválidos!", exception.getMessage());
    }
}
