import java.io.*;
import java.net.*;


// TODO: make something to keep track of connections and their details. each client will also need their own socket.

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

            // First message client will send is to tell us their name. Echo it back to them as acknowledgment.
            Message hello = (Message) inFromClient.readObject();
            outToClient.writeBytes(hello.getClientName() + '\n');
            outToClient.flush();

            while (true) {
                // Read message from client
                Message equation = (Message) inFromClient.readObject();
                if (equation.getMessageType() == Message.MessageType.QUIT) {
                    System.out.println("Client has terminated connection.");
                    break;
                } else if (equation.getMessageType() == Message.MessageType.CALC) {
                    System.out.println("Received equation: " + equation);
                    // Solve equation and send solution to client
                    String solution;
                    if (isValidOperation(equation.operator1, equation.operand2, equation.operator2, equation.operand3))
                        solution = calculate(equation).toString();
                    else
                        solution = "Invalid equation";
                    System.out.println("Sending solution: " + solution + "\n");
                    outToClient.writeBytes(solution + '\n');
                    outToClient.flush();
                }
            }
        } catch (EOFException e) {
            // Socket closed normally
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }

    public static Double calculate(Message eq) {
        // TODO: try-catch and throw exception for invalid equation
        //try { Operator2.DIV.apply(5, 0); } catch (ArithmeticException e) { System.out.println(e.getMessage()); } // Test divide by zero error handling

        // Respect order of operations
        if (!operatorIsPriority(eq.operator1) && operatorIsPriority(eq.operator2)) {
            double part = Operator.fromSymbol(eq.operator2).operate(eq.operand2, eq.operand3);
            return Operator.fromSymbol(eq.operator1).operate(eq.operand1, part);
        } else {
            double part = Operator.fromSymbol(eq.operator1).operate(eq.operand1, eq.operand2);
            return Operator.fromSymbol(eq.operator2).operate(part, eq.operand3);
        }
    }

    // Check for divide by zero
    private static boolean isValidOperation(Character op1, Double v2, Character op2, Double v3) {
        if (v2 == 0.0 && (op1 == '/' || op1 == '%')) {
            return false;
        } else if (v3 == 0.0 && (op2 == '/' || op2 == '%')) {
            return false;
        } else {
            return true;
        }
    }

    // Order of operations check
    private static boolean operatorIsPriority(Character op) {
        return op == '*' || op == '/' || op == '%';
    }
}