import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // adresse du serveur
        int port = 1234; // port du serveur

        try (Socket socket = new Socket(serverAddress, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Demander le pseudo
            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine();
            out.println(pseudo); // envoyer au serveur

            // Thread pour écouter les messages entrants
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Déconnecté du serveur.");
                }
            }).start();

            // Boucle pour envoyer des messages
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Impossible de se connecter au serveur.");
            e.printStackTrace();
        }
    }
}
