import java.io.Serializable;


public class Message implements Serializable {
    public enum MessageType { HELLO, CALC, CHAT, QUIT }

    private MessageType messageType = MessageType.CALC;

    private String clientName;

    public Double operand1, operand2, operand3;
    public Character operator1, operator2;


    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message(MessageType messageType, String clientName) {
        this.messageType = messageType;
        this.clientName = clientName;
    }

    public Message(Double operand1, Character operator1, Double operand2, Character operator2, Double operand3) {
        this.operand1 = operand1;
        this.operator1 = operator1;
        this.operand2 = operand2;
        this.operator2 = operator2;
        this.operand3 = operand3;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getClientName() {
        return clientName;
    }

    public String toString() {
        //return String.format("%.4f %c %.4f %c %.4f", operand1, operator1, operand2, operator2, operand3);
        return operand1 + " " + operator1 + " " + operand2 + " " + operator2 + " " + operand3;
    }

    public String toString2() {
        return operand1.toString() + operator1 + operand2 + operator2 + operand3;
    }
}