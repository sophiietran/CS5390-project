import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


class TCPClient {
    public static void main(String[] argv) throws Exception {
        List<Message> equations = new ArrayList<>();
        equations.add(new Message(1.0, '+', 2.0, '+', 3.0));
        equations.add(new Message(1.0, '*', 2.0, '+', 3.0));
        equations.add(new Message(1.0, '+', 2.0, '*', 3.0));
        equations.add(new Message(1.0, '/', 2.0, '+', 3.0));
        equations.add(new Message(1.0, '+', 2.0, '/', 3.0));
        equations.add(new Message(4.0, '*', 2.0, '*', 3.0));

        // Connect to server and send equation messages
        try (Socket clientSocket = new Socket("127.0.0.1", TCPServer.SERVER_PORT)) {
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client connected to server using socket " + clientSocket.getLocalPort() + "\n");

            for (Message equation : equations) {
                System.out.println("Sending equation: " + equation);
                outToServer.writeObject(equation); // Send equation
                String solution = inFromServer.readLine(); // Receive solution
                System.out.println("Received solution: " + solution);
                Double expected = check(equation.operand1, equation.operator1, equation.operand2, equation.operator2, equation.operand3); // Verify
                System.out.println("Expected solution: " + expected + "\n");
                if (!solution.equals(expected.toString())) break;
            }

            System.out.println("Terminating connection to server");
            outToServer.writeObject(new Message(Message.MessageType.DISCONNECT));
        } catch (EOFException e) {
            // Socket closed normally
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }

    private static Double check(double v1, char o1, double v2, char o2, double v3) {
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