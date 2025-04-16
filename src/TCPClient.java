import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class TCPClient {
    static char[] operators = {'+', '-', '*', '/'};

    public static void main(String[] argv) throws Exception {
        List<Message> equations = new ArrayList<>();
        equations.add(new Message(1.0, '+', 2.0, '+', 3.0));
        equations.add(new Message(1.0, '*', 2.0, '+', 3.0));
        equations.add(new Message(1.0, '+', 2.0, '*', 3.0));
        equations.add(new Message(1.0, '/', 2.0, '+', 3.0));
        equations.add(new Message(1.0, '+', 2.0, '/', 3.0));
        equations.add(new Message(4.0, '*', 2.0, '*', 3.0));
        equations.add(new Message(4.0, '+', 2.0, '/', 0.0));

        Random random = new Random(0);

        // Connect to server and send equation messages
        try (Socket clientSocket = new Socket("127.0.0.1", TCPServer.SERVER_PORT)) {
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client connected to server using socket " + clientSocket.getLocalPort() + "\n");

            //for (Message equation : equations) {
            for (int i = 0; i < 10; i++) {
                Message equation = new Message(
                        random.nextDouble() * 20 - 10,
                        operators[random.nextInt(operators.length)],
                        random.nextDouble() * 20 - 10,
                        operators[random.nextInt(operators.length)],
                        random.nextDouble() * 20 - 10
                );

                // Send equation
                System.out.println("Sending equation:  " + equation);
                outToServer.writeObject(equation);
                // Receive solution
                String solution = inFromServer.readLine();
                //System.out.println("Received solution: " + solution);
                System.out.printf("Received solution: %.2f\n", Double.parseDouble(solution));
                // Verify calculation
                String expected;
                try {
                    expected = check(equation.operand1, equation.operator1, equation.operand2, equation.operator2, equation.operand3).toString();
                } catch (Exception e) {
                    expected = "Invalid equation";
                }
                //System.out.println("Expected solution: " + expected + "\n");
                System.out.printf("Expected solution: %.2f\n\n", Double.parseDouble(expected));
                if (!solution.equals(expected)) {
                    System.out.println("Server response does not match expected solution. Terminating.");
                    outToServer.writeObject(new Message(Message.MessageType.DISCONNECT));
                    return;
                }
            }

            System.out.println("Done. Terminating connection to server.");
            outToServer.writeObject(new Message(Message.MessageType.DISCONNECT));
        } catch (EOFException e) {
            // Socket closed normally
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }

    private static Double check(Double v1, Character o1, Double v2, Character o2, Double v3) throws Exception {
        if ((v2 == 0.0 && (o1 == '/' || o1 == '%')) || (v3 == 0.0 && (o2.equals('/') || o2.equals('%')))) throw new Exception("INVALID");
        if (o1 == '*' || o1 == '/' || o1 == '%') return operate(operate(v1, v2, o1), v3, o2);
        else if (o2 == '*' || o2 == '/' || o2 == '%') return operate(v1, operate(v2, v3, o2), o1);
        else return operate(operate(v1, v2, o1), v3, o2);
    }

    private static Double operate(Double v1, Double v2, Character op) {
        if (op == '+') return v1 + v2;
        else if (op == '-') return v1 - v2;
        else if (op == '*') return v1 * v2;
        else if (op == '/') return v1 / v2;
        else return v1 % v2;
    }
}