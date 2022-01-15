# Sensedia API Mocks UI
Essa projeto tem como premissa gerenciar cenários de mocks sensívies a contexto.

Ou seja, devolver uma response baseada no contexto de uma dada request a uma operação.

### Requisitos mínimos
* Java JDK 1.8
* Maven 3.6.0
* Docker (caso queira rodar a aplicação em um container)
* Lombok 1.18.10

### Variáveis de Ambiente
* **REDIS_HOST**: Endereço do Redis que serve como base de dados. Opcional. Por padrão localhost
* **REDIS_PORT**: Porta do Redis que serve como base de dados. Opcional. Por padrão 6379.
* **REDIS_PASSWORD**: Senha do Redis que serve como base de dados. Opcional. Por padrão vazio.
* **MOCKS_BACKEND_PORT**: Porta do serviço de mocks. Opcional. Por padrão 8090.

### Building and Running
Para buildar o projeto utilizamos o seguinte comando na raiz:
``mvn clean package``

Para rodar a aplicação utilizamos o comando:
``mvn sprint-boot:run``

### Docker
Sintam-se livres para utilizar a imagem de meu repositório: 
``docker pull gabrieln/apimocks:latest``

Se quiserem utilizar uma imagem do zero, existe um arquivo Dockerfile incluso nesse projeto que faz o build da imagem do Docker. Para criar uma imagem local basta utilizar o comando: 
``docker build -t seu_usuario/apimocks:latest .``

Para publicar essa imagem é necessário criar uma conta no Docker Hub e associar essa conta em seu Docker local. Logo em seguida utilizar o comando:
``docker push seu_usuario/apimocks:latest``

Para criar um novo container a partir dessa imagem basta utilizar o comando:
``docker run -d -p 8090:8090 --name api_mocks -e "REDIS_HOST=localhost" -e "REDIS_PORT=6379" -e "REDIS_PASSWORD=password" -e "MOCKS_BACKEND_PORT=8090" seu_usuario/apimocks:latest``

Ou simplesmente:
``docker run -d -p 8000:8000 --name api_mocks seu_usuario/apimocks:latest``

### Known Issues
* Atualmente é possível criar APIs com basePath duplicados. Isso será tratado nas próximas versões.

### Next Features
* Documentação da API em Swagger (implementaremos a documentação em Swagger utilizando o SpringFox)
* Capacidade de inativar / reativar Operações de Mocks
* Criação e controle de perfils de acesso aos Mocks
* Capacidade de validação de "conditions" de cenários
