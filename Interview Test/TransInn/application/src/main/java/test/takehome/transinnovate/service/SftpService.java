package test.takehome.transinnovate.service;

public interface SftpService {
    void uploadFile(String fileName, String content) throws Exception;
}
