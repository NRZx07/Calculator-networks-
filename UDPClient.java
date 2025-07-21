import java.net.*;
import java.io.*;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private byte[] buffer;

    public UDPClient(String serverHost, int serverPort) throws SocketException, UnknownHostException {
        // Create a DatagramSocket with an automatically assigned port
        // By default, this binds to all interfaces (0.0.0.0) on a random port
        // For production environments where security is important, you might want to bind to a specific interface:
        // socket = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"));
        socket = new DatagramSocket();

        // Resolve the server hostname to an IP address
        // For learning purposes, we recommend using "localhost" or "127.0.0.1"
        // This keeps all traffic local to your machine
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;

        System.out.println("UDP Client ready to send to " + serverHost +
                " (" + this.serverAddress.getHostAddress() + "):" + serverPort);

        // If connecting to localhost, inform the user
        if (this.serverAddress.isLoopbackAddress()) {
            System.out.println("NOTE: Connecting to localhost. This is perfect for learning!");
        } else {
            System.out.println("WARNING: Connecting to external host. Make sure firewall allows UDP on port " + serverPort);
        }
    }

    public void sendMessage(String message) throws IOException {
        // Convert the message to bytes
        buffer = message.getBytes();

        // Create a packet with the message, server address, and port
        DatagramPacket packet = new DatagramPacket(
                buffer,
                buffer.length,
                serverAddress,
                serverPort
        );

        // Send the packet
        socket.send(packet);
        System.out.println("Sent: " + message);

        // Prepare to receive response
        buffer = new byte[256];
        packet = new DatagramPacket(buffer, buffer.length);

        // Set a timeout for receiving response (optional but recommended)
        socket.setSoTimeout(5000); // 5 second timeout

        try {
            // Wait for response
            socket.receive(packet);

            // Extract and display the response
            String response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received: " + response);
        } catch (SocketTimeoutException e) {
            System.out.println("No response received (timeout)");
            if (!serverAddress.isLoopbackAddress()) {
                System.out.println("TIP: If connecting to external server, check firewall settings.");
            }
        }
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        // Default to localhost for safe learning environment
        String serverHost = "localhost";
        int serverPort = 5000;

        // Parse command line arguments
        if (args.length >= 1) {
            serverHost = args[0];
        }
        if (args.length >= 2) {
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 5000.");
            }
        }

        System.out.println("\n=== UDP Echo Client ===");
        System.out.println("For learning, it's recommended to use 'localhost' or '127.0.0.1'");
        System.out.println("To connect to external servers, use their IP address or hostname");
        System.out.println("Current target: " + serverHost + ":" + serverPort + "\n");

        try {
            UDPClient client = new UDPClient(serverHost, serverPort);
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter messages to send (type 'quit' to exit):");

            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();

                if ("quit".equalsIgnoreCase(message)) {
                    break;
                }

                try {
                    client.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }

            scanner.close();
            client.close();
            System.out.println("Client shutdown.");

        } catch (SocketException e) {
            System.err.println("Could not create client: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
            System.err.println("TIP: Use 'localhost' or '127.0.0.1' for local testing.");
        }
    }
}
