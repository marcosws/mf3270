# mf3270

Biblioteca Java para automação e interação com terminais 3270, utilizando comandos de alto nível para facilitar testes e integrações com sistemas legados.

## Visão Geral

O projeto **mf3270** fornece uma API orientada a objetos para controlar sessões 3270, encapsulando comandos comuns como navegação, preenchimento de campos, leitura de tela, espera por eventos e manipulação de teclas especiais.

A classe principal é `S3270Emulator`, que utiliza uma instância de `S3270Session` para comunicação com o terminal.

## Principais Funcionalidades

- Obtenção da tela em formato ASCII
- Busca de campos por label e envio de texto
- Movimentação do cursor
- Pressionamento de teclas especiais (Enter, PF, PA, Tab, etc.)
- Espera por eventos ou textos específicos na tela
- Extração de textos de posições ou campos específicos

## Exemplo de Uso

```java
S3270Session session = new S3270Session(...); // inicialização da sessão
S3270Emulator emulator = new S3270Emulator(session);

// Obter a tela atual
String tela = emulator.getScreen();

// Enviar texto para um campo identificado por label
emulator.sendTextByField("Username:", "admin");

// Pressionar Enter
emulator.enter();

// Esperar até que um texto apareça na tela
emulator.waitForText("Bem-vindo", 5000);
```

## Principais Métodos

| Método                              | Descrição                                                                                   |
|--------------------------------------|---------------------------------------------------------------------------------------------|
| `getScreen()`                       | Retorna a tela atual em formato ASCII, removendo linhas de controle.                        |
| `asciiScreen()`                     | Igual ao anterior, mas com tentativas de leitura até obter uma tela válida.                 |
| `sendTextByField(label, texto)`     | Envia texto para o campo identificado pelo label.                                           |
| `moveCursor(row, col)`              | Move o cursor para a posição especificada.                                                  |
| `sendString(text)`                  | Digita o texto na posição atual do cursor.                                                  |
| `enter()`                           | Pressiona a tecla Enter.                                                                    |
| `pressPF(PFKey)`                    | Pressiona uma tecla PF (PF1, PF2, ...).                                                     |
| `pressPA(PAKey)`                    | Pressiona uma tecla PA (PA1, PA2, ...).                                                     |
| `waitForText(text, timeoutMillis)`  | Aguarda até que o texto apareça na tela ou o tempo limite seja atingido.                    |
| `getTextByField(label, size)`       | Extrai texto a partir de um campo identificado pelo label.                                  |
| `getTextBeforeField(label, size)`   | Extrai texto imediatamente antes de um campo identificado pelo label.                       |

## Tratamento de Exceções

A biblioteca lança a exceção customizada `S3270SessionException` para erros de sessão, como falha ao encontrar campos ou timeout ao aguardar textos.

Exemplo de tratamento:
```java
try {
    emulator.waitForText("Login realizado", 3000);
} catch (S3270SessionException e) {
    // Log e tratamento
}
```

## Testes

Os testes utilizam JUnit 5 e Mockito para simular sessões e validar o comportamento dos métodos de alto nível.

## Estrutura do Projeto

- `src/main/java/com/github/marcosws/mf3270/` — Código principal
- `src/test/java/com/github/marcosws/mf3270/` — Testes unitários

## Requisitos

- Java 11+
- (Opcional) Ferramenta s3270 instalada para integração real

## Autor

Marcos Willian de Souza
