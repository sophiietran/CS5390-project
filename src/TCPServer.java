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

            while (true) {
                // Read equation from client
                Message equation = (Message) inFromClient.readObject();
                if (equation.getMessageType() == Message.MessageType.DISCONNECT) {
                    System.out.println("Client has terminated connection.");
                    break;
                } else {
                    System.out.println("Received equation: " + equation);
                }

                // Solve equation and send solution to client
                String solution;
                try {
                    solution = calculate(equation.getOperands(), equation.getOperators()).toString();
                } catch (Exception e) {
                    solution = "Invalid equation";
                }
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

    public static Double calculate(Double[] operands, Character[] operators) throws Exception {
        if (!isValidOperation(operands, operators)) {
            throw new Exception("INVALID");
        }

        // TODO: maybe just catch and handle arithmetic exceptions here
        //try { Operator2.DIV.apply(5, 0); } catch (ArithmeticException e) { System.out.println(e.getMessage()); } // Test divide by zero error handling

        // Respect order of operations
        if (!operatorIsPriority(operators[0]) && operatorIsPriority(operators[1])) {
            double part = Operator.fromSymbol(operators[1]).apply(operands[1], operands[2]);
            return Operator.fromSymbol(operators[0]).apply(operands[0], part);
        } else {
            double part = Operator.fromSymbol(operators[0]).apply(operands[0], operands[1]);
            return Operator.fromSymbol(operators[1]).apply(part, operands[2]);
        }
    }

    // Check for divide by zero
    private static boolean isValidOperation(Double[] operands, Character[] operators) {
        if (operands[1] == 0.0 && (operators[0] == '/' || operators[0] == '%')) {
            return false;
        } else if (operands[2] == 0.0 && (operators[1] == '/' || operators[1] == '%')) {
            return false;
        } else {
            return true;
        }
    }

    // Order of operations check
    private static boolean operatorIsPriority(Character ot) {
        return ot == '*' || ot == '/' || ot == '%';
    }
}