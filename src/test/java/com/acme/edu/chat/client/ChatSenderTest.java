package com.acme.edu.chat.client;

import com.acme.edu.chat.SysoutCaptureAndAssertionAbility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import static java.lang.System.lineSeparator;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatSenderTest implements SysoutCaptureAndAssertionAbility {

    private ChatSender sender;
    @Mock
    private DataOutputStream outputStream;

    @Mock
    private BufferedReader consoleInput;

    @Before
    public void setUpSystemOut() throws IOException {
        resetOut();
        captureSysout();
    }

    @Before
    public void setUpSender() {
        sender = new ChatSender(outputStream, consoleInput);
    }

    @Test
    public void shouldTerminateOnExit() throws IOException {
        // Given
        when(consoleInput.readLine()).thenReturn("/exit");

        // When
        sender.run();

        // Then
        assertSysoutEquals("Terminated." + lineSeparator());
    }

    @Test
    public void shouldTerminateOnQuit() throws IOException {
        // Given
        when(consoleInput.readLine()).thenReturn("/quit");

        // When
        sender.run();

        // Then
        assertSysoutContains("Terminated.");
    }

    @Test
    public void shouldRejectLongMessages() throws IOException {
        // Given
        when(consoleInput.readLine()).thenReturn("/snd 1111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111111111111111111", "/quit");

        // When
        sender.run();

        // Then
        assertSysoutContains("Error: message should be shorter than 150 symbols.");
    }

    @Test
    public void shouldRejectFilesWhichDoesntStartWithSndOrHist() throws IOException {
        // Given
        when(consoleInput.readLine()).thenReturn("asdasd", "/exit");

        // When
        sender.run();

        // Then
        assertSysoutContains("Error: message should start with /snd or /hist");
    }
}