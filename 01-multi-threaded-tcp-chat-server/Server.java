
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 8080;
    private static final Set<ClientHandler> clients =
            ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Listening on port " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: "
                        + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler handler) {
        clients.remove(handler);
        System.out.println("Client disconnected.");
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Socket s = socket;
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(s.getInputStream(),
                                 StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(
                         new OutputStreamWriter(s.getOutputStream(),
                                 StandardCharsets.UTF_8), true)) {

                reader = in;
                writer = out;
                readRequest();

            } catch (IOException e) {
                System.out.println("Client connection closed.");
            } finally {
                Server.removeClient(this);
            }
        }

        private void readRequest() throws IOException {
            String line;
            while ((line = reader.readLine()) != null) {
                handleCommand(line);
            }
        }

        private void handleCommand(String command) {
            if (command.startsWith("NICK ")) {
                String name = command.substring(5).trim();

                boolean taken = clients.stream().anyMatch(c ->
                        c != this && c.nickname != null
                                && c.nickname.equalsIgnoreCase(name));

                if (nickname != null) {
                    writeResponse("ERROR Nickname already set");
                } else if (name.isEmpty() || name.contains(" ")) {
                    writeResponse("ERROR Invalid nickname");
                } else if (taken) {
                    writeResponse("ERROR Nickname already taken");
                } else {
                    nickname = name;
                    writeResponse("OK Nickname set");
                }

            } else if (command.startsWith("MSG ")) {
                String text = command.substring(4).trim();

                if (nickname == null) {
                    writeResponse("ERROR Set nickname first");
                } else if (text.isEmpty()) {
                    writeResponse("ERROR Message cannot be empty");
                } else {
                    writeResponse("OK Message sent");
                    System.out.println(nickname + ": " + text);
                    Server.broadcast("MSG " + nickname + ": " + text, this);
                }

            } else {
                writeResponse("ERROR Unknown command");
            }
        }

        private synchronized void writeResponse(String response) {
            if (writer != null) {
                writer.println(response);
            }
        }

        public void sendMessage(String message) {
            writeResponse(message);
        }
    }
}

