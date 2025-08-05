package views;
import model.Usuario;
//metodo que notifica o sucesso do login e delegar a proxima acao, a classe login agora pode so verificar as coisas
public interface GerenciaFluxoLogin {
    void sucessoLogin(Usuario usuarioLogado);

    void fazerLogout();
}
