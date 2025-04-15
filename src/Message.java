import java.io.Serializable;


public class Message implements Serializable {
    public enum MessageType { CALCULATE, CHAT }

    private MessageType messageType = MessageType.CALCULATE;

    private Double operand1, operand2, operand3;
    private Operator operator1, operator2;


    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message(Double operand1, Operator operator1, Double operand2, Operator operator2, Double operand3) {
        this.operand1 = operand1;
        this.operator1 = operator1;
        this.operand2 = operand2;
        this.operator2 = operator2;
        this.operand3 = operand3;
    }

    public Double[] getOperands() {
        return new Double[] {operand1, operand2, operand3};
    }

    public Operator[] getOperators() {
        return new Operator[] {operator1, operator2};
    }

    public String toString() {
        return operand1 + " " + operator1 + " " + operand2 + " " + operator2 + " " + operand3;
    }
}