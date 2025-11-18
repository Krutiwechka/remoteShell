package remoteShell;

public class ExecuteMessage extends Message {
    public String command;

    public ExecuteMessage(String cm) {
        super(MessageType.Execute);
        command = cm;
    }
}
