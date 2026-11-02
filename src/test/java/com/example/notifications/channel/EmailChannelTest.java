package com.example.notifications.channel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EmailChannelTest {
 @Test void sendsValidEmail() throws Exception { EmailGateway gateway = mock(EmailGateway.class); new EmailChannel(gateway).send("a@b.com", "hello"); verify(gateway).sendEmail("a@b.com", "hello"); }
 @Test void rejectsInvalidEmailBeforeGateway() { EmailGateway gateway = mock(EmailGateway.class); assertThrows(IllegalArgumentException.class, () -> new EmailChannel(gateway).send("bad", "hello")); verifyNoInteractions(gateway); }
}
