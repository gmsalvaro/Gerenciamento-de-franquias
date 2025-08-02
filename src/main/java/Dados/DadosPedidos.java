package Dados;

import Model.Franquia;
import Model.Pedido;
import Model.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import exception.persistencia.ErroCarregarArquivosException;
import exception.persistencia.ErroSalvarLojaException;
import exception.persistencia.LojaNaoCarregadaException;
import exception.persistencia.PersistenciaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap; // Bom para uso multi-thread se aplicável

public class DadosPedidos extends DadosGenerico <Pedido>{


    public DadosPedidos(String filePath) throws PersistenciaException {
        super(filePath);
        carregar();
    }

    @Override
    protected TypeReference<List<Pedido>> getTypeReference(){
        return new TypeReference<List<Pedido>>() {};
    }

    public Map<String, Pedido> getPedidosMap() throws PersistenciaException {
        return getEntidadesMap();
    }

}