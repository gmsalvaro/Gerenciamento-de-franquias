package tela;

import Model.Usuario;

public class LoginSucesso implements GerenciaFluxoLogin{

    @Override
    public void sucessoLogin(Usuario usuarioLogado) {
        System.out.println("Login feito com sucesso pelo usuário: " + usuarioLogado.getNome());
        //new InterfacePrincipal(usuarioLogado).setVisible(true);
    }
}
