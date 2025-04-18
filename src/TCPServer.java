import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.time.format.DateTimeFormatter;


class TCPServer {
    public static final int SERVER_PORT = 9001;
    private static final Map<String, ClientSession> clientLog = new ConcurrentHashMap<>(); // logs client sessions
    private static final BlockingQueue<CalcRequest> requestQueue = new LinkedBlockingQueue<>(); // queue to store incoming CALC requests

    public static void main(String[] argv) throws Exception {

        // Await and accept connection
        try (ServerSocket welcomeSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server listening for connections on port " + welcomeSocket.getLocalPort());
            
            // start a worker thread to watch request queue and process calculations
            new Thread(new CalcWorker()).start();

            // thread to accept clients continuously
            while(true){
                Socket connectionSocket = welcomeSocket.accept();
                new Thread(new ClientHandler(connectionSocket)).start(); //Starts new thread for each client connected
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

    // Info for client logging
    static class ClientSession {
        String name;
        LocalDateTime connectTime;
        BufferedWriter output;
        
        public ClientSession(String name, LocalDateTime connectTime, BufferedWriter out){
            this.name = name;
            this.connectTime = connectTime;
            this.output = out;
        }

        public void send(String response){
            try{
                output.write(response + "\n");
                output.flush();
            }catch(IOException e){
                System.err.println("Response failed to send to client: " + e.getMessage());
            }
        }
    }

    // Represents a math request from client
    static class CalcRequest{
        String name;
        Message equation;

        CalcRequest(String name, Message equation){
            this.name = name;
            this.equation = equation;
        }
    }

    // Handles all math requests in FIFO order
    static class CalcWorker implements Runnable{
        public void run(){
            while(true){
                try{
                    CalcRequest request = requestQueue.take(); // blocking queue will wait until there is something in the queue
                    Message eq = request.equation;
                    String solution;

                    if(isValidOperation(eq.operator1, eq.operand2, eq.operator2, eq.operand3))
                        solution = String.format("%.4f", calculate(eq)); // four values after decimal point, can change later
                    else
                        solution = "Invalid equation";

                    // gets proper client to send to
                    ClientSession session = clientLog.get((request.name));
                    if(session != null){ 
                        session.send(solution);
                        System.out.println("Processed for " + request.name + ": " + eq + " = " + solution);
                    }
                }catch(Exception e){
                    System.err.println("Error in CalcWorker(): " + e.getMessage());
                }
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String clientName;
        private LocalDateTime connectTime;
        private AtomicBoolean connected = new AtomicBoolean(true); //atomic booleans are thread-safe booleans (this is new to me)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss"); 
    
        public ClientHandler(Socket socket){
            this.socket = socket;
        }
    
        public void run(){
            try{
                ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
                BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    
                // First message client will send is to tell us their name. Echo it back to them as acknowledgment.
                Message hello = (Message) inFromClient.readObject();
                clientName = hello.getClientName();
                connectTime = LocalDateTime.now();

                outToClient.write(clientName + '\n');
                outToClient.flush();

                // Log connection
                clientLog.put(clientName, new ClientSession(clientName, connectTime, outToClient));
                System.out.println("User: " + clientName + " connected at " + connectTime.format(formatter));

                // Handle incoming messages
                while (connected.get()) {
                    Message equation = (Message) inFromClient.readObject();

                    if (equation.getMessageType() == Message.MessageType.QUIT) {
                        disconnect();
                        break;
                    } else if (equation.getMessageType() == Message.MessageType.CALC) {
                        System.out.println("Received equation from " + clientName + ": " + equation);
                        requestQueue.put(new CalcRequest(clientName, equation));
                    } else{
                        System.out.println("Unknown message type from " + clientName);
                    }
                        
                }
            }catch (Exception e) {
                System.out.println("Error with client " + clientName + ": " + e.getMessage());
            }
        }
        public void disconnect(){
            connected.set(false);
            LocalDateTime disconnectTime = LocalDateTime.now();
            Duration duration = Duration.between(connectTime, disconnectTime);

            clientLog.remove(clientName);
            System.out.println("[" + clientName + " disconnected at " + disconnectTime.format(formatter) + " after " + duration.toSeconds() + " seconds.]");

            try {
                socket.close();
            } catch (Exception e) {
                
            }
            
        }

    }
}
       
