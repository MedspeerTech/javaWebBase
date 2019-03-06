package medspeer.tech.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import medspeer.tech.constants.MessageType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExceptionResource {

    private String message;
    private int status;
    private MessageType type;

    public ExceptionResource() {
    }

    public ExceptionResource(MessageType type, String message) {
        this.message = message;
        this.type = type;
    }

    public ExceptionResource(int status, MessageType type, String message) {
		
    	this.message = message;
        this.type = type;
        this.status = status;
	}

	public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
