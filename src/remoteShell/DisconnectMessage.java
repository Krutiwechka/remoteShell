package remoteShell;

public class DisconnectMessage extends Message {
    String username;

    public DisconnectMessage(String name) {
        super(MessageType.Connect);
        this.username = name;
    }
}
