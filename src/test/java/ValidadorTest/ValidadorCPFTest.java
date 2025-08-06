package ValidadorTest;

import utils.ValidadorCPF;
import exception.usuario.CPFInvalidoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para a classe ValidadorCPF")
class ValidadorCPFTest {

    private final ValidadorCPF validadorCPF = new ValidadorCPF();

    @Test
    @DisplayName("Teste de validação com CPF válido")
    void testValidarCPFValido() {
        // Um CPF válido de 11 dígitos, sem caracteres especiais
        String cpfValido = "12345678901";
        Assertions.assertDoesNotThrow(() -> validadorCPF.validar(cpfValido));
    }

    @Test
    @DisplayName("Teste de validação com CPF nulo")
    void testValidarCPFNulo() {
        // A validação de um CPF nulo deve lançar CPFInvalidoException
        CPFInvalidoException exception = Assertions.assertThrows(CPFInvalidoException.class, () -> validadorCPF.validar(null));
        Assertions.assertEquals("ERRO: CPF não pode ser vazio!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com CPF vazio")
    void testValidarCPFVazio() {
        // A validação de um CPF vazio deve lançar CPFInvalidoException
        CPFInvalidoException exception = Assertions.assertThrows(CPFInvalidoException.class, () -> validadorCPF.validar(""));
        Assertions.assertEquals("ERRO: CPF não pode ser vazio!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com CPF com caracteres não-numéricos")
    void testValidarCPFComCaracteresNaoNumericos() {
        // Um CPF com um ponto, por exemplo
        String cpfComPonto = "123.45678901";
        CPFInvalidoException exception = Assertions.assertThrows(CPFInvalidoException.class, () -> validadorCPF.validar(cpfComPonto));
        Assertions.assertEquals("ERRO: CPF deve conter apenas dígitos!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com CPF com menos de 11 dígitos")
    void testValidarCPFMenosDeOnzeDigitos() {
        // Um CPF com 10 dígitos, por exemplo
        String cpfCurto = "1234567890";
        CPFInvalidoException exception = Assertions.assertThrows(CPFInvalidoException.class, () -> validadorCPF.validar(cpfCurto));
        Assertions.assertEquals("ERRO: CPF fora dos padrões!", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de validação com CPF com mais de 11 dígitos")
    void testValidarCPFMaisDeOnzeDigitos() {
        // Um CPF com 12 dígitos, por exemplo
        String cpfLongo = "123456789012";
        CPFInvalidoException exception = Assertions.assertThrows(CPFInvalidoException.class, () -> validadorCPF.validar(cpfLongo));
        Assertions.assertEquals("ERRO: CPF fora dos padrões!", exception.getMessage());
    }
}
