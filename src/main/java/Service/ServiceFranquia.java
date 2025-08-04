package Service;

import Dados.DadosFranquias;
import Model.*;
import exception.ValidacaoException;
import exception.persistencia.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class    ServiceFranquia {
    String FILE_FRANQUIA;
    DadosFranquias dadosFranquias;
   // Map<String, Franquia> franquiasMap;


    public ServiceFranquia(String FILE_FRANQUIA) throws PersistenciaException {
        this.dadosFranquias = new DadosFranquias(FILE_FRANQUIA);
    }

    public void addFranquia(Franquia franquia) throws PersistenciaException {
        // A validação agora busca os dados mais recentes diretamente da fonte.
        for(Franquia f : dadosFranquias.listarMap().values()) {
            if(f.getEndereco().equalsIgnoreCase(franquia.getEndereco()) ||
                    f.getNome().equalsIgnoreCase(franquia.getNome())) {
                throw new LojaInvalidaException("ERRO: já existe uma franquia com esse nome ou endereço!");
            }
        }
        dadosFranquias.adicionar(franquia);
    }

    public void removeFranquia(Franquia franquia, ServiceManager serviceManager) throws PersistenciaException {
        // 1. Busca os dados mais recentes da franquia para garantir que estamos com a lista de lojas correta
        Franquia franquiaAtualizada = buscarPorId(franquia.getId());
        if (franquiaAtualizada == null) {
            throw new PersistenciaException("Franquia '" + franquia.getNome() + "' não encontrada para remoção.");
        }

        // 2. Cria uma cópia da lista de IDs de loja para iterar com segurança
        List<String> idsLojas = new ArrayList<>(franquiaAtualizada.getIdLojas());

        // 3. Delega a remoção completa de cada loja para o ServiceLoja
        for (String idLoja : idsLojas) {
            Loja lojaParaRemover = serviceManager.getServiceLoja().getLojaById(idLoja);
            if (lojaParaRemover != null) {
                // Chama o novo metodo que encapsula toda a lógica de remoção da loja
                serviceManager.getServiceLoja().removerLoja(lojaParaRemover, serviceManager);
            }
        }

        // 4. Após todas as lojas serem removidas, remove a própria franquia
        dadosFranquias.remover(franquiaAtualizada.getId());

        System.out.println("Franquia '" + franquia.getNome() + "' e todos os seus dados associados foram removidos com sucesso!");
    }


    public List<Franquia> listarFranquias() throws PersistenciaException {
        return new ArrayList<>(dadosFranquias.listarMap().values());
    }

    public Franquia buscarPorId(String id) {
        return dadosFranquias.listarMap().get(id);
    }

    public int numLojasFranquia(String idLoja) throws PersistenciaException {
        for(Franquia f:  dadosFranquias.listarMap().values()) {
            if(f.getId().equals(idLoja)) {
                return f.getIdLojas().size();
            }
        }
        return 0;
    }

    public void atualizar(Franquia franquia) throws PersistenciaException {
        // Delega a chamada diretamente.
        dadosFranquias.atualizar(franquia);
    }

    public Franquia getFranquiaDoGerente(Usuario gerente, ServiceLoja serviceloja) {

        if (!(gerente instanceof Gerente))
            return null;

        for (Loja loja : serviceloja.listarTodasAsLojas()) {
            if (loja.getIdsUsuarios() != null && loja.getIdsUsuarios().contains(gerente.getId()))
                return this.buscarPorId(loja.getFranquiaId());

        }
        return null;
    }

    public boolean existeDuplicata(Franquia franquiaParaVerificar) {
       try{
           for (Franquia existente : listarFranquias()) {
               // Se o ID for o mesmo, é a própria franquia, então pulamos a verificação
               if (existente.getId().equals(franquiaParaVerificar.getId())) {
                   continue;
               }

               // Verifica se o nome ou endereço de OUTRA franquia já é igual
               if (existente.getNome().equalsIgnoreCase(franquiaParaVerificar.getNome()) ||
                       existente.getEndereco().equalsIgnoreCase(franquiaParaVerificar.getEndereco())) {
                   return true; // Encontrou uma duplicata
               }
           }
       }catch(PersistenciaException e){
           System.out.println("ERRO! algum dado igual.");
       }
        return false; // Nenhuma duplicata encontrada
    }

}
