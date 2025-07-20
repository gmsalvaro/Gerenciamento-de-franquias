package Service;
import Model.Franquia;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GerenciamentoFranquias {
    private final String FRANQUIAS_FILE = "franquias.json";
    private List<Franquia> franquias;
    Dados dados;

    public GerenciamentoFranquias(){
        franquias = dados.carregarDados(FRANQUIAS_FILE, new TypeReference<List<Franquia>>() {});
        this.dados = new Dados();
    }
    public List<Franquia> getAllFranquias() {
        return new ArrayList<>(franquias);
    }

    public Optional<Franquia> getFranquiaById(String id) {
        return franquias.stream().filter(f -> f.getId().equals(id)).findFirst();
    }

    public void addFranquia(Franquia franquia) {
        if (franquia.getId() == null || franquia.getId().isEmpty()) {
            franquia.setId(UUID.randomUUID().toString());
        }
        franquias.add(franquia);
        dados.salvarDados(FRANQUIAS_FILE, franquias);
    }

    public boolean removeFranquia(String id) {
        boolean removed = franquias.removeIf(f -> f.getId().equals(id));
        if (removed) {
            dados.salvarDados(FRANQUIAS_FILE, franquias);
        }
        return removed;
    }

    public boolean updateFranquia(Franquia updatedFranquia) {
        boolean flag = false;
        for (int i = 0; i < franquias.size(); i++) {
            if (franquias.get(i).getId().equals(updatedFranquia.getId())) {
                franquias.set(i, updatedFranquia);
                flag = true;
                break;
            }
        }
        if (flag) {
            dados.salvarDados(FRANQUIAS_FILE, franquias);
        }
        return flag;
    }
}
