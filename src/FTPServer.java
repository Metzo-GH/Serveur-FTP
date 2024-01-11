import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {
    public static void main(String[] args) {
        int portNumber = 2121;

        try (
            ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Attente connexion sur " + portNumber);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connecté.");

                // Thread
                Thread clientHandler = new Thread(new ClientHandler(socket));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
    }
}
