package Service;

import Dados.DadosLojas;
import Model.Franquia;
import Model.Gerente;
import Model.Loja;
import Model.Usuario;
import exception.persistencia.LojaInvalidaException;
import exception.persistencia.LojaNaoAtualizadaException;
import exception.persistencia.LojaNaoRemovidaException;
import exception.persistencia.PersistenciaException;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceLoja {
    private final String FILE_LOJA;
    private final DadosLojas dadosLojas;
    private Map<String, Loja> lojasMap;

    public ServiceLoja(String FILE_LOJA) throws PersistenciaException {
        this.FILE_LOJA = FILE_LOJA;
        this.dadosLojas = new DadosLojas(FILE_LOJA);
        this.lojasMap = dadosLojas.getLojasMap();
    }

    public void addLoja(Loja loja, Franquia franquia) throws PersistenciaException {
        for (Loja l : lojasMap.values()) { // verificar essa validação !
            if (l.getNome().equalsIgnoreCase(loja.getNome()) ||
                l.getEndereco().equalsIgnoreCase(loja.getEndereco())) {
                throw new LojaInvalidaException("Loja com nome ou endereço já existente.");
            }
        }
        if(franquia == null) {
            throw new LojaInvalidaException("Franquia invalida");
        }
        loja.setFranquiaId(franquia.getId());
        franquia.adicionarIdLoja(loja.getId());
        dadosLojas.adicionar(loja);
        lojasMap = dadosLojas.getLojasMap();
    }

    public void auxRemoverLoja(String id) throws PersistenciaException {
        if (lojasMap.containsKey(id)) {
            dadosLojas.remover(id);
            lojasMap = dadosLojas.getLojasMap();
        } else {
            throw new LojaNaoRemovidaException("Loja não encontrada para remoção.");
        }
    }

    public void removerLoja(Loja lojaParaRemover, ServiceFranquia serviceFranquia) throws PersistenciaException {
        // 1. Verifica se a loja existe antes de tentar qualquer coisa
        if (lojaParaRemover == null || !lojasMap.containsKey(lojaParaRemover.getId())) {
            throw new LojaNaoRemovidaException("Loja não encontrada para remoção.");
        }

        // 2. Busca a franquia-mãe usando o ID armazenado na loja
        String idFranquia = lojaParaRemover.getFranquiaId();
        Franquia franquiaMae = serviceFranquia.buscarPorId(idFranquia);

        if (franquiaMae != null) {
            // 3. Remove a associação da loja na franquia
            franquiaMae.removeIDLoja(lojaParaRemover.getId());

            // 4. Salva o estado atualizado da franquia
            serviceFranquia.atualizar(franquiaMae);
        }

        // 5. Remove a loja do seu próprio arquivo de dados
        // Chama o metodo remover(id) que já existe na classe
        this.auxRemoverLoja(lojaParaRemover.getId());
    }

    public List<Loja> listarTodasAsLojas() {
        return new ArrayList<>(lojasMap.values());
    }

    public List<Loja> listarPorIDFranquia(String id) {
        ArrayList<Loja> lojas = new ArrayList<>();
        for(Loja l : lojasMap.values()) {
            if (l.getFranquiaId().equalsIgnoreCase(id)) {
                lojas.add(l);
            }
        }
        return lojas;
    }


    public void atualizarLoja(Loja lojaAtualizada) throws PersistenciaException {
        if (lojasMap.containsKey(lojaAtualizada.getId())) {
            dadosLojas.atualizar(lojaAtualizada);
            lojasMap = dadosLojas.getLojasMap();
        } else {
            throw new LojaNaoAtualizadaException("Loja não encontrada para atualização.");
        }
    }

    public boolean lojaTemGerente(Loja loja, ServiceManager serviceManager) {
        return auxLojaTemGerente(loja, serviceManager.getServiceUsuario());
    }

    private boolean auxLojaTemGerente(Loja loja, ServiceUsuario serviceUsuario) {
        if (loja.getIdsUsuarios() == null || loja.getIdsUsuarios().isEmpty()) {
            return false;
        }

        // Versão corrigida para funcionar com o seu método getUsuarioById
        return loja.getIdsUsuarios().stream()         // 1. Cria um fluxo de IDs (String)
                .map(serviceUsuario::getUsuarioById)  // 2. Para cada ID, busca o Usuario (pode ser null)
                .filter(Objects::nonNull)             // 3. IMPORTANTE: Remove todos os nulos do fluxo
                .anyMatch(usuario -> usuario instanceof Gerente); // 4. Verifica se algum dos usuários não-nulos é Gerente
    }

    public String getGerente(Loja loja, ServiceManager serviceManager) {
        String idGerente = null;

        if(!lojaTemGerente(loja, serviceManager))
            return idGerente;

        else{
            for(String idUsuario : loja.getIdsUsuarios()) {
                Usuario usuario = serviceManager.getServiceUsuario().getUsuarioById(idUsuario);
                if(usuario instanceof Gerente)
                    idGerente = usuario.getId();
            }
        }

        return idGerente;
    }

    public String getNomeGerenteDaLoja(Loja loja, ServiceManager serviceManager) {
        if(loja.getIdsUsuarios() == null || loja.getIdsUsuarios().isEmpty() | !lojaTemGerente(loja, serviceManager))
            return null;
        else{
            for(String idUsuario : loja.getIdsUsuarios()) {
                Usuario usuario = serviceManager.getServiceUsuario().getUsuarioById(idUsuario);
                if(usuario instanceof Gerente)
                    return usuario.getNome();
            }
        }
        return null;
    }


    public Loja getLojaById(String idLoja) {
                return lojasMap.get(idLoja);
    }

    public void designarGerenteParaLoja(Gerente novoGerente, Loja novaLoja, ServiceUsuario serviceUsuario) throws PersistenciaException {
        // 1. VERIFICA SE O GERENTE JÁ ESTÁ EM OUTRA LOJA
        Optional<Loja> lojaAntigaOpt = buscarLojaPorGerente(novoGerente);

        if (lojaAntigaOpt.isPresent()) {
            Loja lojaAntiga = lojaAntigaOpt.get();

            // Se a loja antiga é a mesma que a nova, não há nada a fazer.
            if (lojaAntiga.getId().equals(novaLoja.getId())) {
                return; // Interrompe a operação
            }

            // 2. REMOVE O GERENTE DA LOJA ANTIGA
            System.out.println("Removendo gerente '" + novoGerente.getNome() + "' da loja antiga '" + lojaAntiga.getNome() + "'");
            lojaAntiga.getIdsUsuarios().remove(novoGerente.getId());
            this.atualizarLoja(lojaAntiga); // Salva o estado da loja antiga
        }

        // 3. REMOVE QUALQUER OUTRO GERENTE QUE ESTEJA NA NOVA LOJA
        // (Esta lógica que você já tinha continua importante)
        if (novaLoja.getIdsUsuarios() != null) {
            novaLoja.getIdsUsuarios().removeIf(idUsuario -> {
                Optional<Usuario> uOpt = serviceUsuario.buscarPorId(idUsuario); // Usando o metodo seguro com Optional
                return uOpt.isPresent() && uOpt.get() instanceof Gerente;
            });
        }

        // 4. ADICIONA O GERENTE À NOVA LOJA
        System.out.println("Adicionando gerente '" + novoGerente.getNome() + "' à nova loja '" + novaLoja.getNome() + "'");
        novaLoja.addUsuarioID(novoGerente.getId());
        this.atualizarLoja(novaLoja); // Salva o estado da nova loja
    }


    public String buscarStatusLojaPorGerente(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        // Itera por todas as lojas cadastradas
        for (Loja loja : listarTodasAsLojas()) {
            // Verifica se a lista de usuários da loja contém o ID do gerente
            if (loja.getIdsUsuarios() != null && loja.getIdsUsuarios().contains(usuario.getId())) {
                return loja.getNome();
            }
        }
        return "Gerente disponível";
    }

    public List<Loja> listarLojasSemGerente(ServiceManager serviceManager) {
        // Usando Streams para um código mais limpo:
        return listarTodasAsLojas().stream() // Pega todas as lojas
                .filter(loja -> !lojaTemGerente(loja, serviceManager)) // Filtra apenas as que NÃO têm gerente
                .collect(Collectors.toList()); // Coleta o resultado em uma nova lista
    }

    public Optional<Loja> buscarLojaPorGerente(Usuario usuario) {
        if (usuario == null) {
            return Optional.empty();
        }

        // Itera por todas as lojas cadastradas
        return listarTodasAsLojas().stream()
                .filter(loja -> loja.getIdsUsuarios() != null && loja.getIdsUsuarios().contains(usuario.getId()))
                .findFirst(); // Retorna a primeira loja que encontrar
    }
}

