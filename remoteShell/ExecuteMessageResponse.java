package remoteShell;

public class ExecuteMessageResponse extends ResponseMessage{
    String terminalResponse;
    
    public ExecuteMessageResponse(String rs) {
        super(MessageType.Execute);
        terminalResponse = rs;
    }

    public String getTerminalResponse() {
        return terminalResponse;
    }
}