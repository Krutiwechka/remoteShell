package remoteShell;

public class ExecuteMessage extends Message {
    String command;

    public ExecuteMessage(String cm) {
        super(MessageType.Execute);
        command = cm;
    }
}
