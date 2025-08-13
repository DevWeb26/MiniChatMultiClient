import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Serveur de chat démarré sur le port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket);
                ClientHandler handler = new ClientHandler(clientSocket, clients);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private String pseudo;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Lecture du pseudo à la connexion
            pseudo = in.readLine();
            broadcast("🔵 " + pseudo + " a rejoint le chat");

            String message;
            while ((message = in.readLine()) != null) {
                broadcast(pseudo + ": " + message);
            }

        } catch (IOException e) {
            System.out.println("❌ Déconnexion de " + pseudo);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
            broadcast("🔴 " + pseudo + " a quitté le chat");
        }
    }

    private void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c != this) {
                    c.out.println(message);
                }
            }
        }
    }
}
