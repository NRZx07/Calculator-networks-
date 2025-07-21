import java.net.*;
import java.io.*;


public class Calculator {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];

    private double evaluateExpression(String input) throws Exception {
        input = input.trim();
        String[] parts = input.split(" ");
        if (parts.length != 3)
            throw new Exception("Invalid format");

        double a = Double.parseDouble(parts[0]);
        String op = parts[1];
        double b = Double.parseDouble(parts[2]);

        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Divide by zero");
                return a / b;
            default: throw new Exception("Unknown operator");
        }
    }


    public Calculator(int port) throws SocketException, UnknownHostException {
        // IMPORTANT: Binding to localhost (127.0.0.1) for security and learning purposes
        // This means the server will only accept connections from the same machine
        // For production use or to accept connections from other machines, you would use:
        // socket = new DatagramSocket(port); // This binds to all interfaces (0.0.0.0)
        // or
        // socket = new DatagramSocket(port, InetAddress.getByName("your.public.ip"));

        // For learning and experimentation, localhost is recommended as it:
        // 1. Doesn't require firewall configuration
        // 2. Is more secure (not exposed to the network)
        // 3. Works even without internet connection
        InetAddress localhost = InetAddress.getByName("127.0.0.1");
        socket = new DatagramSocket(port, localhost);

        System.out.println("UDP Server started on " + localhost.getHostAddress() + ":" + port);
        System.out.println("NOTE: Server is bound to localhost only. Connections from other machines will be refused.");
    }

    public void run() {
        running = true;

        while (running) {
            try {
                // Create a DatagramPacket to receive data
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive a packet (this method blocks until a packet arrives)
                socket.receive(packet);

                // Extract information from the received packet
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String received = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Received from " + clientAddress + ":" + clientPort + " - " + received);

                // Prepare response
                String response;
                try {
                    response = "Result: " + evaluateExpression(received);
                } catch (Exception e) {
                    response = "Error: Invalid expression";
                }

                byte[] responseData = response.getBytes();

                // Create a packet with the response data, client address, and port
                DatagramPacket responsePacket = new DatagramPacket(
                        responseData,
                        responseData.length,
                        clientAddress,
                        clientPort
                );

                // Send the response back to the client
                socket.send(responsePacket);

                // Clear the buffer for the next message
                buffer = new byte[256];

            } catch (IOException e) {
                System.err.println("Error in server: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        int port = 5000; // Default port

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 5000.");
            }
        }

        try {
            Calculator server = new Calculator(port);

            System.out.println("\n=== UDP Echo Server ===");
            System.out.println("Server is running on localhost only for security.");
            System.out.println("Clients must connect to 127.0.0.1:" + port);
            System.out.println("Press Ctrl+C to stop the server.\n");

            server.run();
        } catch (SocketException e) {
            System.err.println("Could not start server: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Could not bind to localhost: " + e.getMessage());
        }
    }
}