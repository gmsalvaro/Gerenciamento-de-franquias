package Service;

import Model.Franquia;
import exception.ValidacaoException;

import java.util.Map;

public class ServiceValidadores {

    public boolean verificaFranquiasIguais(Map<String, Franquia> franquias, Franquia tester)  {
        for(Map.Entry<String, Franquia> entry : franquias.entrySet()) {
            if( entry.getValue().getEndereco().equals(tester.getEndereco()) ||
                    entry.getValue().getNome().equals(tester.getNome())) {
                return true;
            }
        }
        return false;
    }
}
