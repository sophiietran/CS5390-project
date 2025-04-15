import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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

    public static Double calculate(Double[] operands, OperatorType[] operators) {
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

    private static double operate(double val1, double val2, OperatorType operator) {
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

    private static boolean operatorIsPriority(OperatorType ot) {
        return ot == OperatorType.MUL || ot == OperatorType.DIV || ot == OperatorType.MOD;
    }
}