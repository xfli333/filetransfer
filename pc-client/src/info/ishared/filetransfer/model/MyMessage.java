package info.ishared.filetransfer.model;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-24
 * Time: PM3:37
 */
public class MyMessage {
    private String messageType;
    private Object data;

    public MyMessage(String messageType, Object data) {
        this.messageType = messageType;
        this.data = data;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
