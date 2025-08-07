//Álvaro José Souza Gomes 202465095A
//Heitor Coelho Costa 202465508B
//Pedro Nalon Moraes 202465507B

package views;
import model.Usuario;
//metodo que notifica o sucesso do login e delegar a proxima acao, a classe login agora pode so verificar as coisas
public interface GerenciaFluxoLogin {
    void sucessoLogin(Usuario usuarioLogado);

    void fazerLogout();
}
