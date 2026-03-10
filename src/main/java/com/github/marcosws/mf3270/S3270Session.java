package com.github.marcosws.mf3270;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.marcosws.mf3270.enums.PAKey;
import com.github.marcosws.mf3270.enums.PFKey;
import com.github.marcosws.mf3270.enums.WaitType;
import com.github.marcosws.mf3270.exceptions.S3270SessionException;

/**
 * S3270Session.java
 * 
 * Session class for managing connections and operations with the s3270.
 * This class is an initial framework and can be expanded with specific methods
 * for sending commands, reading screens, etc.
 * 
 * Classe de sessão para gerenciar conexões e operações com o s3270.
 * Esta classe é uma estrutura inicial e pode ser expandida com métodos específicos
 * para enviar comandos, ler telas, etc.
 * 
 * @version 1.0
 * @since 2026-03
 * @Author: Marcos Willian de Souza
 * 
 * Repository: www.github.com/marcosws
 * References: 
 * - https://x3270.bgp.nu/Unix/s3270-man.html#Actions
 * - https://x3270.bgp.nu/guide/scripting.html
 * - https://x3270.bgp.nu/guide/scripting.html#scripting-commands
 * - https://x3270.miraheze.org/wiki/Main_Page
 * - https://github.com/pmattes/x3270
 * - https://x3270.miraheze.org/wiki/Downloads
 */
public class S3270Session implements AutoCloseable {
	
	private Process process;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	private ExecutorService executor;

	public S3270Session() {
        // SingleThreadExecutor por instância, isolado para testes
        executor = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * English: Checks if the session is connected. Throws an exception if the reader or writer streams are not initialized, indicating that the session is not connected.
	 * Português: Verifica se a sessão está conectada. Lança uma exceção se os streams de leitura ou escrita não estiverem inicializados, indicando que a sessão não está conectada.
	 * @throws S3270SessionException se a sessão não estiver conectada
	 */
	private void checkSession() {
	    if (writer == null || reader == null || process == null) {
	        throw new S3270SessionException(
	            "Session not initialized. Call connect(host, port) first."
	        );
	    }
	}
		
	/**
	 * English: Closes the process resources and streams. 
	 * Throws an exception if an I/O error occurs while closing the resources.
	 * Português: Fecha os recursos do processo e streams. 
	 * e depois fecha os recursos do processo e streams. Lança uma exceção se ocorrer um erro de I/O ao fechar os recursos.
	 * @throws S3270SessionException Português: se ocorrer um erro de I/O ao fechar os recursos, English: if an I/O error occurs while closing the resources
	 */
    @Override
	public void close()  {
    	
		try {
			if (writer != null) 
				writer.close();
			if (reader != null) 
				reader.close();
			if (process != null && process.isAlive()) {
				process.destroy();
				try {
					process.waitFor(2, TimeUnit.SECONDS);
				}
				catch (InterruptedException e) {
					process.destroyForcibly();
					e.printStackTrace();
				}
			}
		} 
		catch (IOException e) {
			throw new S3270SessionException("Error closing session", e);
		}
	    finally {
	        // Fecha o executor para evitar vazamento de threads
	        executor.shutdownNow();
	    }
	}
	
	/**
	 * English: Sends a command to s3270 and waits for the response. Returns the complete response until "ok" or throws an exception if "error" occurs.
	 * Português: Envia um comando para o s3270 e aguarda a resposta. Retorna a resposta completa até o "ok" ou lança exceção se ocorrer "error".
	 * @param command O comando a ser enviado (ex: "Ascii()", "MoveCursor(5,10)", etc.)
	 * @return A resposta do s3270 para o comando enviado
	 * @throws RuntimeException Português: se o s3270 retornar "error" ou se a stream for fechada inesperadamente, English: if s3270 returns "error" or if the stream is closed unexpectedly
	 * 
	 */
	public String sendCommand(String command) {
		
		checkSession();
		
        try {
            writer.write(command);
            writer.newLine();
            writer.flush();
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
                if ("ok".equals(line)) return response.toString();
                if ("error".equals(line)) {
                    throw new S3270SessionException(
                        "S3270 returned error for command: " + command + "\nResponse:\n" + response
                    );
                }
            }
            throw new S3270SessionException("S3270 stream closed unexpectedly.");
        } 
        catch (IOException e) {
            throw new S3270SessionException("Error sending command: " + command, e);
        }
		
	}
	
