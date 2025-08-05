package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusPedido {
    PENDENTE("Pendente"),
    EM_PREPARO("Em Preparo"),
    ANALISE_CANCELAMENTO("Análise de Cancelamento"),
    ANALISE_ALTERACAO("Análise de Alteração"),
    PRONTO_PARA_RETIRADA("Pronto para Retirada"),
    ENTREGUE("Entregue"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado"), NEGADO("Alteração Negada");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Anotação @JsonValue: Diz ao Jackson para usar o resultado deste método
     * ao converter o enum para JSON (escrita).
     */
    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /**
     * Anotação @JsonCreator: Diz ao Jackson para usar este método
     * ao converter do JSON para o enum (leitura).
     */
    @JsonCreator
    public static StatusPedido fromDescricao(String descricao) {
        // Itera por todos os valores do enum
        for (StatusPedido status : StatusPedido.values()) {
            // Se a descrição corresponder (ignorando maiúsculas/minúsculas), retorna o enum correto
            if (status.descricao.equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        // Se não encontrar uma correspondência, lança um erro
        throw new IllegalArgumentException("Status desconhecido: " + descricao);
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
