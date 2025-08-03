package ValidadoresTest;

import Service.ValidadorCPF;
import exception.usuario.CPFInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorCPFTest {

    private final ValidadorCPF validador = new ValidadorCPF();

    @Test
    void testValidar_cpfValido_deveRetornarTrue() {
        String cpfValido = "12345678901";
        assertDoesNotThrow(() -> {
            assertTrue(validador.validar(cpfValido));
        });
    }

    @Test
    void testValidar_cpfNulo_deveLancarExcecao() {
        String cpfNulo = null;
        assertThrows(CPFInvalidoException.class, () -> validador.validar(cpfNulo));
    }

    @Test
    void testValidar_cpfVazio_deveLancarExcecao() {
        String cpfVazio = "";
        assertThrows(CPFInvalidoException.class, () -> validador.validar(cpfVazio));
    }

    @Test
    void testValidar_cpfCurto_deveLancarExcecao() {
        String cpfCurto = "1234567890"; // 10 dígitos
        assertThrows(CPFInvalidoException.class, () -> validador.validar(cpfCurto));
    }

    @Test
    void testValidar_cpfLongo_deveLancarExcecao() {
        String cpfLongo = "123456789012"; // 12 dígitos
        assertThrows(CPFInvalidoException.class, () -> validador.validar(cpfLongo));
    }

    @Test
    void testValidar_cpfComLetras_deveLancarExcecao() {
        String cpfComLetras = "1234567890A";
        assertThrows(CPFInvalidoException.class, () -> validador.validar(cpfComLetras));
    }

    @Test
    void testValidar_cpfComCaracteresEspeciais_deveLancarExcecao() {
        String cpfComSimbolos = "123.456.78-01";
        assertThrows(CPFInvalidoException.class, () -> validador.validar(cpfComSimbolos));
    }
}