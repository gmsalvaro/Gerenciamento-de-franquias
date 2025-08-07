# Gerenciamento de Franquias

## Visão Geral do Funcionamento

### 1.1 Introdução

O Gerenciamento de Franquias é um sistema desenvolvido em Java que simula e gerencia uma rede de franquias de forma centralizada. A aplicação oferece controle sobre lojas, produtos e pedidos, com três perfis de usuário distintos:

- Dono
- Gerente
- Vendedor

A arquitetura do sistema é organizada em camadas, separando interface, regras de negócio e persistência de dados. Os dados são armazenados em arquivos JSON. O fluxo do sistema inicia-se com a autenticação do usuário, que é direcionado para a interface correspondente ao seu perfil.

### 1.2 Tópicos Importantes

- Sistema de Build: Apache Maven. As dependências (como Jackson e JUnit) são declaradas no `pom.xml`.
- Execução:
  - Via IDE (classe `Main` no pacote `org.example`)
  - Via terminal:
    ```bash
    mvn clean package
    java -jar target/GerenciamentoDeFranquias-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
- Repositório GitHub: https://github.com/gmsalvaro/Gerenciamento-de-franquias

## Estrutura de Pacotes

O projeto está modularizado para facilitar manutenção e escalabilidade.

### 2.1 Detalhamento

#### repository

Gerencia persistência e conversão de dados JSON.

- Principais classes: `DadosUsuario`, `DadosLojas`, `DadosPedidos`, `DadosFranquias`, `DadosProdutos`
- Utiliza a biblioteca Jackson

#### exception

Exceções personalizadas para erros específicos.

- Principais classes: `AutenticacaoException`, `SenhaInvalidaException`, `LojaInvalidaException`, `CPFInvalidoException`
- Organizado em subpacotes como `autenticacao`, `persistencia`, etc.

#### model

Define as entidades de negócio.

- Principais classes: `Usuario` (abstrata), `Dono`, `Gerente`, `Vendedor`, `Franquia`, `Loja`, `Produto`, `Pedido`
- Relacionamentos:
  - Franquia possui várias lojas
  - Loja pertence a uma franquia e possui funcionários, produtos e pedidos
  - Pedido é feito por um vendedor e possui produtos
  - Produto pode ser vendido em várias lojas
- Serialização com Jackson usando `@JsonTypeInfo` e `@JsonSubTypes`

#### org.example

Pacote principal com a classe `Main`.

- Inicializa diretórios, configura `ServiceManager`, realiza a carga de dados e exibe o login

#### service

Contém a lógica de negócio.

- Principais classes: `ServiceManager`, `ServiceUsuario`, `ServiceLoja`, `ServiceProduto`, `ServicePedido`, `ServiceFranquia`, `ServiceRelatorio`
- Realiza validações, aplica regras de negócio, coordena operações complexas

#### views

Interface gráfica do usuário utilizando Swing.

- Principais classes: `Login`, `PainelPrincipal`, `InterfaceDono`, `InterfaceGerente`, `InterfaceVendedor`, `InterfaceGerenciarLojas`, `InterfaceGerenciarProdutos`, `InterfaceGerenciarPedidos`, `InterfaceGerenciarUsuario`, `InterfaceGerenciarVendas`, `InterfaceRelatorioLoja`
- Captura entrada do usuário e delega para os serviços

#### testes

Testes automatizados para garantir o funcionamento do sistema.

- `ServiceTest`: testa regras de negócio
- `PersistenciaTest`: testa leitura e escrita JSON
- `ValidacaoTest`: testa validadores como `ValidadorCPF`, `ValidadorEmail`, etc.

## Herança

O projeto utiliza herança para as classes `Dono`, `Gerente` e `Vendedor`, que herdam de `Usuario`. Isso permite o reaproveitamento de atributos e métodos comuns.

## Polimorfismo

O método `getPermissao()` é sobrescrito nas subclasses de `Usuario`:

- Dono: retorna 1
- Gerente: retorna 2
- Vendedor: retorna 3

Esse comportamento polimórfico permite tratamento genérico no sistema.

## Encapsulamento

Atributos são privados e acessados/modificados via getters e setters. Isso garante proteção dos dados e controle de acesso.

## Arquivos JSON

Os dados são persistidos em arquivos JSON separados por entidade (`usuarios.json`, `lojas.json`, etc.).

- Serialização/desserialização com Jackson
- Campo `"tipo"` usado para identificar a subclasse no processo de desserialização