	/**
	 * Envia um comando para o s3270 e aguarda a resposta com timeout. Retorna a resposta completa até o "ok" ou lança exceção se ocorrer "error" ou se o timeout for atingido.
	 * @param command O comando a ser enviado (ex: "Ascii()", "MoveCursor(5,10)", etc.)
	 * @param timeoutMillis O tempo máximo em milissegundos para aguardar a resposta
	 * @return A resposta do s3270 para o comando enviado
	 * @throws RuntimeException se o s3270 retornar "error", se a stream for fechada inesperadamente ou se o timeout for atingido
	 */
	public String sendCommand(String command, int timeoutMillis) {
		
	    checkSession();

	    Future<String> future = executor.submit(() -> {
	        try {
	            writer.write(command);
	            writer.newLine();
	            writer.flush();
	            StringBuilder response = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                response.append(line).append("\n");
	                if ("ok".equals(line)) return response.toString();
	                if ("error".equals(line)) {
	                    throw new S3270SessionException(
	                        "S3270 returned error for command: " + command + "\nResponse:\n" + response
	                    );
	                }
	            }
	            throw new S3270SessionException("S3270 stream closed unexpectedly.");
	        } 
	        catch (IOException e) {
	            throw new S3270SessionException("Error sending command: " + command, e);
	        }
	    });

