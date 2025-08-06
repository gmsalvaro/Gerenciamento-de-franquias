package ValidadorTest;
import service.ValidadorNome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidadorNomeTest {

    private ValidadorNome validadorNome;

    @BeforeEach
    void setUp() {
        validadorNome = new ValidadorNome();
    }

    @Test
    @DisplayName("Deve validar um nome simples com sucesso")
    void deveValidarNomeSimples() {
        assertDoesNotThrow(() -> validadorNome.validar("Joao"),
                "A validação de um nome simples não deveria lançar exceção.");
    }

    @Test
    @DisplayName("Deve validar um nome com sobrenome com sucesso")
    void deveValidarNomeComSobrenome() {
        assertDoesNotThrow(() -> validadorNome.validar("Maria Silva"),
                "A validação de um nome completo não deveria lançar exceção.");
    }

    @Test
    @DisplayName("Deve validar um nome no limite mínimo de caracteres")
    void deveValidarNomeComLimiteMinimo() {
        assertDoesNotThrow(() -> validadorNome.validar("Eu"),
                "A validação de um nome com 2 caracteres não deveria falhar.");
    }

    @Test
    @DisplayName("Deve validar um nome no limite máximo de caracteres")
    void deveValidarNomeComLimiteMaximo() {
        String nomeLonguissimo = "a".repeat(50);
        assertDoesNotThrow(() -> validadorNome.validar(nomeLonguissimo),
                "A validação de um nome com 50 caracteres não deveria falhar.");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome nulo")
    void deveLancarExcecaoParaNomeNulo() {
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> validadorNome.validar(null),
                "Deveria lançar IllegalArgumentException para nome nulo.");
        assertEquals("ERRO: Nome não pode ser vazio!", excecao.getMessage(),
                "A mensagem de erro para nome nulo está incorreta.");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome vazio")
    void deveLancarExcecaoParaNomeVazio() {
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> validadorNome.validar(""),
                "Deveria lançar IllegalArgumentException para nome vazio.");
        assertEquals("ERRO: Nome não pode ser vazio!", excecao.getMessage(),
                "A mensagem de erro para nome vazio está incorreta.");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com menos de 2 caracteres")
    void deveLancarExcecaoParaNomeCurto() {
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> validadorNome.validar("A"),
                "Deveria lançar IllegalArgumentException para nome com 1 caractere.");
        assertEquals("ERRO: Nome deve ter entre 3 e 50 caracteres!", excecao.getMessage(),
                "A mensagem de erro para nome curto está incorreta.");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com mais de 50 caracteres")
    void deveLancarExcecaoParaNomeLongo() {
        String nomeSuperLongo = "a".repeat(51);
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> validadorNome.validar(nomeSuperLongo),
                "Deveria lançar IllegalArgumentException para nome com 51 caracteres.");
        assertEquals("ERRO: Nome deve ter entre 3 e 50 caracteres!", excecao.getMessage(),
                "A mensagem de erro para nome longo está incorreta.");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com números")
    void deveLancarExcecaoParaNomeComNumeros() {
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> validadorNome.validar("João123"),
                "Deveria lançar IllegalArgumentException para nome com números.");
        assertEquals("ERRO: Nome deve conter apenas letras e espaços!", excecao.getMessage(),
                "A mensagem de erro para nome com números está incorreta.");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com caracteres especiais")
    void deveLancarExcecaoParaNomeComCaracteresEspeciais() {
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> validadorNome.validar("Maria!@#"),
                "Deveria lançar IllegalArgumentException para nome com caracteres especiais.");
        assertEquals("ERRO: Nome deve conter apenas letras e espaços!", excecao.getMessage(),
                "A mensagem de erro para nome com caracteres especiais está incorreta.");
    }
}