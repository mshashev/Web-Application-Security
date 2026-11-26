
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(),
                             StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(),
                             StandardCharsets.UTF_8), true);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in,
                             StandardCharsets.UTF_8))) {

            System.out.println("Connected to server.");

            System.out.print("Enter nickname: ");
            writer.println("NICK " + console.readLine());

            String response = reader.readLine();
            System.out.println(response);

            if (response == null || !response.startsWith("OK")) return;

            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null)
                        System.out.println(message);
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            listener.setDaemon(true);
            listener.start();

            System.out.println("Type messages or /quit to exit.");

            String input;
            while ((input = console.readLine()) != null) {
                if (input.equalsIgnoreCase("/quit")) break;
                writer.println("MSG " + input);
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