	    try {
	        return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
	    } 
	    catch (TimeoutException e) {
	        future.cancel(true);
	        throw new S3270SessionException("Timeout reached for command: " + command);
	    } 
	    catch (ExecutionException e) {
	        throw new S3270SessionException("Execution exception for command: " + command, e.getCause());
	    } 
	    catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new S3270SessionException("Interrupted while sending command: " + command, e);
	    } 
	}
	
	/**
	 * Tenta enviar um comando para o s3270 e aguarda a resposta. Retorna um Optional contendo a resposta completa até o "ok" se bem-sucedido, ou Optional.empty() se ocorrer um erro.
	 * O método captura qualquer exceção lançada durante o envio do comando e leitura da resposta, retornando Optional.empty() em caso de falha, ao invés de lançar a exceção.
	 * Isso permite que o chamador tente enviar um comando sem se preocupar com tratamento de exceções, e verifique se a operação foi bem-sucedida verificando se o Optional contém um valor.
	 * Exemplo de uso: 
	 * 				Optional<String> responseOpt = session.trySendCommand("Ascii()"); 
	 * 				if (responseOpt.isPresent()) { 
	 * 					String response = responseOpt.get(); // processar resposta 
	 * 				} else { // comando falhou }
	 * 	
	 * @param command
	 * @return Um Optional contendo a resposta do s3270 para o comando enviado se bem-sucedido, ou Optional.empty() se ocorrer um erro
	 */
	public Optional<String> trySendCommand(String command) {
	    try {
	        return Optional.of(sendCommand(command));
	    } catch (Exception e) {
	        return Optional.empty();
	    }
	}
	
	/**
	 * English: Gets the current screen from the host in ASCII format, removing control lines.
	 * Sends the "Ascii()" command and reads the response until it finds "ok". Returns the screen as a string, removing lines that start with "data:" and the last 2 control lines.
	 * The method tries to get the screen up to 10 times, waiting 150ms between each attempt, to handle cases where the screen may not be immediately available or may contain only control lines.
	 * 
	 * Português: Obtém a tela atual do host em formato ASCII, removendo as linhas de controle. 
	 * Envia o comando "Ascii()" e lê a resposta até encontrar "ok". 
	 * Retorna a tela como string, removendo as linhas que começam com "data:" e as últimas 2 linhas de controle.
	 * O método tenta obter a tela até 10 vezes, esperando 150ms entre cada tentativa, para lidar com casos onde a tela pode não estar imediatamente disponível ou pode conter apenas linhas de controle.
	 * 
	 * @return Português: Screen atual do host em formato ASCII, sem linhas de controle, English: Current screen from the host in ASCII format, without control lines
	 */
	public String asciiScreen() {
		
		int retries = 10; // número de tentativas
		String screen = "";
		String rawScreen = "";

		while (retries-- > 0) {
		    rawScreen = sendCommand("Ascii()"); // pega a tela atual

		    // divide por linhas, remove linhas em branco e as 2 últimas
		    String[] lines = rawScreen.split("\n");
		    StringBuilder sb = new StringBuilder();

		    int limit = Math.max(0, lines.length - 2); // ignora últimas 2 linhas
		    for (int i = 0; i < limit; i++) {
		        String line = lines[i].trim();
		        if (!line.isBlank() && !line.equals("data:")) { // ignora "data:" ou linha vazia
		            sb.append(line).append("\n");
		        }
		    }

		    screen = sb.toString().trim();

		    if (!screen.isBlank()) { // se sobrou algum conteúdo, considera carregado
		        break;
		    }

		    sleep(150); // espera 500ms antes de tentar de novo
		}
	    return rawScreen;
		
	}
	
	/**
	 * Obtém a tela atual do host em formato ASCII, removendo as linhas de controle. 
	 * Envia o comando "Ascii()" e lê a resposta até encontrar "ok". 
	 * Retorna a tela como string, removendo as linhas que começam com "data:" e as últimas 2 linhas de controle.
	 * @throws RuntimeException se ocorrer um erro de I/O ao enviar o comando ou ler a resposta
	 * @return Screen atual do host em formato ASCII, sem linhas de controle
	 */
	public String getScreen() {
		
		StringBuilder screen = new StringBuilder();
		String rawScreen = sendCommand("Ascii()"); // pega a tela atual

	   int limit = Math.max(0, rawScreen.split("\n").length - 2); // ignora últimas 2 linhas
	    for (int i = 0; i < limit; i++) {
	        String line = rawScreen.split("\n")[i].trim();
	        screen.append(line.replace("data:", "")).append("\n");

	    }
	    return screen.toString();
		
	}
	    
	// Captura posição do cursor
	/**
	 * Obtém a posição atual do cursor. Envia o comando "Query(Cursor)" e lê a resposta até encontrar "ok". Extrai os números da resposta para determinar a linha e coluna do cursor.
	 * @throws RuntimeException se ocorrer um erro de I/O ao enviar o comando ou ler a resposta
	 * @return Um array de inteiros onde o primeiro elemento é a linha e o segundo é a coluna do cursor (baseado em 1)
	 */
	public Optional<CursorPosition> getCursorPosition() {

	    checkSession();

	    try {

	        writer.write("Query(Cursor)");
	        writer.newLine();
	        writer.flush();

	        String line;

	        while ((line = reader.readLine()) != null) {

	            if ("ok".equals(line)) {
	                break;
	            }

	            String cleaned = line.replaceAll("[^0-9 ]", "").trim();

	            if (!cleaned.isEmpty()) {

	                String[] parts = cleaned.split("\\s+");

	                if (parts.length >= 2) {

	                    int row = Integer.parseInt(parts[0]);
	                    int col = Integer.parseInt(parts[1]);

	                    return Optional.of(new CursorPosition(row, col));
	                }
	            }
	        }

	        return Optional.empty();

	    } catch (IOException e) {
	        throw new S3270SessionException("Error getting cursor position", e);
	    }
	}
	
	/**
	 * Encontra a posição de um campo na tela com base em um rótulo (label). Divide a tela em linhas, procura o rótulo em cada linha e retorna a posição do campo (linha e coluna) se encontrado.
	 * A posição retornada é baseada em 1, ou seja, a primeira linha e coluna são consideradas como 1. A coluna é calculada como a posição do rótulo mais o comprimento do rótulo mais 1 (para o espaço entre o rótulo e o campo).
	 * Lança uma exceção se o rótulo não for encontrado na tela.
	 * Exemplo: se a tela contém "Password  ===>" na linha 10, a posição retornada será (10, 20) considerando que "Password  ===>" tem 19 caracteres e o campo começa na coluna 20.
	 * Uso: Optional<CursorPosition> posOpt = findField(screen, "Password  ===>"); if (posOpt.isPresent()) { CursorPosition pos = posOpt.get(); // usar pos.getRow() e pos.getCol() } else { // campo não encontrado }
	 * @param screen A tela atual do host em formato ASCII
	 * @param field O texto do rótulo que identifica o campo de entrada
	 * @return Um Optional contendo a posição do campo como CursorPosition se encontrado, ou Optional.empty() se não encontrado
	 */
	public Optional<CursorPosition> findField(String screen, String field) {

	    String[] lines = screen.split("\n");

	    for (int i = 0; i < lines.length; i++) {
	        int col = lines[i].indexOf(field);

	        if (col >= 0) {
	            int row = i + 1;
	            int column = col + field.length() + 1;
	            return Optional.of(new CursorPosition(row, column));
	        }
	    }

	    return Optional.empty();
	}
	
	/**
	 * Envia texto para um campo identificado por um rótulo (label). Primeiro obtém a tela atual, encontra a posição do campo usando o label, move o cursor para essa posição e envia o texto.
	 * @param field O texto do rótulo que identifica o campo de entrada
	 * @param text O texto a ser enviado para o campo
	 * @return A resposta do s3270 para os comandos enviados ou uma mensagem de erro se o label não for encontrado
	 * @throws IOException se ocorrer um erro de I/O ao enviar os comandos ou ler as respostas
	 */    
	public String sendTextByField(String field, String text) {

	    String screen = asciiScreen().replace("data:", "");

	    Optional<CursorPosition> posOpt = findField(screen, field);

	    if (posOpt.isEmpty()) {
	        throw new S3270SessionException(
	            "Field '" + field + "' not found in screen."
	        );
	    }

	    CursorPosition pos = posOpt.get();

	    int row = pos.getRow() - 1;
	    int col = pos.getCol() - 1;

	    return moveAndSendString(row, col, text);
	}
	
	/**
	 * Envia texto para um campo identificado por um rótulo (label) com offsets. Primeiro obtém a tela atual, encontra a posição do campo usando o label, move o cursor para essa posição ajustada pelos offsets e envia o texto.
	 * @param label O texto do rótulo que identifica o campo de entrada
	 * @param text O texto a ser enviado para o campo
	 * @param offsetRow O número de linhas para ajustar a posição do cursor (pode ser positivo ou negativo)
	 * @param offsetCol O número de colunas para ajustar a posição do cursor (pode ser positivo ou negativo)
	 * @return A resposta do s3270 para os comandos enviados ou uma mensagem de erro se o label não for encontrado
	 * @throws IOException se ocorrer um erro de I/O ao enviar os comandos ou ler as respostas
	 */
	public String sendTextByField(String field, String text, int offsetRow, int offsetCol) {

		String screen = asciiScreen().replaceAll("data:", "");
		
		Optional<CursorPosition> posOpt = findField(screen, field);
		
	    if (posOpt.isEmpty()) {
	        throw new S3270SessionException(
	            "Field '" + field + "' not found in screen."
	        );
	    }
	    
	    CursorPosition pos = posOpt.get();
		
		int targetRow = (offsetRow >= 0 ? Math.abs(pos.getRow() - 1) + Math.abs(offsetRow) : Math.abs(pos.getRow() - 1) - Math.abs(offsetRow));
		int targetCol = (offsetCol >= 0 ? Math.abs(pos.getCol() - 1) + Math.abs(offsetCol) : Math.abs(pos.getCol() - 1) - (field.length() + Math.abs(offsetCol)));

		return moveAndSendString(targetRow, targetCol, text);
		
	}
	
	/**
	 * Move o cursor para a posição especificada (linha, coluna) e envia o texto. Retorna a resposta do s3270 para os comandos enviados.
	 * @param row A linha para mover o cursor (baseado em 1)
	 * @param col A coluna para mover o cursor (baseado em 1)
	 * @param text O texto a ser enviado para a posição do cursor
	 * @return A resposta do s3270 para os comandos enviados
	 * @throws IOException se ocorrer um erro de I/O ao enviar os comandos ou ler as respostas
	 */
	public String moveAndSendString(int row, int col, String text) {
		
		StringBuilder returnCommand = new StringBuilder();
		returnCommand.append("\n");
		returnCommand.append(moveCursor(row, col)).append("\n");
		returnCommand.append(sendString(text));
		return returnCommand.toString();
		
	}
	
	/**
	 * Move o cursor para a posição especificada (linha, coluna). Retorna a resposta do s3270 para o comando de movimento do cursor.
	 * @param row A linha para mover o cursor (baseado em 1)
	 * @param col A coluna para mover o cursor (baseado em 1)
	 * @return A resposta do s3270 para o comando de movimento do cursor
	 * @throws IOException se ocorrer um erro de I/O ao enviar o comando ou ler a resposta
	 */
	public String connect(String host, String port, boolean useX3270) {
		
		ProcessBuilder processBuilder = (useX3270 ? 
				new ProcessBuilder("x3270","-script", host + ":" + port) :
				new ProcessBuilder("s3270"));
		
		
		processBuilder.redirectErrorStream(true);
		try {
			process = processBuilder.start();
		} 
		catch (IOException e) {
			throw new S3270SessionException("Error starting s3270 process", e);
		}
		writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		return (useX3270 ? "connection via (w)x3270\nok" : sendCommand("connect(" + host + ":" + port + ")"));
	}
	
	/**
	 * English: Connects to the host using the "connect(host:port)" command. Starts the s3270 process, sets up the input and output streams, and sends the connection command. The host should be the IP address or server name, and the port should be the 3270 connection port of the host.
	 * @param host
	 * @param port
	 * @return A resposta do s3270 para o comando de conexão
	 * @throws IOException
	 */
	public String connect(String host, String port) {
		return connect(host, port, false);
	}
	
	
	/**
	 * Desconecta do host enviando o comando "Disconnect". Retorna a resposta do s3270 para o comando de desconexão.
	 * @return A resposta do s3270 para o comando de desconexão
	 */
	public String disconnect() {
		return sendCommand("Disconnect");
	}

	/**
	 * Verifica se a conexão com o host está ativa. Envia o comando "Query(ConnectionState)" e lê a resposta até encontrar "ok". Verifica se a resposta contém "connected-3270" para determinar se está conectado.
	 * @return true se estiver conectado, false caso contrário
	 * @throws IOException se ocorrer um erro de I/O ao enviar o comando ou ler a resposta
	 */
	public boolean isConnected() {
				
		checkSession();
		
		try {
						
			writer.write("Query(ConnectionState)");
			writer.newLine();
			writer.flush();
							
			StringBuilder screen = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("ok")) break;
					screen.append(line).append("\n");
			}
			return screen.toString().contains("connected-3270");
		}
		catch (IOException e) {
			throw new S3270SessionException("Error checking connection state", e);
		}
	}
	
	/**
	 * Envia o comando "Quit" para o s3270 para encerrar a sessão, e depois fecha os recursos do processo e streams. Retorna a resposta do s3270 para o comando "Quit".
	 * @return A resposta do s3270 para o comando "Quit"
	 */
	public String quit() {
		String returnCommand = sendCommand("Quit");
		close();
		return returnCommand;
	}
	
	/**
	 * Wait(WaitType)
	 * Aguarda um evento específico definido por WaitType (Unlock, InputField, Output, T3270Mode, NVTMode, Disconnect)
	 * Exemplo: waitFor(WaitType.Unlock) para aguardar o teclado ser liberado
	 * @param waitType O tipo de espera a ser realizada
	 * @return A resposta do s3270 para o comando de espera
	 * @throws IOException 
	 */
	public String waitFor(WaitType waitType) {
		return sendCommand("Wait(" + waitType.getValue() + ")", 30000); // espera até 30 segundos por padrão
	}
	
	/**
	 * Wait(WaitType, seconds)
	 * Aguarda um evento específico definido por WaitType (Unlock, InputField, Output, T3270Mode, NVTMode, Disconnect) por um número máximo de segundos
	 * Exemplo: waitFor(WaitType.Unlock, 10) para aguardar o teclado ser liberado por até 10 segundos
	 * @param waitType O tipo de espera a ser realizada
	 * @param seconds O tempo máximo em segundos para aguardar o evento
	 * @return A resposta do s3270 para o comando de espera
	 * @throws IOException 
	 */
	public String waitFor(WaitType waitType, int seconds) {
		return sendCommand("Wait(" + seconds + "," + waitType.getValue() + ")");
	}
	
	/**
	 * Wait(WaitType, seconds)
	 * Aguarda um evento específico definido por WaitType (Unlock, InputField, Output, T3270Mode, NVTMode, Disconnect) por um número máximo de segundos
	 * Exemplo: waitFor(WaitType.Unlock, 10) para aguardar o teclado ser liberado por até 10 segundos
	 * @param waitType O tipo de espera a ser realizada
	 * @param seconds O tempo máximo em segundos para aguardar o evento
	 * @return A resposta do s3270 para o comando de espera
	 * @throws IOException 
	 */
	public String waitFor(WaitType waitType, long seconds) {
		return sendCommand("Wait(" + seconds + "," + waitType.getValue() + ")");
	}
	
	/**
	 * Wait(seconds)
	 * Aguarda um número específico de segundos
	 * Exemplo: waitSeconds(5) para aguardar 5 segundos
	 * @param seconds O número de segundos para aguardar
	 * @return A resposta do s3270 para o comando de espera
	 * @throws IOException 
	 */
	public String waitSeconds(long seconds) {
		return sendCommand("Wait(" + seconds + ",Seconds)");
	}
	
	/**
	 * Wait(seconds)
	 * Aguarda um número específico de segundos
	 * Exemplo: waitSeconds(5) para aguardar 5 segundos
	 * @param seconds O número de segundos para aguardar
	 * @return A resposta do s3270 para o comando de espera
	 * @throws IOException 
	 */
	public String waitSeconds(int seconds) {
		return sendCommand("Wait(" + seconds + ",Seconds)");
	}
	
	/**
	 * WaitForText(text, timeoutMillis)
	 * Aguarda até que um texto específico apareça na tela ou até que o tempo limite seja atingido
	 * Exemplo: waitForText("Login successful", 10000) para aguardar até 10 segundos por "Login successful" na tela
	 * @param text O texto a ser aguardado na tela
	 * @param timeoutMillis O tempo máximo em milissegundos para aguardar o texto
	 * @throws S3270SessionException se o texto não aparecer na tela dentro do tempo limite
	 */
	public void waitForText(String text, int timeoutMillis) {

	    long start = System.currentTimeMillis();

	    while (System.currentTimeMillis() - start < timeoutMillis) {

	        if (getScreen().contains(text)) {
	            return;
	        }

	        sleep(200);
	    }

	    throw new S3270SessionException("Timeout waiting for text: " + text);
	}
		
	/**
	 * MoveCursor(row, col)
	 * Move o cursor para a posição especificada (linha, coluna)
	 * @param row A linha para mover o cursor (baseado em 1)
	 * @param col A coluna para mover o cursor (baseado em 1)
	 * @return A resposta do s3270 para o comando de movimento do cursor
	 */
	public String moveCursor(int row, int col) {
		return sendCommand("MoveCursor(" + row + "," + col + ")");
	}
	
	/**
	 * MoveCursor(position)
	 * Move o cursor para a posição especificada por um objeto CursorPosition
	 * @param position A posição do cursor encapsulada em um objeto CursorPosition
	 * @return A resposta do s3270 para o comando de movimento do cursor
	 */
	public String moveCursor(CursorPosition position) {
		return sendCommand("MoveCursor(" + position.getRow() + "," + position.getCol() + ")");
	}
		
	/**
	* Tab()
	* Move para o próximo campo editável
	* @throws IOException 
	*/
	public String tab() {
		return sendCommand("Tab()");
	}
		
	/**
	* Home()
	* Move o cursor para a primeira posição da tela
	* @throws IOException 
	*/
	public String home() {
		return sendCommand("Home()");
	}
		
	/**
	* String("texto")
	* Digita texto na posição atual do cursor
	* @param text
	* @throws IOException 
	*/
	public String sendString(String text) {
		return sendCommand("String(\"" + text + "\")");
	}
	
	/**
	 * DeleteField
	 * Apaga o conteúdo do campo atual
	 * @throws IOException 
	 */
	public String deleteField() {
		return sendCommand("DeleteField");
	}
		
	/**
	* EraseEOF
	* Apaga tudo da posição atual até o final da linha
	* @throws IOException 
	*/
	public String eraseEOF() {
		return sendCommand("EraseEOF");
	}
	
	/**
	 * EraseInput
	 * Apaga tudo da posição atual até o final do campo
	 * @return
	 */
	public String eraseInput() {
		return sendCommand("EraseInput");
	}
	
	/**
	 * Reset
	 * Reseta a tela, apagando tudo e movendo o cursor para a posição inicial
	 * @throws IOException 
	 */
	public String reset() {
		return sendCommand("Reset");
	}
		
	/**
	 *  Enter
	 *  Pressiona a tecla Enter
	 *  @throws IOException 
	 */
	public String enter() {
		return sendCommand("Enter");
	}
		
	/**
	*  PF(n)
	*  Pressiona a tecla PF n (PF1, PF2, PF3...)
	*  Exemplo: pressPF(PFKey.PF1) para pressionar a tecla PF1
	*  @param pfNumber
	*  @throws IOException 
	*/
	public String pressPF(PFKey pfKey) {
		return sendCommand("PF(" + pfKey.getValue() + ")");
	}
		
	/**
	 * PA(n)
	 * Pressiona PA n (Attention)
	 * Exemplo: pressPA(PAKey.PA1) para pressionar a tecla PA1
	 * @param paNumber
	 * @throws IOException 
	 */
	public String pressPA(PAKey paKey) {
		return sendCommand("PA(" + paKey.getValue() + ")");
	}
		
	/**
	 * Clear()
	 * Limpa toda a tela
	 * @throws IOException 
	 */
	public String clear() {
		return sendCommand("Clear()");
	}
		
	/**
	* Backspace()
	* Apaga caractere à esquerda do cursor
	*/
	public String backspace() {
		return sendCommand("Backspace()");
	}
		
	/**
	 * Sleep
	 * @param millis
	 */
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} 
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

}
