import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Message implements Serializable {
    public enum MessageType { CALCULATE, CHAT }

    private MessageType messageType = MessageType.CALCULATE;

    private double operand1, operand2, operand3;
    private OperatorType operator1, operator2;


    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message(Double operand1, OperatorType operator1, Double operand2, OperatorType operator2, Double operand3) {
        this.operand1 = operand1;
        this.operator1 = operator1;
        this.operand2 = operand2;
        this.operator2 = operator2;
        this.operand3 = operand3;
    }

    /*
    public Double calculate() {
        double val;

        if (operator1 == OperatorType.MUL || operator1 == OperatorType.DIV || operator1 == OperatorType.MOD) {
            val = operate(operand1, operand2, operator1);
            val = operate(val, operand3, operator2);
        } else if (operator2 == OperatorType.MUL || operator2 == OperatorType.DIV || operator2 == OperatorType.MOD) {
            val = operate(operand2, operand3, operator2);
            val = operate(operand1, val, operator1);
        } else {
            val = operate(operand1, operand2, operator1);
            val = operate(val, operand3, operator2);
        }

        return val;
    }

    private double operate(double val1, double val2, OperatorType operator) {
        if (operator == OperatorType.ADD) {
            return val1 + val2;
        } else if (operator == OperatorType.SUB) {
            return val1 - val2;
        } else if (operator == OperatorType.MUL) {
            return val1 * val2;
        } else if (operator == OperatorType.DIV) {
            return val1 / val2;
        } else {
            return val1 % val2;
        }
    }
     */

    public Double[] getOperands() {
        return new Double[] {operand1, operand2, operand3};
    }

    public OperatorType[] getOperators() {
        return new OperatorType[] {operator1, operator2};
    }

    public String toString() {
        return operand1 + " " + operator1 + " " + operand2 + " " + operator2 + " " + operand3;
    }
}