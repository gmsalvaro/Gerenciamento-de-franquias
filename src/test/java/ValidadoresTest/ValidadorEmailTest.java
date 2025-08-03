package test.Service;

import Service.ValidadorEmail;
import exception.usuario.EmailInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorEmailTest {

    private final ValidadorEmail validador = new ValidadorEmail();

    @Test
    void testValidar_emailValido_deveRetornarTrue() {
        String emailValido = "teste.email+1@dominio.com";
        assertDoesNotThrow(() -> {
            assertTrue(validador.validar(emailValido));
        });
    }

    @Test
    void testValidar_emailNulo_deveLancarExcecao() {
        String emailNulo = null;
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailNulo));
    }

    @Test
    void testValidar_emailVazio_deveLancarExcecao() {
        String emailVazio = "";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailVazio));
    }

    @Test
    void testValidar_emailSemArroba_deveLancarExcecao() {
        String emailSemArroba = "teste.dominio.com";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailSemArroba));
    }

    @Test
    void testValidar_emailComMultiplosArrobas_deveLancarExcecao() {
        String emailMultiplosArrobas = "teste@email@dominio.com";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailMultiplosArrobas));
    }

    @Test
    void testValidar_emailComParteLocalVazia_deveLancarExcecao() {
        String emailLocalVazio = "@dominio.com";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailLocalVazio));
    }

    @Test
    void testValidar_emailComDominioSemPonto_deveLancarExcecao() {
        String emailDominioSemPonto = "teste@dominio";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailDominioSemPonto));
    }

    @Test
    void testValidar_emailComDominioInvalido_comecaComPonto_deveLancarExcecao() {
        String emailDominioInvalido = "teste@.dominio.com";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailDominioInvalido));
    }

    @Test
    void testValidar_emailComDominioInvalido_terminaComPonto_deveLancarExcecao() {
        String emailDominioInvalido = "teste@dominio.com.";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailDominioInvalido));
    }

    @Test
    void testValidar_emailComCaracteresInvalidos_deveLancarExcecao() {
        String emailComEspaco = "teste email@dominio.com";
        assertThrows(EmailInvalidoException.class, () -> validador.validar(emailComEspaco));
    }
}