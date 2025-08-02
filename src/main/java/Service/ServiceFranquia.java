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
    Map<String, Franquia> franquiasMap;


    public ServiceFranquia(String FILE_FRANQUIA) throws PersistenciaException {
        this.FILE_FRANQUIA = FILE_FRANQUIA;
        try{
            dadosFranquias = new DadosFranquias(FILE_FRANQUIA);
            franquiasMap = dadosFranquias.listarMap();

        }catch(ErroCarregarArquivosException e){
            throw new ErroCarregarArquivosException("ERRO: não foi possível inicializar o serviço de franquias!");
        }
    }

    public void addFranquia(Franquia franquia) throws PersistenciaException {
        for(Map.Entry<String, Franquia> entry : franquiasMap.entrySet()) {
            if( entry.getValue().getEndereco().equals(franquia.getEndereco()) ||  //Verifica se tem franquias com mesmo nome ou endereço
                    entry.getValue().getNome().equals(franquia.getNome())) {
                throw new LojaInvalidaException("ERRO: já existe uma loja com esse nome ou endereço!"); // Excessao
            }
        }
        dadosFranquias.adicionar(franquia);
        franquiasMap = dadosFranquias.listarMap();
    }

    public void removeFranquia(Franquia franquia, ServiceManager serviceManager) throws PersistenciaException {
        if (!franquiasMap.containsKey(franquia.getId())) {
            throw new PersistenciaException("Franquia '" + franquia.getNome() + "' não encontrada para remoção.");
        }
        try {
            // 1. Remover Lojas e seus dependentes (Usuários, Produtos, Pedidos)
            List<String> idsLojas = new ArrayList<>(franquia.getIdLojas());
            for (String idLoja : idsLojas) {
                Loja lojaParaRemover = serviceManager.getServiceLoja().getLojaById(idLoja);
                if (lojaParaRemover != null) {
                    // Remover Usuários da Loja
                    List<String> idsUsuarios = new ArrayList<>(lojaParaRemover.getIdsUsuarios());
                    for (String idUsuario : idsUsuarios) {
                        Usuario usuarioParaRemover = serviceManager.getServiceUsuario().getUsuarioById(idUsuario);
                        if (usuarioParaRemover != null) {
                            serviceManager.getServiceUsuario().removeUsuario(usuarioParaRemover);
                            System.out.println("Usuário " + usuarioParaRemover.getNome() + " removido.");
                        }
                    }

                    // Remover Produtos da Loja
                    List<String> idsProdutos = new ArrayList<>(lojaParaRemover.getIdProdutos());
                    for (String idProduto : idsProdutos) {
                        Produto produtoParaRemover = serviceManager.getServiceProduto().getProdutoById(idProduto);
                        if (produtoParaRemover != null) {
                            serviceManager.getServiceProduto().removerProduto(produtoParaRemover);
                            System.out.println("Produto " + produtoParaRemover.getNome() + " removido.");
                        }
                    }

                    // Remover Pedidos da Loja
                    List<String> idsPedidos = new ArrayList<>(lojaParaRemover.getIdPedidos());
                    for (String idPedido : idsPedidos) {
                        Pedido pedidoParaRemover = serviceManager.getServicePedido().getPedidoById(idPedido);
                        if (pedidoParaRemover != null) {
                            serviceManager.getServicePedido().removerPedido(pedidoParaRemover);
                            System.out.println("Pedido " + pedidoParaRemover.getId() + " removido.");
                        }
                    }
                    // Remover a própria Loja
                    serviceManager.getServiceLoja().removerLoja(lojaParaRemover.getId());
                    franquia.removeIDLoja(lojaParaRemover.getId());
                    serviceManager.getServiceFranquia().atualizar(franquia);
                    System.out.println("Loja " + lojaParaRemover.getNome() + " removida.");
                }
            }
            dadosFranquias.remover(franquia.getId());
            this.franquiasMap = dadosFranquias.listarMap();
            System.out.println("Franquia " + franquia.getNome() + " e todos os seus dados associados removidos com sucesso!");
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Erro ao remover dados associados à franquia '" + franquia.getNome() + "': " + e.getMessage());
        }
    }

    public List<Franquia> listarFranquias() throws PersistenciaException {
        return new ArrayList<>(franquiasMap.values());
    }

    public Franquia buscarPorId(String id) {
        return franquiasMap.get(id);
    }

    public int numLojasFranquia(String idLoja) throws PersistenciaException {
        for(Franquia f:  franquiasMap.values()) {
            if(f.getId().equals(idLoja)) {
                return f.getIdLojas().size();
            }
        }
        return 0;
    }

    public void atualizar(Franquia franquia) throws PersistenciaException {
        if (franquiasMap.containsKey(franquia.getId())) {
            dadosFranquias.atualizar(franquia);
            franquiasMap = dadosFranquias.listarMap();
        } else
            throw new LojaNaoAtualizadaException("ERRO: não foi possível atualizar a loja!");
    }
}
