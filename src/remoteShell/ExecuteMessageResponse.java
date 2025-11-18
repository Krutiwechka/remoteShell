package remoteShell;

public class ExecuteMessageResponse extends ResponseMessage{
    String terminalResponse;
    
    ExecuteMessageResponse(String rs) {
        super(MessageType.Execute);
        terminalResponse = rs;
    }
}
