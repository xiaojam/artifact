package test.takehome.transinnovate.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SftpServiceImplTest {

    @InjectMocks
    private SftpServiceImpl sftpService;

    @Mock
    private Session session;

    @Mock
    private ChannelSftp channelSftp;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sftpService, "sftpHost", "localhost");
        ReflectionTestUtils.setField(sftpService, "sftpPort", 2022);
        ReflectionTestUtils.setField(sftpService, "sftpUser", "orders");
        ReflectionTestUtils.setField(sftpService, "sftpPassword", "orders");
    }

    @Test
    void uploadFile_Success() throws Exception {
        try (MockedConstruction<JSch> jschMock = mockConstruction(JSch.class, (mock, context) -> {
            when(mock.getSession(anyString(), anyString(), anyInt())).thenReturn(session);
        })) {

            when(session.openChannel("sftp")).thenReturn(channelSftp);

            sftpService.uploadFile("test.json", "{}");

            verify(channelSftp, times(1)).put(any(InputStream.class), eq("test.json"));

            verify(channelSftp, times(1)).disconnect();
            verify(session, times(1)).disconnect();
        }
    }
}