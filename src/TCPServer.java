import java.io.*;
import java.net.*;


class TCPServer {
    public static final int SERVER_PORT = 9001;


    public static void main(String[] argv) throws Exception {
        // Await and accept connection
        try (ServerSocket welcomeSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server listening for connections on port " + welcomeSocket.getLocalPort());
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Connection established.\n");
            ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // TODO: check if operation is valid
            while (true) {
                // Read equation from client
                Message equation = (Message) inFromClient.readObject();
                if (equation.getMessageType() == Message.MessageType.DISCONNECT) {
                    System.out.println("Client has terminated connection.");
                }
                System.out.println("Received equation: " + equation);
                //System.out.println(equation.operator1.getClass().getName());

                // Solve equation and send solution to client
                String solution = calculate(equation.getOperands(), equation.getOperators()).toString();
                System.out.println("Sending solution: " + solution + "\n");
                outToClient.writeBytes(solution + '\n');
                outToClient.flush();
            }
        } catch (EOFException e) {
            // Socket closed normally
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }

    public static Double calculate(Double[] operands, Character[] operators) {
        double solution;

        if (operatorIsPriority(operators[0])) {
            solution = operate(operands[0], operands[1], operators[0]);
            solution = operate(solution, operands[2], operators[1]);
        } else if (operatorIsPriority(operators[1])) {
            solution = operate(operands[1], operands[2], operators[1]);
            solution = operate(operands[0], solution, operators[0]);
        } else {
            solution = operate(operands[0], operands[1], operators[0]);
            solution = operate(solution, operands[2], operators[1]);
        }

        return solution;
    }

    private static Double operate(Double operand1, Double operand2, Character operator) {
        /*if (operator == Operator.ADD) {
            return val1 + val2;
        } else if (operator == Operator.SUB) {
            return val1 - val2;
        } else if (operator == Operator.MUL) {
            return val1 * val2;
        } else if (operator == Operator.DIV) {
            return val1 / val2;
        } else {
            return val1 % val2;
        }*/

        //Double result = Operator2.ADD.apply(3, 5); // Apply an operation directly
        //try { Operator2.DIV.apply(5, 0); } catch (ArithmeticException e) { System.out.println(e.getMessage()); } // Test divide by zero error handling

        return Operator.fromSymbol(operator).apply(operand1, operand2); // Convert a symbol to an operator and use it
    }

    private static boolean isValidOperation(Double[] operands, Character[] operators) {
        // TODO: check if divide by 0, mod by 0
        return true;
    }

    private static boolean operatorIsPriority(Character ot) {
        return ot == '*' || ot == '/' || ot == '%';
    }
}