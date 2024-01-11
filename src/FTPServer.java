import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {
    public static void main(String[] args) {
        int portNumber = 2121; // Port FTP standard

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Serveur FTP en attente de connexions sur le port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connexion cliente.");

                // Créer un gestionnaire de client dans un nouveau thread
                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du serveur sur le port " + portNumber);
            e.printStackTrace();
        }
    }
}
