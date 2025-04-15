import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class TCPClient {
    public static final int TOTAL_EQUATIONS = 3;

    public static void main(String[] argv) throws Exception {
        // Establish connection to server
        Socket clientSocket = new Socket("127.0.0.1", TCPServer.SERVER_PORT);
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Client connected to server using socket " + clientSocket.getLocalPort() + "\n");

        // Equations to send
        List<Message> equations = new ArrayList<>();
        equations.add(new Message(1.0, Operator.ADD, 2.0, Operator.ADD, 3.0)); // 1+2+3 = 6
        equations.add(new Message(1.0, Operator.MUL, 2.0, Operator.ADD, 3.0)); // 1*2+3 = 5
        equations.add(new Message(1.0, Operator.ADD, 2.0, Operator.MUL, 3.0)); // 1+2*3 = 7

        // Send each equation and receive each solution
        for (Message equation : equations) {
            System.out.println("Sending equation: " + equation);
            outToServer.writeObject(equation);
            System.out.println("Received solution: " + inFromServer.readLine() + "\n");
        }

        clientSocket.close();
    }
}