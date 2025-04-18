import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;



class TCPClient {
    static final int NUM_EQUATIONS_TO_SEND = 5; // i changed it to 5 so i can see the flow
    static final char[] OPERATORS = {'+', '-', '*', '/', '%'};

    public static void main(String[] argv) throws Exception {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in); // to get user name

        // Connect to server and send equation messages
        try (Socket clientSocket = new Socket("127.0.0.1", TCPServer.SERVER_PORT)) {
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client connected to server using socket " + clientSocket.getLocalPort());

            // Tell the server our name, wait for reply
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            outToServer.writeObject(new Message(Message.MessageType.HELLO, username));
            String ack = inFromServer.readLine();
            System.out.println("Server acknowledged our name: " + ack + "\n");

            for (int i = 0; i < NUM_EQUATIONS_TO_SEND; i++) {
                // randomly wait a little bit
                int waitMillis = 1000 + random.nextInt(4000); // 1-5 seconds
                Thread.sleep(waitMillis);
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
                System.out.println("Received solution: " + solution);

                /* // Run equation through Python for now to verify correctness
                Process p = Runtime.getRuntime().exec("C:/Users/User/AppData/Local/Programs/Python/Python313/python.exe -c print(f'{" + equation.toString2() + "}')");
                Scanner s = new Scanner(p.getInputStream(), StandardCharsets.UTF_8);
                String expected = s.nextLine();
                System.out.println("Python's solution: " + expected);
                s.close();
                p.waitFor();
                System.out.println();
                if (!solution.equals(expected)) {
                    System.out.println("Server response does not match expected solution. Terminating.");
                    outToServer.writeObject(new Message(Message.MessageType.QUIT));
                    return;
                } */
            }

            System.out.println("Done. Terminating connection to server.");
            outToServer.writeObject(new Message(Message.MessageType.QUIT));
        } catch (EOFException e) {
            // Socket closed normally
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }
}
