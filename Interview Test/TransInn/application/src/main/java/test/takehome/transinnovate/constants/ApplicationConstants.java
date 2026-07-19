package test.takehome.transinnovate.constants;

public class ApplicationConstants {

    public ApplicationConstants() throws Exception {
        throw new Exception("Utility");
    }

    public enum ChannelType {
        REST,
        SOAP,
        SFTP_REST,
        SFTP_SOAP,
        REST_FAILING,
    }
}
