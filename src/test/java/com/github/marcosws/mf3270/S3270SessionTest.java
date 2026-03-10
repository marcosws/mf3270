package com.github.marcosws.mf3270;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.marcosws.mf3270.exceptions.S3270SessionException;


public class S3270SessionTest {

	private S3270Session session;
    private BufferedWriter mockWriter;
    private BufferedReader mockReader;

    @BeforeEach
    void setup() throws IOException {
        session = new S3270Session();

        // Mock Writer e Reader
        mockWriter = spy(new BufferedWriter(new StringWriter()));
        mockReader = spy(new BufferedReader(new StringReader("line1\nline2\nok\n")));

        // Injetando mocks na sessão
        session.getClass().getDeclaredFields(); // apenas para referência
        setPrivateField(session, "writer", mockWriter);
        setPrivateField(session, "reader", mockReader);
        setPrivateField(session, "process", mock(Process.class));
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendCommandReturnsOk() {
        String result = session.sendCommand("DummyCommand");
        assertTrue(result.contains("ok"));
    }

    @Test
    void testAsciiScreen() throws IOException {

        S3270Session session = spy(new S3270Session());

        // mock do waitFor
        doReturn("ok").when(session).waitFor(any());

        // mock do sendCommand
        doReturn("data: screen line 1\ndata: screen line 2\nok")
            .when(session)
            .sendCommand("Ascii()");

        String result = session.asciiScreen();

        assertNotNull(result);
        assertTrue(result.contains("screen line 1"));
        
    }

    @Test
    void testGetCursorPosition() throws IOException {
        // Preparando reader simulado para cursor
        StringReader sr = new StringReader("row 5 col 10\nok\n");
        BufferedReader reader = new BufferedReader(sr);
        setPrivateField(session, "reader", reader);

        Optional<CursorPosition> posOpt = session.getCursorPosition();
        assertTrue(posOpt.isPresent());
        CursorPosition pos = posOpt.get();
        assertEquals(5, pos.getRow());
        assertEquals(10, pos.getCol());
    }

    @Test
    void testTimeout() {
        BufferedReader slowReader = mock(BufferedReader.class);
        try {
            when(slowReader.readLine()).thenAnswer(invocation -> {
                Thread.sleep(2000); // simula delay maior que timeout
                return "ok";
            });
        } catch (Exception ignored) {}
        setPrivateField(session, "reader", slowReader);

        S3270SessionException ex = assertThrows(S3270SessionException.class, () -> {
            session.sendCommand("AnyCommand", 500); // timeout de 500ms
        });
        assertTrue(ex.getMessage().contains("Timeout"));
    }

    @Test
    void testFindField() {
        String screen = "Username: \nPassword: \n";
        Optional<CursorPosition> posOpt = session.findField(screen, "Username:");
        assertTrue(posOpt.isPresent());
        assertEquals(1, posOpt.get().getRow()); // logo após "Username:"
        assertEquals(10, posOpt.get().getCol()); // logo após os dois pontos
    }

    @Test
    void testSendTextByLabel() throws IOException {
    	
        // Cria um spy da sessão
        S3270Session session = spy(new S3270Session());

        // Mock dos streams e processo
        BufferedReader reader = mock(BufferedReader.class);
        BufferedWriter writer = mock(BufferedWriter.class);
        Process process = mock(Process.class);

        // Injeta os campos privados simulando conexão ativa
        setPrivateField(session, "reader", reader);
        setPrivateField(session, "writer", writer);
        setPrivateField(session, "process", process);

        // Mock da tela para findField
        String screen = "Username: ______\nPassword: ______\nok";
        doReturn(screen).when(session).asciiScreen(); // ou getScreen(), se usar

        // Mock de comandos para não executar código real
        doReturn("ok").when(session).moveCursor(anyInt(), anyInt());
        doReturn("ok").when(session).sendCommand(startsWith("String("));

        // Executa o método
        String result = session.sendTextByField("Username:", "admin");

        // Verificações
        assertNotNull(result);
        verify(session).moveCursor(0, 9); // posição do campo depois do label
        verify(session).sendCommand("String(\"admin\")");
        
    }
	
}
