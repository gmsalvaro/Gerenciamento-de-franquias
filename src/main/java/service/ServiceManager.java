package service;

import exception.persistencia.PersistenciaException;

public class ServiceManager {
    private final ServiceUsuario serviceUsuario;
    private final ServiceLoja serviceLoja;
    private final ServiceProduto serviceProduto;
    private final ServicePedido servicePedido;
    private final ServiceFranquia serviceFranquia;
    private final ServiceRelatorio serviceRelatorio;

    public ServiceManager(String FILE_USUARIO, String FILE_LOJA, String FILE_PRODUTOS, String FILE_PEDIDOS, String FILE_FRANQUIAS) throws PersistenciaException {
        this.serviceUsuario = new ServiceUsuario(FILE_USUARIO);
        this.serviceLoja = new ServiceLoja(FILE_LOJA);
        this.serviceProduto = new ServiceProduto(FILE_PRODUTOS);
        this.servicePedido = new ServicePedido(FILE_PEDIDOS);
        this.serviceFranquia = new ServiceFranquia(FILE_FRANQUIAS);
        this.serviceRelatorio = new ServiceRelatorio(serviceLoja, servicePedido, serviceUsuario, serviceFranquia);
    }

    public ServiceRelatorio getServiceRelatorio() {return serviceRelatorio;}

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
