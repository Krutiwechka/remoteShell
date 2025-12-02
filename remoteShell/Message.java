package remoteShell;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageType type;

    protected Message(MessageType t) {
        this.type = t;
	}

    public MessageType getType() {
        return type;
    }
}

