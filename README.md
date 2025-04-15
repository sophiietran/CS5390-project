# CS5390 - Computer Networks Project
## Network Application - Math Server and Clients
### **Overview**
This project involves implementing a network application using Java Sockets. The application includes a centralized Math server and multiple clients that can request basic mathemtical calculations.

### **Server Application**
- Keeps track of all users (who, when, length of connection)
- Accepts multiple conenctions at one time
- Logs details about clients upon connection
- Processes math requests from clients in order received
- Closes connection with client when requested

### **Client Application**
- Connects to server with a given name and gets acknowledgement
- Sends basic math calculations
- Sends close connection request to end session
- Supports concurrent execution with other clients

### Additional Feature (For CS 5390 Students)
Implements a group chat between clients to discuss mathematical equations.
