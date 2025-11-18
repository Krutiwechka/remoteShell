package remoteShell;

public class ConnectMessage extends Message {
    private static final long serialVersionUID = 1L;
    public String username;
    public ConnectMessage(String name) {
        super(MessageType.Connect);
        this.username = name;
    }
}