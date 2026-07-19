package test.takehome.transinnovate.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.takehome.transinnovate.service.SftpService;

import java.io.ByteArrayInputStream;

@Service
@Slf4j
public class SftpServiceImpl implements SftpService {

    @Value("${xdock.sftp.host}")
    private String sftpHost;

    @Value("${xdock.sftp.port}")
    private int sftpPort;

    @Value("${xdock.sftp.user}")
    private String sftpUser;

    @Value("${xdock.sftp.password}")
    private String sftpPassword;

    @Override
    public void uploadFile(String fileName, String content) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);
        session.setPassword(sftpPassword);

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(5000);

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(5000);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes())) {
            channel.put(bais, fileName);
            log.info("File {} successfully uploaded to SFTP server", fileName);
        } finally {
            channel.disconnect();
            session.disconnect();
        }
    }

}
