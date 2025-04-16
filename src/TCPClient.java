import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;


class TCPClient {
    static final int NUM_EQUATIONS_TO_SEND = 10;
    static final char[] OPERATORS = {'+', '-', '*', '/', '%'};

    public static void main(String[] argv) throws Exception {
        Random random = new Random();

        // Connect to server and send equation messages
        try (Socket clientSocket = new Socket("127.0.0.1", TCPServer.SERVER_PORT)) {
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client connected to server using socket " + clientSocket.getLocalPort());

            // Tell the server our name
            outToServer.writeObject(new Message(Message.MessageType.HELLO, "Client1"));
            String ack = inFromServer.readLine(); // Server will echo our name back to us, after which we can begin sending equations
            System.out.println("Server acknowledged our name: " + ack + "\n");

            //for (Message equation : equations) {
            for (int i = 0; i < NUM_EQUATIONS_TO_SEND; i++) {
                // Randomly generate equation
                Message equation = new Message( // TODO: for now only use positive values so python spits out the same answer as java for any modulo operation
                        //random.nextDouble() * 20 - 10,
                        random.nextDouble() * 10,
                        OPERATORS[random.nextInt(OPERATORS.length)],
                        //random.nextDouble() * 20 - 10,
                        random.nextDouble() * 10,
                        OPERATORS[random.nextInt(OPERATORS.length)],
                        //random.nextDouble() * 20 - 10
                        random.nextDouble() * 10
                );

                // Send equation to server
                System.out.println("Sending equation:  " + equation);
                outToServer.writeObject(equation);

                // Receive solution from server
                String solution = inFromServer.readLine();
                //System.out.println("Received solution: " + solution);
                System.out.printf("Received solution: %.4f\n", Double.parseDouble(solution));

                // TODO: get rid of this and just use python to test correctness
                // Verify calculation
                String expected;
                try {
                    expected = check(equation.operand1, equation.operator1, equation.operand2, equation.operator2, equation.operand3).toString();
                } catch (Exception e) {
                    expected = "Invalid equation";
                }
                //System.out.println("Expected solution: " + expected + "\n");
                System.out.printf("Expected solution: %.4f\n", Double.parseDouble(expected));

                // Run equation through Python for now to verify correctness
                Process p = Runtime.getRuntime().exec("C:/Users/User/AppData/Local/Programs/Python/Python313/python.exe -c print(f'{" + equation.toString2() + ":.4f}')");
                Scanner s = new Scanner(p.getInputStream(), StandardCharsets.UTF_8); System.out.println("Python's solution: " + s.nextLine()); s.close(); p.waitFor();
                System.out.println();

                if (!solution.equals(expected)) {
                    System.out.println("Server response does not match expected solution. Terminating.");
                    outToServer.writeObject(new Message(Message.MessageType.QUIT));
                    return;
                }
            }

            System.out.println("Done. Terminating connection to server.");
            outToServer.writeObject(new Message(Message.MessageType.QUIT));
        } catch (EOFException e) {
            // Socket closed normally
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }

    // TODO: get rid of this and just use python to test correctness
    private static Double check(Double v1, Character op1, Double v2, Character op2, Double v3) throws Exception {
        if ((v2 == 0.0 && (op1 == '/' || op1 == '%')) || (v3 == 0.0 && (op2 == '/' || op2 == '%'))) throw new Exception("INVALID");
        if (op1 == '*' || op1 == '/' || op1 == '%') return operate(operate(v1, v2, op1), v3, op2);
        else if (op2 == '*' || op2 == '/' || op2 == '%') return operate(v1, operate(v2, v3, op2), op1);
        else return operate(operate(v1, v2, op1), v3, op2);
    }

    // TODO: get rid of this and just use python to test correctness
    private static Double operate(Double v1, Double v2, Character op) {
        if (op == '+') return v1 + v2;
        else if (op == '-') return v1 - v2;
        else if (op == '*') return v1 * v2;
        else if (op == '/') return v1 / v2;
        else return v1 % v2;
    }
}