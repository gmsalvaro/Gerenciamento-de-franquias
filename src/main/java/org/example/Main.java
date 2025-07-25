
import Dados.*;
import Model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String FRANQUIA_FILE = "frannquia.json";
        DadosFranquias franquias = new DadosFranquias(FRANQUIA_FILE);
        Franquia teste1 = new Franquia("mcc", "tttt", "lerolero");
        Franquia teste2 = new Franquia("mcc", "dasdasdtttt", "lerolero");
        franquias.adicionar(teste1);
        franquias.adicionar(teste2);
        List<Franquia> lista = franquias.listarTodas();
        for (Franquia franquia : lista) {
            System.out.println(franquia.getNome());
        }
        Franquia novo = lista.getFirst();
        novo.getId();
        Loja loja = new Loja("lerolero", "lero", novo.getId());
        novo.adicionarIdLoja(loja.getId());
        franquias.atualizar(novo);

    }
}
