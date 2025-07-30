package Service;

import Service.ServiceLoja;
import Service.ServicePedido;
import Service.ServiceProduto;
import Service.ServiceUsuario;
import exception.persistencia.PersistenciaException;

public class ServiceManager {
    private final ServiceUsuario serviceUsuario;
    private final ServiceLoja serviceLoja;
    private final ServiceProduto serviceProduto;
    private final ServicePedido servicePedido;
    private final ServiceFranquia serviceFranquia;

    public ServiceManager(String basePath) throws PersistenciaException {
        this.serviceUsuario = new ServiceUsuario( "usuario.json");
        this.serviceLoja = new ServiceLoja( "lojas.json");
        this.serviceProduto = new ServiceProduto( "produtos.json");
        this.servicePedido = new ServicePedido("pedidos.json");
        this.serviceFranquia = new ServiceFranquia("franquia.json");
    }

    public ServiceFranquia getServiceFranquia() {
        return serviceFranquia;
    }

    public ServiceUsuario getServiceUsuario() {
        return serviceUsuario;
    }

    public ServiceLoja getServiceLoja() {
        return serviceLoja;
    }

    public ServiceProduto getServiceProduto() {
        return serviceProduto;
    }

    public ServicePedido getServicePedido() {
        return servicePedido;
    }
}
