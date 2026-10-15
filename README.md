# terminus3270

Biblioteca Java para automação e interação com terminais 3270, utilizando comandos de alto nível para facilitar testes e integrações com sistemas legados.

## Visão Geral

O projeto **terminus3270** fornece uma API orientada a objetos para controlar sessões 3270, encapsulando comandos comuns como navegação,# terminus3270

Biblioteca Java para automação e interação com terminais 3270, utilizando comandos de alto nível para facilitar testes e integrações com sistemas legados.

## Visão Geral

O projeto **terminus3270** fornece uma API orientada a objetos para controlar sessões 3270, encapsulando comandos comuns como navegação, preenchimento de campos, leitura de tela, espera por eventos e manipulação de teclas especiais.

A classe principal é `S3270Emulator`, que utiliza uma instância de `S3270Session` para comunicação com o terminal.

## Principais Funcionalidades

- Obtenção da tela em formato ASCII
- Busca de campos por label e envio de texto
- Movimentação do cursor
- Pressionamento de teclas especiais (Enter, PF, PA, Tab, etc.)
- Espera por eventos ou textos específicos na tela
- Extração de textos de posições ou campos específicos

## Exemplo de Uso

O exemplo a seguir demonstra como usar a biblioteca `terminus3270` para interagir com um terminal 3270:

```java
import com.github.marcosws.terminus3270.S3270Emulator;
import com.github.marcosws.terminus3270.exceptions.S3270SessionException;

public class Main {
    public static void main(String[] args) {
        try {
            // Inicializa a sessão 3270
            S3270Emulator emulator = new S3270Emulator();

            // Conecta ao terminal 3270
            emulator.connect();

            // Envia texto para um campo identificado por label
            emulator.sendTextByField("Username:", "admin");

            // Pressiona Enter
            emulator.enter();

            // Espera até que um texto apareça na tela
            if (emulator.waitForText("Bem-vindo", 5000)) {
                System.out.println("Login realizado com sucesso!");
            } else {
                System.out.println("Falha no login.");
            }

            // Desconecta da sessão 3270
            emulator.disconnect();
        } catch (S3270SessionException e) {
            System.err.println("Erro ao interagir com o terminal 3270: " + e.getMessage());
        }
    }
}
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

- `src/main/java/com/github/marcosws/terminus3270/` — Código principal
- `src/test/java/com/github/marcosws/terminus3270/` — Testes unitários

## Requisitos

- Java 11+
- (Opcional) Ferramenta s3270 instalada para integração real

## Autor

Marcos Willian de Souza preenchimento de campos, leitura de tela, espera por eventos e manipulação de teclas especiais.

A classe principal é `S3270Emulator`, que utiliza uma instância de `S3270Session` para comunicação com o terminal.

## Principais Funcionalidades

- Obtenção da tela em formato ASCII
- Busca de campos por label e envio de texto
- Movimentação do cursor
- Pressionamento de teclas especiais (Enter, PF, PA, Tab, etc.)
- Espera por eventos ou textos específicos na tela
- Extração de textos de posições ou campos específicos

## Exemplo de Uso

O exemplo a seguir demonstra como usar a biblioteca `terminus3270` para interagir com um terminal 3270:


