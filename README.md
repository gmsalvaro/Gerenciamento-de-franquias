# Gerenciamento de Franquias

## Introdução

O **Gerenciamento de Franquias** é um sistema desenvolvido em **Java** com **interface gráfica Swing**, voltado para simular e gerenciar uma rede de franquias. A aplicação oferece controle completo sobre **usuários (Dono, Gerente, Vendedor)**, **lojas**, **produtos** e **pedidos**, utilizando arquitetura em camadas, persistência com arquivos **JSON** e organização modular por pacotes.

Principais destaques:

- Autenticação com perfis de acesso distintos (Dono, Gerente, Vendedor)
- Persistência com JSON via biblioteca **Jackson**
- Interface gráfica em **Swing**
- Camada de serviços com validações e regras de negócio
- **Testes automatizados** com JUnit
- Estrutura modular com pacotes `model`, `repository`, `service`, `views`, `exception` e `test`

## Como executar o projeto

### Requisitos

- **Java 17 ou superior**
- **Maven** instalado e configurado

### Compilar e executar via terminal

```bash
# Navegue até o diretório raiz do projeto
cd Gerenciamento-de-franquias

# Limpa e compila o projeto, gerando o JAR com dependências
mvn clean package

# Executa a aplicação
java -jar target/GerenciamentoDeFranquias-1.0-SNAPSHOT-jar-with-dependencies.jar

#Executar testes
mvn test
