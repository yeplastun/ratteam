package com.acme.edu.chat.client;

import com.acme.edu.chat.SysoutCaptureAndAssertionAbility;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatReceiverTest implements SysoutCaptureAndAssertionAbility {
    private static final String message = "test message";

    private ChatReceiver receiver;

    @Mock
    private DataInputStream inputStream;

    @Mock
    private DataOutputStream outputStream;

    @Test @Ignore
    public void shouldPrintInputData() throws IOException {
        // Given
        when(inputStream.readUTF()).thenReturn(message);

        // When
        receiver = new ChatReceiver(inputStream, outputStream);

        // Then
        verify(outputStream).writeUTF(message);
    }

    @Test @Ignore
    public void shouldPrintExceptionMessageToConsole() throws IOException {
        // Given
        when(inputStream.readUTF()).thenThrow(new IOException());

        // When
        receiver = new ChatReceiver(inputStream, outputStream);

        // Then
        assertSysoutContains("No more data from server");
    }
}