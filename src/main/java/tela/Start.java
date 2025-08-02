package tela;

import Model.Usuario;
import Service.ServiceManager;
import exception.persistencia.PersistenciaException;

public class Start {

    private ServiceManager serviceManager;

    public Start() throws PersistenciaException {
        this.serviceManager = new ServiceManager("lero");
        Login login = new Login(serviceManager, new LoginSucesso());
        login.setVisible(true);
    }
}