package remoteShell;

public class ResponseMessage extends Message {
    String errorMessage;

    ResponseMessage(MessageType t, String msg) {
        super(t);
        errorMessage = msg;
    }

    ResponseMessage(MessageType t) {
        super(t);
        errorMessage = "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
