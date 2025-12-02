package remoteShell;

public class ConnectMessageResponse extends ResponseMessage{
    private static final long serialVersionUID = 1L;

    public ConnectMessageResponse(String errorMessage)  {
        super(MessageType.Connect, errorMessage);
    }

    public ConnectMessageResponse()  {
        super(MessageType.Connect, "");
    }
}

