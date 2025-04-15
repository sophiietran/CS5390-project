import java.io.*;
import java.net.*;

class TCPServer {
    public static final int SERVER_PORT = 9001;

    public static void main(String[] argv) throws Exception {
        // Await and accept connection
        ServerSocket welcomeSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server listening for connections on port " + welcomeSocket.getLocalPort());
        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("Connection established.\n");
        ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        // TODO: check if operation is valid
        for (int i = 0; i < TCPClient.TOTAL_EQUATIONS; i++) {
            // Read equation from client
            Message equation = (Message) inFromClient.readObject();
            System.out.println("Received equation: " + equation);

            // Solve equation and send solution to client
            String solution = calculate(equation.getOperands(), equation.getOperators()).toString();
            System.out.println("Sending solution: " + solution + "\n");
            outToClient.writeBytes(solution + '\n');
            outToClient.flush();
        }
    }

    public static Double calculate(Double[] operands, Operator[] operators) {
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

    private static double operate(double val1, double val2, Operator operator) {
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

        //double result = Operator2.ADD.apply(3, 5); // Apply an operation directly
        //try { Operator2.DIV.apply(5, 0); } catch (ArithmeticException e) { System.out.println(e.getMessage()); } // Test divide by zero error handling
        return Operator2.fromSymbol("*").apply(4, 6); // Convert a symbol to an operator and use it
    }

    private static boolean isValidOperation(Double[] operands, Operator[] operators) {
        if (operators[0] == Operator.DIV && operands[1] == 0 || operators[1] == Operator.DIV && operands[2] == 0) { // Divide by 0
            return false;
        } else if (operators[0] == Operator.MOD && operands[1] == 0 || operators[1] == Operator.MOD && operands[2] == 0) { // Mod by 0
            return false;
        } else {
            return true;
        }
    }

    private static boolean operatorIsPriority(Operator ot) {
        return ot == Operator.MUL || ot == Operator.DIV || ot == Operator.MOD;
    }
}