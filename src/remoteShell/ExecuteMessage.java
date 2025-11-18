package remoteShell;

public class ExecuteMessage extends Message {
    String command;

    ExecuteMessage(String cm) {
        super(MessageType.Execute);
        command = cm;
    }
}
